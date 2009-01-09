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

import java.io.OutputStream;
import java.text.DecimalFormat;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.vast.ows.sld.Color;


/**
 * <p><b>Title:</b>
 * KMLWriter21
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO KMLWriter21 type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 1, 2007
 * @version 1.0
 */
public class KMLStreamWriter21
{
    protected final static String KML21_NS = "http://earth.google.com/kml/2.1";
    protected final static char coordSeparator = ',';
    protected final static char tupleSeparator = ' ';
    protected XMLStreamWriter writer;
    protected OutputStream out;
    protected DecimalFormat numberFormatter;
    protected boolean insidePolygon;


    public KMLStreamWriter21(OutputStream outStream) throws XMLStreamException
    {
        this.out = outStream;
        this.writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream);
        this.numberFormatter = new DecimalFormat();
        this.numberFormatter.setMaximumFractionDigits(4);
        this.numberFormatter.setGroupingUsed(false);
    }


    public void setMaximumFractionDigits(int fracDigits)
    {
        this.numberFormatter.setMaximumFractionDigits(fracDigits);
    }


    /**
     * Writes begining of Document structure so that it is
     * ready to accept entries such as Folders and Placemarks.
     * @throws XMLStreamException
     */
    public void startDocument() throws XMLStreamException
    {
        writer.writeStartElement("kml");
        writer.writeAttribute("xmlns", KML21_NS);
        writer.writeStartElement("Document");
    }


    public void endDocument() throws XMLStreamException
    {
        writer.writeEndElement(); // close Document
        writer.writeEndElement(); // close kml
        writer.flush();
        writer.close();
    }
    
    
    /**
     * Writes begining of Folder structure
     * Folder entries can then be added recursively
     * @param name
     * @throws XMLStreamException
     */
    public void startFolder(String name, String description, boolean visible) throws XMLStreamException
    {
        /*
        <Folder id="ID">
          <name>...</name>
          <visibility>1</visibility>
          <open>1</open>
          <address>...</address>
          <phoneNumber>...</phoneNumber>
          <Snippet maxLines="2">...</Snippet>
          <description>...</description>
          <LookAt>...</LookAt>
          <TimePrimitive>...</TimePrimitive>
          <styleUrl>...</styleUrl>
          <StyleSelector>...</StyleSelector>
          <Region>...</Region>
          <Metadata>...</Metadata>
          <Feature> 0..*
        </Folder>
        */
        
        writer.writeStartElement("Folder");
        this.writeSimpleElement("name", name);
        this.writeSimpleElement("visibility", (visible ? "1" : "0"));
        this.writeSimpleElement("open", "false");
        if (description != null)
            this.writeSimpleElement("description", description);
    }
    
    
    public void endFolder() throws XMLStreamException
    {
        writer.writeEndElement();
    }
    
    
    public void startNetworkLink(String name, String description, boolean visible) throws XMLStreamException
    { 
        /*
        <NetworkLink id="ID">
          <name>...</name>
          <visibility>1</visibility>
          <open>1</open>
          <address>...</address>
          <phoneNumber>...</phoneNumber>
          <Snippet maxLines="2">...</Snippet>
          <description>...</description>
          <LookAt>...</LookAt>
          <TimePrimitive>...</TimePrimitive>
          <styleUrl>...</styleUrl>
          <StyleSelector>...</StyleSelector>
          <Region>...</Region>
          <Metadata>...</Metadata>
          <Link>...</Link>
          <refreshVisibility>0</refreshVisibility>
          <flyToView>0</flyToView>
          </NetworkLink>
        */
        
        writer.writeStartElement("NetworkLink");
        this.writeSimpleElement("name", name);
        this.writeSimpleElement("visibility", (visible ? "1" : "0"));
        if (description != null)
            this.writeSimpleElement("description", description);
    }
    
    
    public void endNetworkLink() throws XMLStreamException
    {
        writer.writeEndElement();
    }
    
    
    public void writeSimpleLink(String url, String refreshMode, float refreshInterval) throws XMLStreamException
    {
        /*
        <Link id="ID">
          <href>...</href>
          <refreshMode>onChange</refreshMode>          <!-- refreshModeEnum: onChange, OnInterval, or OnExpire -->   
          <refreshInterval>4</refreshInterval>
          <viewRefreshMode>never</viewRefreshMode>     <!-- viewRefreshModeEnum: never, onStop, onRequest, onRegion -->
          <viewRefreshTime>4</viewRefreshTime>
          <viewBoundScale>1</viewBoundScale>
          <viewFormat>BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]</viewFormat>
          <httpQuery>...</httpQuery>
        </Link>
        */
        
        writer.writeStartElement("Link");
        
        this.writeSimpleElement("href", url);
        this.writeSimpleElement("refreshMode", refreshMode);
        this.writeSimpleElement("refreshInterval", Float.toString(refreshInterval));
        
        writer.writeEndElement();
    }
    
    
    public void writeViewLink(String url, String viewRefreshMode, float viewRefreshDelay) throws XMLStreamException
    {
        /*
        <Link id="ID">
          <href>...</href>
          <refreshMode>onChange</refreshMode>          <!-- refreshModeEnum: onChange, OnInterval, or OnExpire -->   
          <refreshInterval>4</refreshInterval>
          <viewRefreshMode>never</viewRefreshMode>     <!-- viewRefreshModeEnum: never, onStop, onRequest, onRegion -->
          <viewRefreshTime>4</viewRefreshTime>
          <viewBoundScale>1</viewBoundScale>
          <viewFormat>BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]</viewFormat>
          <httpQuery>...</httpQuery>
        </Link>
        */
        
        writer.writeStartElement("Link");
        
        this.writeSimpleElement("href", url);
        this.writeSimpleElement("viewRefreshMode", viewRefreshMode);
        this.writeSimpleElement("viewRefreshTime", Float.toString(viewRefreshDelay));
        this.writeSimpleElement("viewFormat", "bbox=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]");
        
        writer.writeEndElement();
    }
    
    
    /**
     * Writes beginning of PlaceMark structure
     * @param parentElt
     * @param name
     * @param description
     * @throws XMLStreamException
     */
    public void startPlacemark(String name, String description, boolean visible) throws XMLStreamException
    {
        /*
        <Placemark id="ID">
          <name>...</name>
          <visibility>1</visibility>
          <open>1</open>
          <address>...</address>
          <AddressDetails xmlns="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0">...
          </AddressDetails>
          <phoneNumber>...</phoneNumber>
          <Snippet maxLines="2">...</Snippet>
          <description>...</description>
          <LookAt>...</LookAt>
          <TimePrimitive>...</TimePrimitive>
          <styleUrl>...</styleUrl>
          <StyleSelector>...</StyleSelector>
          <Region>...</Region>
          <Metadata>...</Metadata>
          <Geometry>...</Geometry>
        </Placemark>
        */

        writer.writeStartElement("Placemark");
        
        if (name != null)
            this.writeSimpleElement("name", name);
        
        this.writeSimpleElement("visibility", (visible ? "1" : "0"));        
        //this.writeSimpleElement("open", "false");
        
        if (description != null)
            this.writeSimpleElement("description", description);      
    }
    
    
    public void endPlacemark() throws XMLStreamException
    {
        writer.writeEndElement();
    }
    
    
    public void writeModel(String modelFile) throws XMLStreamException
    {
        /*
        <Model id="model_4">
            <altitudeMode>relativeToGround</altitudeMode>
            <Location>
                <longitude>0</longitude>
                <latitude>90</latitude>
                <altitude>-6356750</altitude>
            </Location>
            <Orientation>
                <heading>90</heading>
                <tilt>0</tilt>
                <roll>0</roll>
            </Orientation>
            <Scale>
                <x>1</x>
                <y>1</y>
                <z>1</z>
            </Scale>
            <Link>
                <href>files/rectangle.dae</href>
            </Link>
        </Model>
        */
        
        writer.writeStartElement("Model");
        
        writer.writeStartElement("Location");
        this.writeSimpleElement("longitude", "0");
        this.writeSimpleElement("latitude", "90");
        this.writeSimpleElement("altitude", "0");
        writer.writeEndElement();
        
        writer.writeStartElement("Orientation");
        this.writeSimpleElement("heading", "90");
        this.writeSimpleElement("tilt", "0");
        this.writeSimpleElement("roll", "0");
        writer.writeEndElement();
        
        writer.writeStartElement("Scale");
        this.writeSimpleElement("x", "1");
        this.writeSimpleElement("y", "1");
        this.writeSimpleElement("z", "1");
        writer.writeEndElement();
        
        writer.writeStartElement("Link");
        this.writeSimpleElement("href", modelFile);
        writer.writeEndElement();
        
        writer.writeEndElement();
    }
    
    
    /**
     * Util method to write the color element with BGRA hex data
     * @param r
     * @param g
     * @param b
     * @param a
     * @throws XMLStreamException
     */
    protected void writeColor(String tagName, float r, float g, float b, float a) throws XMLStreamException
    {
        String color = Color.toHexString(r, g, b, a, true);
        this.writeSimpleElement(tagName, color);
    }
    
    
    /**
     * Writes IconStyle with color and width
     * @param r
     * @param g
     * @param b
     * @param a
     * @param size
     * @param heading
     * @param iconUrl
     * @throws XMLStreamException
     */
    public void writeIconStyle(float r, float g, float b, float a, float size, float heading, String iconUrl, boolean noLabel) throws XMLStreamException
    {
        /*
        <IconStyle id="ID">
          <color>ffffffff</color>
          <colorMode>normal</colorMode>
          <scale>1</scale>
          <heading>0</heading>
          <Icon>
            <href>...</href>
          </Icon> 
          <hotSpot x="0.5"  y="0.5" xunits="fraction" yunits="fraction"/>    <!-- kml:vec2Type -->                    
        </IconStyle>
        */
        
        writer.writeStartElement("Style");
        
        if (noLabel)
        {
            writer.writeStartElement("LabelStyle");
            this.writeColor("color", 0.0f, 0.0f, 0.0f, 0.0f);
            this.writeSimpleElement("scale", Float.toString(0.0f));
            writer.writeEndElement(); // LabelStyle
        }        
        
        writer.writeStartElement("IconStyle");
        this.writeColor("color", r, g, b, a);
        
        if (heading != 0.0)
            this.writeSimpleElement("heading", Float.toString(heading));
        
        if (size != 1)
            this.writeSimpleElement("scale", Float.toString(size));
        
        writer.writeStartElement("Icon");
        this.writeSimpleElement("href", iconUrl);
        writer.writeEndElement();
            
        writer.writeEndElement(); // IconStyle
        writer.writeEndElement(); // Style
    }
    
    
    public void writeLabelStyle(float r, float g, float b, float a, float size, boolean noIcon) throws XMLStreamException
    {
        /*
        <LabelStyle id="ID">
          <color>ffffffff</color>
          <colorMode>normal</colorMode>
          <scale>1</scale>
        </LabelStyle>
        */
        
        writer.writeStartElement("Style");
        
        if (noIcon)
        {
            writer.writeStartElement("IconStyle");
            this.writeColor("color", 0.0f, 0.0f, 0.0f, 0.0f);
            this.writeSimpleElement("scale", Float.toString(0.0f));
            writer.writeEndElement(); // IconStyle
        } 
        
        writer.writeStartElement("LabelStyle");
        this.writeColor("color", r, g, b, a);
        this.writeSimpleElement("scale", Float.toString(size));
        writer.writeEndElement(); // LabelStyle
        writer.writeEndElement(); // Style
    }
        
    
    /**
     * Writes LineStyle with color and width
     * @param r
     * @param g
     * @param b
     * @param a
     * @param width
     * @throws XMLStreamException
     */
    public void writeLineStyle(float r, float g, float b, float a, float width) throws XMLStreamException
    {
        /*
        <LineStyle id="ID">
          <color>ffffffff</color>
          <colorMode>normal</colorMode>
          <width>1</width>
        </LineStyle>
        */
        
        writer.writeStartElement("Style");
        writer.writeStartElement("LineStyle");        
        this.writeColor("color", r, g, b, a);
        this.writeSimpleElement("width", Integer.toString((int)width));        
        writer.writeEndElement(); // LineStyle
        writer.writeEndElement(); // Style
    }
    
    
    /**
     * Writes PolyStyle with color and width
     * @param r
     * @param g
     * @param b
     * @param a
     * @param width
     * @throws XMLStreamException
     */
    public void writePolyStyle(float r, float g, float b, float a) throws XMLStreamException
    {
        /*
        <PolyStyle id="ID">
          <color>ffffffff</color>
          <colorMode>normal</colorMode>
          <fill>1</fill>
          <outline>1</outline>
        </PolyStyle>
        */
        
        writer.writeStartElement("Style");
        writer.writeStartElement("PolyStyle");
        this.writeColor("color", r, g, b, a);
        this.writeSimpleElement("outline", "0");
        writer.writeEndElement(); // PolyStyle
        writer.writeEndElement(); // Style
    }
    
    
    /**
     * Writes BalloonStyle containing this text and background color
     * @param text
     * @param r
     * @param g
     * @param b
     * @param a
     * @throws XMLStreamException
     */
    public void writeBaloonStyle(String text, float r, float g, float b, float a) throws XMLStreamException
    {
        /*
        <BalloonStyle id="ID">
          <bgColor>ffffffff</bgColor>
          <textColor>ff000000</textColor>
          <text>...</text>
        </BalloonStyle>
        */
        
        writer.writeStartElement("Style");
        writer.writeStartElement("BalloonStyle");
        this.writeColor("bgColor", r, g, b, a);
        writer.writeStartElement("text");
        writer.writeCData(text);
        writer.writeEndElement();      
        writer.writeEndElement(); // BalloonStyle
        writer.writeEndElement(); // Style
    }
    

    /**
     * Writes begining of MultiGeometry ready to accept a sequence
     * of Geometry Objects such as Point, LineString, Polygons, etc...
     * @throws XMLStreamException
     */
    public void startMultiGeometry() throws XMLStreamException
    {
        writer.writeStartElement("MultiGeometry");
    }
    
    
    public void endMultiGeometry() throws XMLStreamException
    {
        writer.writeEndElement();
    }
    
    
    /**
     * Writes the whole point structure
     * It uses coordinates from point.x, point.y, point.z
     * @param point
     * @param relativeToGround
     * @throws XMLStreamException
     */
    public void writePoint(double x, double y, double z, boolean relativeToGround) throws XMLStreamException
    {
        /*
         <Point id="ID">
           <extrude>0</extrude>
           <tessellate>0</tessellate>
           <altitudeMode>clampToGround</altitudeMode>
           <coordinates>...</coordinates>         <!-- lon,lat[,alt] -->
         </Point>
         */

        writer.writeStartElement("Point");

        if (relativeToGround)
        {
            this.writeSimpleElement("tessellate", "1");
            this.writeSimpleElement("altitudeMode", "relativeToGround");
        }
        else
        {
            this.writeSimpleElement("altitudeMode", "absolute");
        }

        writer.writeStartElement("coordinates");
        this.writeCoordinates(x, y, z, (z != 0));

        writer.writeEndElement(); // close coordinates
        writer.writeEndElement(); // close Point
    }


    /**
     * Writes begining of LineString structure
     * It should contain at least 2 coordinate tuples
     * @param relativeToGround
     * @throws XMLStreamException
     */
    public void startLineString(boolean relativeToGround) throws XMLStreamException
    {
        /*
         <LineString>
           <extrude>1</extrude>
           <tessellate>1</tessellate>
           <altitudeMode>relativeToGround</altitudeMode>
           <coordinates>...</coordinates>
         </LineString>
         */

        writer.writeStartElement("LineString");

        if (relativeToGround)
        {
            this.writeSimpleElement("tessellate", "1");
            this.writeSimpleElement("altitudeMode", "relativeToGround");
        }
        else
        {
            this.writeSimpleElement("altitudeMode", "absolute");
        }

        writer.writeStartElement("coordinates");
    }


    /**
     * Writes end of LineString structure
     * At this point coordinate tuples should have been written
     * @throws XMLStreamException
     */
    public void endLineString() throws XMLStreamException
    {
        writer.writeEndElement(); // end coordinates
        writer.writeEndElement(); // end LineString
        writer.flush();
    }


    /**
     * Writes begining of LinearRing structure
     * It should contain at least 3 coordinate tuples
     * @param relativeToGround
     * @throws XMLStreamException
     */
    public void startLinearRing(boolean relativeToGround) throws XMLStreamException
    {
        /*
         <LinearRing id="ID">
           <extrude>0</extrude>
           <tessellate>0</tessellate>
           <altitudeMode>clampToGround</altitudeMode>
           <coordinates>...</coordinates>
         </LinearRing>
         */

        writer.writeStartElement("LinearRing");

        // don't repeat this if we're inside a polygon
        if (!insidePolygon)
        {
            if (relativeToGround)
            {
                this.writeSimpleElement("tessellate", "1");
                this.writeSimpleElement("altitudeMode", "relativeToGround");
            }
            else
            {
                this.writeSimpleElement("altitudeMode", "absolute");
            }
        }

        writer.writeStartElement("coordinates");
    }


    /**
     * Writes end of LinearRing structure
     * At this point coordinate tuples should have been written
     * @throws XMLStreamException
     */
    public void endLinearRing() throws XMLStreamException
    {
        writer.writeEndElement(); // end coordinates
        writer.writeEndElement(); // end LinearRing
        writer.flush();
    }


    /**
     * Writes begining of Polygon structure
     * It should contain at least 4 coordinates tuples inside a LinearRing
     * @param relativeToGround
     * @throws XMLStreamException
     */
    public void startPolygon(boolean relativeToGround) throws XMLStreamException
    {
        /*
         <Polygon id="ID">
           <extrude>0</extrude>
           <tessellate>0</tessellate>
           <altitudeMode>relativeToGround</altitudeMode>
           <outerBoundaryIs>
             <LinearRing>
               <coordinates>...</coordinates>
             </LinearRing>
           </outerBoundaryIs>
           <innerBoundaryIs>
             <LinearRing>
               <coordinates>...</coordinates>
             </LinearRing>
           </innerBoundaryIs>
         </Polygon>
         */

        writer.writeStartElement("Polygon");
        this.insidePolygon = true;

        if (relativeToGround)
        {
            this.writeSimpleElement("tessellate", "1");
            this.writeSimpleElement("altitudeMode", "relativeToGround");
        }
        else
        {
            this.writeSimpleElement("altitudeMode", "absolute");
        }

        writer.writeStartElement("outerBoundaryIs");
    }


    /**
     * Writes end of Polygon structure
     * Outer Boundary LinearRing should have been written
     * @throws XMLStreamException
     */
    public void endPolygon() throws XMLStreamException
    {
        this.insidePolygon = false;
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();
    }


    /**
     * Writes a coordinate tuple in the form lon,lat[,alt]
     * @param x
     * @param y
     * @param z
     * @param writeZ include Z coordinate if true
     * @throws XMLStreamException
     */
    public void writeCoordinates(double lat, double lon, double alt, boolean writeZ) throws XMLStreamException
    {
        /* lon,lat[,alt] tuples */
        
        StringBuffer buffer = new StringBuffer(64);

        buffer.append(numberFormatter.format(lat));
        buffer.append(coordSeparator);
        buffer.append(numberFormatter.format(lon));

        if (writeZ)
        {
            buffer.append(coordSeparator);
            buffer.append(numberFormatter.format(alt));
        }

        buffer.append(tupleSeparator);

        writer.writeCharacters(buffer.toString());
    }


    /**
     * Helper to write an element with a simple value
     * Calls writeStartElement(localName), writeCharacters(value) and writeEndElement()
     * @param localName
     * @param value
     * @throws XMLStreamException
     */
    protected void writeSimpleElement(String localName, String value) throws XMLStreamException
    {
        writer.writeStartElement(localName);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }
}
