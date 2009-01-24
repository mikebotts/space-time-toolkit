/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit Cache Engine".
  
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2008
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.stt.provider.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.vast.cdm.common.BinaryEncoding;
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;
import org.vast.data.AbstractDataBlock;
import org.vast.stt.data.BlockList;
import org.vast.stt.data.BlockListIterator;
import org.vast.stt.data.DataException;
import org.vast.stt.event.EventType;
import org.vast.stt.event.STTEvent;
import org.vast.stt.provider.AbstractProvider;
import org.vast.stt.provider.CachedProvider;
import org.vast.stt.provider.DataProvider;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.provider.STTTimeExtent;
import org.vast.sweCommon.BinaryDataParser;
import org.vast.sweCommon.BinaryDataWriter;
import org.vast.util.DateTimeFormat;
import com.sleepycat.je.*;


/**
 * <p><b>Title:</b>
 * TimeCache
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO TimeCache type description
 * 
 * <ul>
 * <li>Each group of duplicate records contains data for a tile</li>
 * <li>The record key is the julian time value for the start of the tile (big endian)</li>
 * <li>The record data is a list of datablocks serialized using the BinaryDataWriter (big endian),
 *     prefixed by the block list index (on one byte)</li>
 * <li>Tile data form multiple block lists are stored as duplicates</li>
 * </ul>
 * 
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin
 * @date Nov 21, 2008
 * @version 1.0
 */
public class TimeCache extends AbstractProvider implements CachedProvider
{
    protected String timeData;
    protected double tileSize; // in seconds
    protected int maxCacheSize; // in MB
    protected int maxLifeTime; // in minutes
    protected DataProvider subProvider;
    protected Database cacheDB;
    protected BinaryDataParser[] parsers;
    protected BinaryDataWriter[] writers;
    protected DataComponent timeDataComponent;
    protected int timeListIndex;
    protected boolean needsClear;
    
    
    public TimeCache()
    {
        super();
        //this.dataNode = new DataNodeBDB();
    }
    
    
    @Override
    public void init() throws DataException
    {
        try
        {
            // setup databases
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            //envConfig.setSharedCache(true);
            String dbDir = System.getProperty("user.home");
            File dbFile = new File(dbDir + "/stt/cache");
            dbFile.mkdirs();
            Environment env = new Environment(dbFile, envConfig);
            
            // Open the database. Create it if it does not exist already
            String dbName = subProvider.getName().replace(" " , "_");
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            dbConfig.setSortedDuplicates(true);
            dbConfig.setDuplicateComparator(new DuplicateComparator());
            cacheDB = env.openDatabase(null, dbName + "_DATA", dbConfig);
            
            // init sub provider and get block list array
            subProvider.init();
            List<BlockList> blockListArray = subProvider.getDataNode().getListArray();
            int numLists = blockListArray.size();
            
            // create array lists
            parsers = new BinaryDataParser[numLists];
            writers = new BinaryDataWriter[numLists];
                        
            // initialize node and binary parsers/writers
            // find time component in one of the list
            String [] timeDataPath = timeData.split("/");
            for (int i=0; i<blockListArray.size(); i++)
            {
                BlockList blockList = blockListArray.get(i);
                
                // copy output structure from sub provider
                DataComponent dataComponents = blockList.getBlockStructure();
                dataNode.createList(dataComponents.copy());
                
                // make another copy for the parser/writer
                dataComponents = dataComponents.copy();
                
                // init parser
                BinaryDataParser dataParser = new BinaryDataParser();
                dataParser.setDataEncoding(BinaryEncoding.getDefaultEncoding(dataComponents));
                dataParser.setDataComponents(dataComponents);
                parsers[i] = dataParser;
                
                // init writer
                BinaryDataWriter dataWriter = new BinaryDataWriter();
                dataWriter.setDataEncoding(BinaryEncoding.getDefaultEncoding(dataComponents));
                dataWriter.setDataComponents(dataComponents);
                writers[i] = dataWriter;
                
                // find time data component
                if (dataComponents.getName().equals(timeDataPath[0]))
                {
                    timeDataComponent = dataComponents;
                    
                    for (int j=1; j<timeDataPath.length; j++)
                    {
                        timeDataComponent = timeDataComponent.getComponent(timeDataPath[j]);                    
                        if (timeDataComponent == null)
                            throw new DataException("Unknown time component: " + timeData);
                    }
                    
                    timeListIndex = i;
                }
            }
            
            dataNode.setNodeStructureReady(true);
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }        
    }
    
    
    protected byte[] encodeKey(double julianTime)
    {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong((long)julianTime*1000);
        return buf.array();
    }
    
    
    protected double decodeKey(byte[] bytes)
    {
         ByteBuffer buf = ByteBuffer.wrap(bytes);
         return ((double)buf.getLong()) / 1000.0;
    }
    

    @Override
    public void updateData() throws DataException
    {
        // adjust incoming TimeExtent to full tiles
        double start = this.timeExtent.getAdjustedLagTime();
        start = ((int)(start / tileSize)) * tileSize;
        
        double stop = this.timeExtent.getAdjustedLeadTime();
        stop = ((int)(stop / tileSize + 1)) * tileSize;
                
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        Cursor dbCursor = null;
        needsClear = true;
        
        try
        {
            double tileTime;
            double request_begin = start;
            double request_end = start;
            OperationStatus status;
            
            dbCursor = cacheDB.openCursor(null, null);
            key.setData(encodeKey(start));
            
            // get tiles data from DB or sub-provider if not in cache
            boolean doSearch = true;
            while (request_begin < stop)
            {
                // first search for closest record (w/ key greater or equal to desired key)
                // then call next non duplicate to iterate through DB records
                if (doSearch)
                {
                    status = dbCursor.getSearchKeyRange(key, data, null);
                    doSearch = false;
                }
                else
                    status = dbCursor.getNextNoDup(key, data, null);
                
                // if no record found, set to +INF so that we request everything left
                if (status == OperationStatus.SUCCESS)
                    tileTime = decodeKey(key.getData());
                else
                    tileTime = Double.POSITIVE_INFINITY;
                
                // if we have tile, just read from DB
                if (tileTime == request_begin)
                {
                    System.out.println("Found tile in DB @ " + DateTimeFormat.formatIso(decodeKey(key.getData()), 0));
                    request_begin += tileSize;
                    readBlocksFromDB(dbCursor, key, data);
                }
                
                // otherwise load data from data provider
                else
                {
                    request_end = Math.min(tileTime, stop);
                    
                    System.out.println("Loading Data for " + DateTimeFormat.formatIso(request_begin, 0) +
                                                     " - " + DateTimeFormat.formatIso(request_end, 0));
                    
                    // get data for missing tiles
                    subProvider.clearData();
                    subProvider.getTimeExtent().setStartTime(request_begin);
                    subProvider.getTimeExtent().setStopTime(request_end);
                    subProvider.updateData();
                    
                    // put result in DB (serialize data for each tile to binary)
                    writeBlocksToDB(dbCursor, request_begin, request_end);                    
                    request_begin = request_end;
                }
            }
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
        catch (CDMException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                System.out.println("Closing cursor");
                
                if (dbCursor != null)
                    dbCursor.close();
            }
            catch (DatabaseException e)
            {
                e.printStackTrace();
            }
        }
    }
        
        
    protected void writeBlocksToDB(Cursor dbCursor, double firstTile, double lastTile) throws CDMException
    {
        double endTile = firstTile;
        boolean isFirstTile = true;
        
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        
        ArrayList<BlockList> blockLists = dataNode.getListArray();
        ArrayList<BlockList> subProviderBlockLists = subProvider.getDataNode().getListArray();
        ByteArrayOutputStream[] outputStreams = new ByteArrayOutputStream[blockLists.size()];
        
        // re-init iterators                    
        ArrayList<BlockListIterator> blockIterators = new ArrayList<BlockListIterator>(subProviderBlockLists.size());                   
        for (BlockList blockList: subProviderBlockLists)
        {
            BlockListIterator it = blockList.getIterator();
            blockIterators.add(it);
        }
        
        System.out.println("*** Writing tiles to DB ***");
        
        // write sub-provider data to DB in tiles
        BlockListIterator it = blockIterators.get(timeListIndex);
        while (it.hasNext())
        {
            // load next block from block list containing time
            BinaryDataWriter dataWriter = writers[timeListIndex];
            AbstractDataBlock nextBlock = it.next().getData();
            dataWriter.getDataComponents().setData(nextBlock);
            
            // extract block time
            double nextBlockTime = timeDataComponent.getData().getDoubleValue();
            
            // if next block is part of next tile
            if (nextBlockTime >= endTile)
            {
                if (!isFirstTile)
                {
                    key.setData(encodeKey(endTile - tileSize));
                    System.out.println("Writing tile @ " + DateTimeFormat.formatIso(endTile - tileSize, 0));
                    
                    for (int i=0; i<subProviderBlockLists.size(); i++)
                    {
                        // insert each record in DB
                        try
                        {
                            writers[i].flush();
                            data.setData(outputStreams[i].toByteArray());
                            OperationStatus status = dbCursor.put(key, data);
                            System.out.println("  List #" + i + ": " + status + ": " + data.getData().length + " bytes written");
                        }
                        catch (DatabaseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    
                    dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
                }
                
                // init writers
                for (int i=0; i<subProviderBlockLists.size(); i++)
                {
                    if (!isFirstTile)
                        writers[i].close();
                    
                    ByteArrayOutputStream os = new ByteArrayOutputStream((int)tileSize*1000);
                    os.write(i); // write list index first for proper duplicate sorting!                    
                    outputStreams[i] = os;
                    writers[i].setOutput(os);
                    writers[i].reset();
                }
                
                endTile += tileSize;
                isFirstTile = false;
            }
            
            System.out.println("Block @ " + DateTimeFormat.formatIso(nextBlockTime, 0));
            
            // serialize blocks to binary
            for (int i=0; i<subProviderBlockLists.size(); i++)
            {
                dataWriter = writers[i];
                BlockListIterator it2 = blockIterators.get(i);
                
                AbstractDataBlock block;
                if (i == timeListIndex)
                    block = nextBlock;
                else
                    block = it2.next().getData();
                
                dataWriter.write(block);
                
                // also add to this data node if in range
                if (nextBlockTime >= timeExtent.getStartTime() && nextBlockTime <= timeExtent.getStopTime())
                {
                    if (needsClear)
                    {
                        dataNode.clearAll();
                        needsClear = false;
                    }
                    
                    blockLists.get(i).addBlock(block);
                }
            }
        }
    }
    
    
    protected void readBlocksFromDB(Cursor dbCursor, DatabaseEntry key, DatabaseEntry data) throws CDMException, DatabaseException
    {
        ArrayList<BlockList> blockLists = dataNode.getListArray();
        ByteArrayInputStream[] inputStreams = new ByteArrayInputStream[blockLists.size()];
        double tileTime = decodeKey(key.getData()); 
        
        System.out.println("Loading Tile from DB @ " + DateTimeFormat.formatIso(tileTime, 0));
        
        // init parsers
        OperationStatus status = OperationStatus.SUCCESS;
        for (int i=0; i<inputStreams.length; i++)
        {
            ByteArrayInputStream is = new ByteArrayInputStream(data.getData());
            is.skip(1); // skip list index
            inputStreams[i] = is;
            parsers[i].setInput(is);
            parsers[i].reset();
            
            System.out.println("  List #" + i + ": " + status + ": " + data.getData().length + " bytes read");
            
            if (i < inputStreams.length-1)
                status = dbCursor.getNextDup(key, data, null);
            
            if (status != OperationStatus.SUCCESS)
            {
                System.out.println("  List #" + i + ": " + status);
                return;
            }
        }
        
        // read blocks and copy them to all block lists
        while (parsers[0].moreData())
        {
            double blockTime = 0.0;
            
            for (int i=0; i<blockLists.size(); i++)
            {
                BinaryDataParser parser = parsers[i];
                AbstractDataBlock block = (AbstractDataBlock)parser.parse();
                
                if (needsClear)
                {
                    dataNode.clearAll();
                    needsClear = false;
                }
                
                blockLists.get(i).addBlock(block);
                
                if (i == timeListIndex)
                    blockTime = timeDataComponent.getData().getDoubleValue();
            }
            
            System.out.println("Block @ " + DateTimeFormat.formatIso(blockTime, 0));
        }
        
        dispatchEvent(new STTEvent(this, EventType.PROVIDER_DATA_CHANGED));
        System.out.println("Tile Loaded\n");
    }
       
    
    public String getTimeData()
    {
        return timeData;
    }


    public void setTimeData(String timeData)
    {
        this.timeData = timeData;
    }


    public double getTileSize()
    {
        return tileSize;
    }


    public void setTileSize(double tileSize)
    {
        this.tileSize = tileSize;
    }


    public int getMaxCacheSize()
    {
        return maxCacheSize;
    }


    public void setMaxCacheSize(int maxCacheSize)
    {
        this.maxCacheSize = maxCacheSize;
    }


    public int getMaxLifeTime()
    {
        return maxLifeTime;
    }


    public void setMaxLifeTime(int maxLifeTime)
    {
        this.maxLifeTime = maxLifeTime;
    }
    
    
    @Override
    public String getDescription()
    {
        return subProvider.getDescription();
    }


    @Override
    public String getName()
    {
        return subProvider.getName();
    }


    @Override
    public void setDescription(String description)
    {
        subProvider.setDescription(description);
    }


    @Override
    public void setName(String name)
    {
        subProvider.setName(name);
    }


    @Override
    public boolean isSpatialSubsetSupported()
    {
        return subProvider.isSpatialSubsetSupported();
    }
    

    @Override
    public boolean isTimeSubsetSupported()
    {
        return subProvider.isTimeSubsetSupported();
    }
    
    
    @Override
    public STTSpatialExtent getMaxSpatialExtent()
    {
        return subProvider.getMaxSpatialExtent();
    }


    @Override
    public STTTimeExtent getMaxTimeExtent()
    {
        return subProvider.getMaxTimeExtent();
    }


    public DataProvider getSubProvider()
    {
        return subProvider;
    }


    public void setSubProvider(DataProvider subProvider)
    {
        if (this.subProvider != subProvider)
        {
            if (this.subProvider != null)
                this.subProvider.removeListener(this);
            
            this.subProvider = subProvider;
            
            if (this.subProvider != null)
                this.subProvider.addListener(this);
        }
    }
    
    
    @Override
    public void handleEvent(STTEvent event)
    {
        switch(event.type)
        {
            case PROVIDER_DATA_CHANGED:
            case PROVIDER_DATA_CLEARED:
            case PROVIDER_DATA_REMOVED:
                //dispatchEvent(event.copy());
                break;
                
            case PROVIDER_DATA_ADDED:
                //dispatchEvent(event.copy());
                break;
                
            case PROVIDER_ERROR:
                this.enabled = false;
                dispatchEvent(event.copy());
                break;
                
            case TIME_EXTENT_CHANGED:
                if (this.isTimeSubsetSupported() && isEnabled())
                {
                    startUpdate(true);
                    break;
                }
        }        
    }  
}
