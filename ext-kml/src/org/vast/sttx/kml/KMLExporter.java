/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "Space Time Toolkit".
 
 The Initial Developer of the Original Code is the VAST team at the
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.
 
 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.sttx.kml;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.stream.XMLStreamException;
import org.vast.ows.sld.GridSymbolizer;
import org.vast.ows.sld.Symbolizer;
import org.vast.ows.sld.TextureSymbolizer;
import org.vast.stt.project.scene.SceneItem;
import org.vast.stt.project.tree.DataItem;
import org.vast.stt.project.world.Projection;
import org.vast.stt.project.world.Projection_LLA;
//import org.vast.stt.project.world.Projection_LLA;
import org.vast.stt.project.world.WorldScene;
import org.vast.stt.provider.STTSpatialExtent;
import org.vast.stt.style.*;
import org.vast.stt.style.IconManager.Icon;


/**
 * <p><b>Title:</b>
 * KML Exporter
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO KMLExporter type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 1, 2007
 * @version 1.0
 */
public class KMLExporter implements StylerVisitor
{
    protected final static double R2D = 180.0/Math.PI;
        
    protected KMLStreamWriter21 kmlWriter;
    protected COLLADAStreamWriter14 colaWriter;
    protected COLLADATextureManager textureManager;
    protected boolean outputKmz;
    protected OutputStream output;
    protected int modelNum = 1;
    protected Hashtable<DataStyler, Integer> modelTable;


    public KMLExporter(String outputFile, boolean outputKmz) throws FileNotFoundException
    {
        this(new BufferedOutputStream(new FileOutputStream(outputFile)), outputKmz);
    }
    
    
    public KMLExporter(OutputStream output, boolean outputKmz)
    {
        try
        {
            this.outputKmz = outputKmz;
            this.modelTable = new Hashtable<DataStyler, Integer>();
            
            if (outputKmz)
            {
                ZipOutputStream kmzOutput = new ZipOutputStream(output);
                this.output = kmzOutput;                
                kmzOutput.setMethod(ZipOutputStream.DEFLATED);
                ZipEntry kmlEntry = new ZipEntry("main.kml");
                kmzOutput.putNextEntry(kmlEntry);
                kmlWriter = new KMLStreamWriter21(kmzOutput);
                colaWriter = new COLLADAStreamWriter14(kmzOutput);
                textureManager = new COLLADATextureManager();
            }
            else
            {
                this.output = output;
                kmlWriter = new KMLStreamWriter21(output);
            }            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void resetAltitudeDamping(WorldScene scene)
    {
        // make sure altitude damping is set to 1.0
        ((Projection_LLA)scene.getViewSettings().getProjection()).setAltitudeDamping(1.0);
        ((Projection_LLA)scene.getViewSettings().getProjection()).setInsertBreaks(false);
    }
    
    
    public void exportScene(WorldScene scene)
    {
        try
        {
            modelTable.clear();            
            kmlWriter.startDocument();         
            
            // export all items
            List<SceneItem> sceneItems = scene.getSceneItems();
            for (int i = 0; i < sceneItems.size(); i++)
            {
                SceneItem sceneItem = sceneItems.get(i);
                if (!sceneItem.isVisible())
                    continue;
                exportItemFolder(sceneItem);
            }
            
            kmlWriter.endDocument();
            
            // if KMZ was selected, loop through all items again
            // to generate collada and texture files
            if (outputKmz)
            {
                for (int i = 0; i < sceneItems.size(); i++)
                {
                    SceneItem sceneItem = sceneItems.get(i);
                    if (!sceneItem.isVisible())
                        continue;
                    exportModels(sceneItem);
                }
            }
            
            output.close();
        }
        catch (XMLStreamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public void exportItemFolder(SceneItem sceneItem)
    {
        try
        {
            DataItem currentItem = sceneItem.getDataItem();
            kmlWriter.startFolder(currentItem.getName(), currentItem.getDescription(), sceneItem.isVisible());
            exportItem(sceneItem);
            kmlWriter.endFolder();
        }
        catch (XMLStreamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public void exportItem(SceneItem sceneItem)
    {
        try
        {
            // output data from all stylers
            for (int s = 0; s < sceneItem.getStylers().size(); s++)
            {
                DataStyler nextStyler = sceneItem.getStylers().get(s);
                Symbolizer sym = nextStyler.getSymbolizer();
                kmlWriter.startFolder(sym.getName(), null, sym.isEnabled());
                nextStyler.accept(this);
                kmlWriter.endFolder();
            }
        }
        catch (XMLStreamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public void exportModels(SceneItem sceneItem)
    {
        // loop through all stylers for this item
        for (int s = 0; s < sceneItem.getStylers().size(); s++)
        {
            DataStyler nextStyler = sceneItem.getStylers().get(s);
            Symbolizer sym = nextStyler.getSymbolizer();
            
            // do only these stylers
            if (sym instanceof GridSymbolizer || sym instanceof TextureSymbolizer)
                nextStyler.accept(this);
        }
    }


    public void visit(PointStyler styler)
    {
        PointGraphic ps = new PointGraphic();
        int groupCount = 1;
        boolean first = true;
        
        try
        {
            boolean relativeToGround = useRelativeToGround(styler);
            styler.resetIterators();
                        
            while (styler.nextBlock() != null)
            {
                PointGraphic p;
                while ((p = styler.nextPoint()) != null)
                {
                    // if style has changed start a new PlaceMark
                    if (first || p.r != ps.r || p.g != ps.g || p.b != ps.b || p.a != ps.a || p.size != ps.size ||
                                 p.orientation != ps.orientation || p.iconId != ps.iconId)
                    {
                        // only if this is not the 1st point
                        if (!first)
                        {
                            kmlWriter.endMultiGeometry();
                            kmlWriter.endPlacemark();
                        }
                        
                        kmlWriter.startPlacemark("G" + Integer.toString(groupCount), null, true);
                        
                        // default img if no icon specified
                        String iconUrl = null;
                        Icon icon = IconManager.getInstance().getIcon(p.iconId);
                        if (icon != null)
                            iconUrl = icon.url;
                        else
                        {
                            iconUrl = "D:/Projects/NSSTC/STT3-KMLAddOn/icons/itemVis.png";
                            p.size /= 6.0f;
                        }
                        
                        kmlWriter.writeIconStyle(p.r, p.g, p.b, p.a, p.size, p.orientation, iconUrl, true);
                        kmlWriter.startMultiGeometry();
                        groupCount++;
                        
                        first = false;
                    }
                    
                    // write next line point coordinates
                    p.x *= R2D;
                    p.y *= R2D;
                    kmlWriter.writePoint(p.x, p.y, p.z, relativeToGround);
                    
                    // save point for reuse
                    p.copy(ps);
                }
            }
            
            kmlWriter.endMultiGeometry();
            kmlWriter.endPlacemark();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }


    public void visit(LineStyler styler)
    {
        LinePointGraphic ps = new LinePointGraphic();
        int groupCount = 1;
        boolean first = true;
        
        try
        {
            boolean relativeToGround = useRelativeToGround(styler);
            styler.resetIterators();
            
            while (styler.nextLineBlock() != null)
            {
                LinePointGraphic p;
                while ((p = styler.nextPoint()) != null)
                {
                    // if style has changed start a new PlaceMark
                    if (first || p.r != ps.r || p.g != ps.g || p.b != ps.b || p.a != ps.a || p.width != ps.width)
                    {
                        // only if this is not the 1st point
                        if (!first)
                        {
                            kmlWriter.endLineString();
                            kmlWriter.endMultiGeometry();
                            kmlWriter.endPlacemark();
                        }
                        
                        kmlWriter.startPlacemark("L" + Integer.toString(groupCount), null, true);
                        kmlWriter.writeLineStyle(p.r, p.g, p.b, p.a, p.width);
                        kmlWriter.startMultiGeometry();
                        kmlWriter.startLineString(relativeToGround);
                        groupCount++;
                        
                        // repeat last point if there wasn't a break
                        if (!first && !p.graphBreak)
                            kmlWriter.writeCoordinates(ps.x, ps.y, ps.z, !relativeToGround);
                        
                        first = false;
                    }
                    
                    // if there is a break, start a new LineString
                    else if (p.graphBreak)
                    {
                        kmlWriter.endLineString();
                        kmlWriter.startLineString(relativeToGround);
                    }
                    
                    // write next line point coordinates
                    p.x *= R2D;
                    p.y *= R2D;
                    kmlWriter.writeCoordinates(p.x, p.y, p.z, !relativeToGround);
                    
                    // save point for reuse
                    p.graphBreak = false;
                    p.copy(ps);
                }
            }
            
            kmlWriter.endLineString();
            kmlWriter.endMultiGeometry();
            kmlWriter.endPlacemark();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }


    public void visit(PolygonStyler styler)
    {
        int polyCount = 1;
        boolean first = true;
        
        try
        {
            boolean relativeToGround = useRelativeToGround(styler);
            styler.resetIterators();
            
            while (styler.nextBlock() != null)
            {
                PolygonPointGraphic p;
                while ((p = styler.nextPoint()) != null)
                {
                    // if there is a break, start a new PlaceMark
                    if (first || p.graphBreak)
                    {
                        if (!first)
                        {
                            kmlWriter.endLinearRing();
                            kmlWriter.endPolygon();
                            kmlWriter.endPlacemark();
                        }
                        
                        kmlWriter.startPlacemark("P" + Integer.toString(polyCount), null, true);
                        kmlWriter.writePolyStyle(p.r, p.g, p.b, p.a);
                        kmlWriter.startPolygon(relativeToGround);
                        kmlWriter.startLinearRing(relativeToGround);
                        polyCount++;
                        
                        first = false;
                    }
                    
                    // write next line point coordinates
                    p.x *= R2D;
                    p.y *= R2D;
                    kmlWriter.writeCoordinates(p.x, p.y, p.z, !relativeToGround);
                    p.graphBreak = false;
                }
            }
            
            kmlWriter.endLinearRing();
            kmlWriter.endPolygon();
            kmlWriter.endPlacemark();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }


    public void visit(VectorStyler styler)
    {
        int vectCount = 1;
        boolean first = true;
        
        try
        {
            boolean relativeToGround = useRelativeToGround(styler);
            styler.resetIterators();
            
            while (styler.nextBlock() != null)
            {
                LinePointGraphic p;
                while ((p = styler.nextPoint()) != null)
                {
                    // if there is a break start a new PlaceMark
                    if (first || p.graphBreak)
                    {
                        if (!first)
                        {
                            kmlWriter.endLineString();
                            kmlWriter.endPlacemark();
                        }
                        
                        kmlWriter.startPlacemark("V" + Integer.toString(vectCount), null, true);
                        kmlWriter.writeLineStyle(p.r, p.g, p.b, p.a, p.width);
                        kmlWriter.startLineString(relativeToGround);
                        vectCount++;
                        
                        first = false;
                    }
                    
                    // write next line point coordinates
                    p.x *= R2D;
                    p.y *= R2D;
                    kmlWriter.writeCoordinates(p.x, p.y, p.z, !relativeToGround);
                    p.graphBreak = false;
                }
            }
            
            kmlWriter.endLineString();
            kmlWriter.endPlacemark();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }


    public void visit(LabelStyler styler)
    {
        try
        {
            boolean relativeToGround = useRelativeToGround(styler);
            styler.resetIterators();
                        
            while (styler.nextBlock() != null)
            {
                LabelGraphic p;
                while ((p = styler.nextPoint()) != null)
                {
                    kmlWriter.startPlacemark(p.text, null, true);
                    kmlWriter.writeLabelStyle(p.r, p.g, p.b, p.a, p.size/10.0f, true);
                    
                    // write next line point coordinates
                    p.x *= R2D;
                    p.y *= R2D;
                    kmlWriter.writePoint(p.x, p.y, p.z, relativeToGround);
                    
                    kmlWriter.endPlacemark();
                }
            }           
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
    }
    
    
    public void visit(GridMeshStyler styler)
    {
        // switch to ECEF projection
        Projection oldProjection = styler.getProjection();
        styler.setProjection(new Projection_ECF_GE());
        
        try
        {
            // if we are already in the hash table, it is the second pass
            if (modelTable.containsKey(styler))
            {
                int modelNum = modelTable.get(styler);
                ZipOutputStream kmzOutput = (ZipOutputStream)output;               
                
                // create COLLADA file in KMZ archive
                String modelFile = "model" + modelNum + ".dae";
                ZipEntry colladaEntry = new ZipEntry(modelFile);
                kmzOutput.putNextEntry(colladaEntry);
                
                colaWriter.startDocument();
                colaWriter.startGeometries();
                GridPatchGraphic patch;
                styler.resetIterators();
                
                // loop through all tiles
                while ((patch = styler.nextPatch()) != null)
                {           
                    colaWriter.startMesh("mesh01");
                    int vertexCount = patch.width * patch.length;
                    colaWriter.startVertexArray(vertexCount);
                    
                    // loop through all grid points and write coordinates in array
                    for (int v = 0; v < patch.length; v++)
                    {
                        for (int u = 0; u < patch.width; u++)
                        {
                            GridPointGraphic point = styler.getPoint(u, v);
                            colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                        }
                    }
                    
                    colaWriter.endVertexArray();
                    int lineCount = (patch.width - 1) * patch.length + (patch.length - 1) * patch.width;
                    colaWriter.startLines(lineCount);
                    
                    // loop through grid to generate vertex indices
                    for (int v = 0; v < patch.length; v++)
                    {
                        for (int u = 0; u < patch.width; u++)
                        {
                            int c = u + v*patch.width;
                            
                            if (u < patch.width -1)
                            {
                                colaWriter.writeIndex(c);
                                colaWriter.writeIndex(c+1);
                            }
                            
                            if (v < patch.length -1)
                            {
                                colaWriter.writeIndex(c);
                                colaWriter.writeIndex(c + patch.width);
                            }
                        }
                    }
                    
                    colaWriter.endLines();
                    colaWriter.endMesh();
                }
                
                colaWriter.endGeometries();
                colaWriter.endDocument();
                
                // reset symbolizer name!
                modelTable.remove(styler);
            }
            else
            {
                // put styler in hashtable for next pass
                modelTable.put(styler, modelNum);
                String modelFile = "model" + modelNum + ".dae";
                modelNum++;
                
                // write model xml
                kmlWriter.startPlacemark("Mesh", null, styler.getSymbolizer().isEnabled());
                kmlWriter.writeModel(modelFile);           
                kmlWriter.endPlacemark();
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            styler.setProjection(oldProjection);
        }
    }


    public void visit(GridFillStyler styler)
    {
        // switch to ECEF projection
        Projection oldProjection = styler.getProjection();
        styler.setProjection(new Projection_ECF_GE());
        
        try
        {
            // if we are already in the hash table, it is the second pass
            if (modelTable.containsKey(styler))
            {
                int modelNum = modelTable.get(styler);
                ZipOutputStream kmzOutput = (ZipOutputStream)output;
                
                // create COLLADA file in KMZ archive
                String modelFile = "model" + modelNum + ".dae";
                ZipEntry colladaEntry = new ZipEntry(modelFile);
                kmzOutput.putNextEntry(colladaEntry);
                
                colaWriter.startDocument();
                colaWriter.startGeometries();              
                GridPatchGraphic patch;
                styler.resetIterators();
                
                // loop through all tiles
                while ((patch = styler.nextPatch()) != null)
                {           
                    colaWriter.startMesh("mesh01");
                    
                    int vertexCount = patch.width * patch.length;
                    colaWriter.startVertexArray(vertexCount);
                    
                    // loop through all grid points and write coordinates in array
                    for (int v = 0; v < patch.length; v++)
                    {
                        for (int u = 0; u < patch.width; u++)
                        {
                            GridPointGraphic point = styler.getPoint(u, v);
                            colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                        }
                    }
                    
                    colaWriter.endVertexArray();
                    
                    int triangleCount = (patch.width - 1) * (patch.length - 1) * 2;
                    colaWriter.startTriangles(triangleCount, false);
                    
                    // loop through grid to generate vertex indices
                    for (int v = 0; v < patch.length-1; v++)
                    {
                        for (int u = 0; u < patch.width-1; u++)
                        {
                            // cell corners indices
                            
                            //      |      |
                            //   -- c1 -- c2 --      |
                            //      |      |       length
                            //   -- c3 -- c4 --      |
                            //      |      |
                            //
                            //  <---  width  --->
                            
                            int c1 = u + v*patch.width;
                            int c2 = c1 + 1;
                            int c3 = c1 + patch.width;
                            int c4 = c3 + 1;
                            
                            // triangle 1
                            colaWriter.writeIndex(c1);
                            colaWriter.writeIndex(c2);
                            colaWriter.writeIndex(c3);
                            
                            // triangle 2
                            colaWriter.writeIndex(c3);
                            colaWriter.writeIndex(c2);
                            colaWriter.writeIndex(c4);
                        }
                    }
                    
                    colaWriter.endTriangles();
                    colaWriter.endMesh();
                }
                
                colaWriter.endGeometries();
                colaWriter.endDocument();
                
                // reset symbolizer name!
                modelTable.remove(styler);
            }
            else
            {
                // put styler in hashtable for next pass
                modelTable.put(styler, modelNum);
                String modelFile = "model" + modelNum + ".dae";
                modelNum++;
                
                // write model xml
                kmlWriter.startPlacemark("Mesh", null, styler.getSymbolizer().isEnabled());
                kmlWriter.writeModel(modelFile);           
                kmlWriter.endPlacemark();
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            styler.setProjection(oldProjection);
        }
    }


    public void visit(GridBorderStyler styler)
    {
        // switch to ECEF projection
        Projection oldProjection = styler.getProjection();
        styler.setProjection(new Projection_ECF_GE());
        
        try
        {
            // if we are already in the hash table, it is the second pass
            if (modelTable.containsKey(styler))
            {
                int modelNum = modelTable.get(styler);
                ZipOutputStream kmzOutput = (ZipOutputStream)output;
                
                // create COLLADA file in KMZ archive
                String modelFile = "model" + modelNum + ".dae";
                ZipEntry colladaEntry = new ZipEntry(modelFile);
                kmzOutput.putNextEntry(colladaEntry);
                
                colaWriter.startDocument();
                colaWriter.startGeometries();
                GridPatchGraphic patch;
                styler.resetIterators();
                
                // loop through all tiles
                while ((patch = styler.nextPatch()) != null)
                {           
                    colaWriter.startMesh("mesh01");
                    int vertexCount = patch.width * 2 + (patch.length - 2) * 2;
                    colaWriter.startVertexArray(vertexCount);
                    GridPointGraphic point;
                    
                    // write each segment coordinates in array
                    // segment 1 (numpoints = width)
                    for (int u = 0; u < patch.width; u++)
                    {
                        point = styler.getPoint(u, 0);
                        colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                    }
                    
                    // segment 2 (numpoints = length-2)
                    for (int v = 1; v < patch.length-1; v++)
                    {
                        point = styler.getPoint(patch.width-1, v);
                        colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                    }
                    
                    // segment 3 (numpoints = width)
                    for (int u = patch.width-1; u >= 0; u--)
                    {
                        point = styler.getPoint(u, patch.length-1);
                        colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                    }
                    
                    // segment 4 (numpoints = length-2)
                    for (int v = patch.length-2; v >= 1 ; v--)
                    {
                        point = styler.getPoint(0, v);
                        colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                    }
                    
                    colaWriter.endVertexArray();
                    
                    // write indices to build line
                    int lineCount = (patch.width + patch.length - 2) * 2;
                    colaWriter.startLines(lineCount);
                    
                    // loop through grid to generate vertex indices
                    int i;
                    for (i = 0; i < lineCount-1; i++)
                    {
                        colaWriter.writeIndex(i);
                        colaWriter.writeIndex(i+1);
                    }
                    
                    // write one more equal to first one to close the line loop!
                    colaWriter.writeIndex(i);
                    colaWriter.writeIndex(0);
                    
                    colaWriter.endLines();
                    colaWriter.endMesh();
                }
                
                colaWriter.endGeometries();
                colaWriter.endDocument();
                
                // reset symbolizer name!
                modelTable.remove(styler);
            }
            else
            {
                // put styler in hashtable for next pass
                modelTable.put(styler, modelNum);
                String modelFile = "model" + modelNum + ".dae";
                modelNum++;
                
                // write model xml
                kmlWriter.startPlacemark("Mesh", null, styler.getSymbolizer().isEnabled());
                kmlWriter.writeModel(modelFile);           
                kmlWriter.endPlacemark();
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            styler.setProjection(oldProjection);
        }
    }
    
    
    public void visit(TextureStyler styler)
    {
        // switch to ECEF projection
        Projection oldProjection = styler.getProjection();
        styler.setProjection(new Projection_ECF_GE());
        TexturePatchGraphic patch;
        int tileIndex;
        
        try
        {
            // if we are already in the hash table, it is the second pass
            if (modelTable.containsKey(styler))
            {
                int modelNum = modelTable.get(styler);
                ZipOutputStream kmzOutput = (ZipOutputStream)output;               
                                
                // count tiles
                int numTiles = 0;
                styler.resetIterators();
                while (styler.nextTile() != null)
                    numTiles++;
                
                // loop through all texture tiles
                tileIndex = modelNum;
                styler.resetIterators();
                while ((patch = styler.nextTile()) != null)
                {
                    // add texture file to KMZ archive
                    String texFile = "tex" + tileIndex + ".png";
                    ZipEntry texEntry = new ZipEntry(texFile);
                    kmzOutput.putNextEntry(texEntry);
                    
                    // write texture to stream
                    textureManager.writeTexture(styler, patch.getTexture(), texFile, kmzOutput);
                    
                    tileIndex++;                    
                    kmzOutput.closeEntry();
                }
                
                // loop through all grid tiles
                tileIndex = modelNum;
                styler.resetIterators();
                while ((patch = styler.nextTile()) != null)
                {           
                    // add COLLADA file to KMZ archive
                    String modelFile = "model" + tileIndex + ".dae";
                    String texFile = "tex" + tileIndex + ".png";
                    ZipEntry colladaEntry = new ZipEntry(modelFile);
                    kmzOutput.putNextEntry(colladaEntry);
                    
                    colaWriter.startDocument();                
                    
                    colaWriter.writeImageLibrary(1, new String[] {texFile});
                    colaWriter.writeEffects(1);
                    colaWriter.writeMaterials(1);

                    colaWriter.startGeometries();
                    
                    colaWriter.startMesh("mesh01");
                    int width = patch.getGrid().width;
                    int length = patch.getGrid().length;
                    
                    
                    // write mesh vertex array
                    int vertexCount = width * length;
                    colaWriter.startVertexArray(vertexCount);
                    
                    // loop through all grid points and write coordinates in array
                    for (int v = 0; v < length; v++)
                    {
                        for (int u = 0; u < width; u++)
                        {
                            GridPointGraphic point = styler.getGridPoint(u, v);
                            colaWriter.writeCoordinates3D(point.x, point.y, point.z);
                        }
                    }
                    
                    colaWriter.endVertexArray();
                    
                    
                    // write texture coordinates array
                    colaWriter.startTexCoordsArray(vertexCount);
                    
                    // loop through all grid points and write coordinates in array
                    for (int v = 0; v < length; v++)
                    {
                        for (int u = 0; u < width; u++)
                        {
                            GridPointGraphic point = styler.getGridPoint(u, v);
                            colaWriter.writeCoordinates2D(point.tx, 1.0 - point.ty);
                        }
                    }
                    
                    colaWriter.endTexCoordsArray();
                    
                    
                    // write triangle faces using indices
                    int triangleCount = (width - 1) * (length - 1) * 2;
                    colaWriter.startTriangles(triangleCount, true);
                    
                    // loop through grid to generate vertex indices
                    for (int v = 0; v < length-1; v++)
                    {
                        for (int u = 0; u < width-1; u++)
                        {
                            // cell corners indices
                            
                            //      |      |
                            //   -- c1 -- c2 --      |
                            //      |      |       length
                            //   -- c3 -- c4 --      |
                            //      |      |
                            //
                            //  <---  width  --->
                            
                            int c1 = u + v*width;
                            int c2 = c1 + 1;
                            int c3 = c1 + width;
                            int c4 = c3 + 1;
                            
                            // triangle 1
                            colaWriter.writeIndex(c1);
                            colaWriter.writeIndex(c2);
                            colaWriter.writeIndex(c3);
                            
                            // triangle 2
                            colaWriter.writeIndex(c3);
                            colaWriter.writeIndex(c2);
                            colaWriter.writeIndex(c4);
                        }
                    }
                    
                    colaWriter.endTriangles();
                    colaWriter.endMesh();                    
                    colaWriter.endGeometries();
                    colaWriter.endDocument();
                    
                    tileIndex++;
                    kmzOutput.closeEntry();
                }
                
                // reset symbolizer name!
                modelTable.remove(styler);
            }
            else
            {
                // put styler in hashtable for next pass
                modelTable.put(styler, modelNum);
                
                styler.resetIterators();
                while ((patch = styler.nextTile()) != null)
                {
                    kmlWriter.startPlacemark("Mesh", null, styler.getSymbolizer().isEnabled());
                    String modelFile = "model" + modelNum + ".dae";
                    kmlWriter.writeModel(modelFile);
                    kmlWriter.endPlacemark();
                    modelNum++;
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            styler.setProjection(oldProjection);
        }
    }
    
    
    public void visit(RasterStyler styler)
    {
        // TODO Auto-generated method stub

    }


    protected boolean useRelativeToGround(DataStyler styler)
    {
        boolean relativeToGround = true;

        STTSpatialExtent bbox = styler.getBoundingBox();
        if (bbox.getMinZ() != 0 || bbox.getMaxZ() != 0)
            relativeToGround = false;

        return relativeToGround;
    }
}
