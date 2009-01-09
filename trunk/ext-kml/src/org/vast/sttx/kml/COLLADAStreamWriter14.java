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
import org.vast.util.DateTime;
import org.vast.util.DateTimeFormat;


/**
 * <p><b>Title:</b>
 * COLLADAStreamWriter4
 * </p>
 *
 * <p><b>Description:</b><br/>
 * TODO COLLADAStreamWriter4 type description
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Nov 1, 2007
 * @version 1.0
 */
public class COLLADAStreamWriter14
{
    protected final static String COLLADA_NS = "http://www.collada.org/2005/11/COLLADASchema";
    protected final static char coordSeparator = ' ';
    protected final static char tupleSeparator = ' ';
    protected XMLStreamWriter writer;
    protected OutputStream out;
    protected DecimalFormat numberFormatter;


    public COLLADAStreamWriter14(OutputStream outStream) throws XMLStreamException
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
        writer.writeStartElement("COLLADA");
        writer.writeAttribute("xmlns", COLLADA_NS);
        
        /*
        <asset>
          <contributor>
            <authoring_tool>Google SketchUp</authoring_tool>
          </contributor>
          <created>2006-11-15T23:17:19Z</created>
          <modified>2006-11-15T23:17:19Z</modified>
          <unit name="meter" meter="1.0"/>
          <up_axis>Z_UP</up_axis>
        </asset>
        */
        
        writer.writeStartElement("asset");
        
        writer.writeStartElement("contributor");
        this.writeSimpleElement("authoring_tool", "Space Time Toolkit v3");
        writer.writeEndElement(); // close contributor
        
        this.writeSimpleElement("created", DateTimeFormat.formatIso(new DateTime().getJulianTime(), 0));
        
        writer.writeStartElement("unit");
        writer.writeAttribute("name", "meter");
        writer.writeAttribute("meter", "1.0");
        writer.writeEndElement(); // close unit
        
        this.writeSimpleElement("up_axis", "Z_UP");
        
        writer.writeEndElement(); // close asset
    }


    public void endDocument() throws XMLStreamException
    {
        /*
          <library_visual_scenes>
            <visual_scene id="scene" name="scene">
              <node id="Rectangle01-node" name="Rectangle01-node">
                <instance_geometry url="#Rectangle01">
                    <bind_material>
                         <technique_common>
                           <instance_material symbol="WHITE" target="#whiteMaterial"/>
                         </technique_common>
                      </bind_material>
                  </instance_geometry>
              </node>
            </visual_scene>
          </library_visual_scenes>
          <scene>
            <instance_visual_scene url="#scene"/>
          </scene> 
        */
        
        writer.writeStartElement("library_visual_scenes");        
        writer.writeStartElement("visual_scene");
        writer.writeAttribute("id", "scene");
        writer.writeAttribute("name", "scene");
        writer.writeStartElement("node");
        writer.writeAttribute("id", "node01");
        writer.writeAttribute("name", "node01");
        
        writer.writeStartElement("instance_geometry");
        writer.writeAttribute("url", "#" + geometryId);
        writer.writeStartElement("bind_material");
        writer.writeStartElement("technique_common");
        writer.writeStartElement("instance_material");        
        writer.writeAttribute("symbol", "SYM1");
        writer.writeAttribute("target", "#mat1");
        writer.writeEndElement(); // close instance_material
        writer.writeEndElement(); // close technique_common
        writer.writeEndElement(); // close bind_material
        writer.writeEndElement(); // close instance_geometry
        writer.writeEndElement(); // close node
        writer.writeEndElement(); // close visual_scene
        writer.writeEndElement(); // close library_visual_scenes
        
        
        writer.writeStartElement("scene");        
        writer.writeStartElement("instance_visual_scene");
        writer.writeAttribute("url", "#scene");
        writer.writeEndElement(); // close instance_visual_scene
        writer.writeEndElement(); // close scene
        
        writer.writeEndElement(); // close COLLADA
        writer.flush();
        writer.close();
    }
    
    
    public void startGeometries() throws XMLStreamException
    {
        writer.writeStartElement("library_geometries");
    }
    
    
    public void endGeometries() throws XMLStreamException
    {
        writer.writeEndElement(); // close library_geometries
    }
    
    
    protected String geometryId;
    public void startMesh(String geometryId) throws XMLStreamException
    {
        this.geometryId = geometryId;
        
        writer.writeStartElement("geometry");
        writer.writeAttribute("id", geometryId);        
        writer.writeStartElement("mesh");
    }
    
    
    public void endMesh() throws XMLStreamException
    {
        writer.writeEndElement(); // close mesh
        writer.writeEndElement(); // close geometry
    }


    protected String vertexSourceId;
    protected int vertexCount;
    public void startVertexArray(int vertexCount) throws XMLStreamException
    {
        /*
         <source id="Rectangle01-vertex">
          <float_array id="mesh01-vertex-array" count="12">...</float_array>
          <technique_common>
            <accessor source="#mesh01-vertex-array" count="4" stride="3">
              <param name="X" type="float"/>
              <param name="Y" type="float"/>
              <param name="Z" type="float"/>
            </accessor>
          </technique_common>
        </source> 
        */
        
        this.vertexSourceId = geometryId + "-vertex";
        this.vertexCount = vertexCount;
        
        writer.writeStartElement("source");
        writer.writeAttribute("id", vertexSourceId);
        
        writer.writeStartElement("float_array");
        writer.writeAttribute("id", vertexSourceId + "-array");
        writer.writeAttribute("count", Integer.toString(vertexCount * 3));
        
        // now have to write characters for each coordinates
    }
    
    
    public void endVertexArray() throws XMLStreamException
    {
        writer.writeEndElement(); // close float_array
        
        writer.writeStartElement("technique_common");
        writer.writeStartElement("accessor");
        writer.writeAttribute("source", "#" + vertexSourceId + "-array");
        writer.writeAttribute("count", Integer.toString(vertexCount));
        writer.writeAttribute("stride", "3");
        
        writer.writeStartElement("param");
        writer.writeAttribute("name", "X");
        writer.writeAttribute("type", "float");
        writer.writeEndElement(); // close param
        
        writer.writeStartElement("param");
        writer.writeAttribute("name", "Y");
        writer.writeAttribute("type", "float");
        writer.writeEndElement(); // close param
        
        writer.writeStartElement("param");
        writer.writeAttribute("name", "Z");
        writer.writeAttribute("type", "float");
        writer.writeEndElement(); // close param
        
        writer.writeEndElement(); // close accessor
        writer.writeEndElement(); // close technique_common
        writer.writeEndElement(); // close source
    }
    
    
    protected String texCoordsSourceId;
    protected int texCoordsCount;
    public void startTexCoordsArray(int texCoordsCount) throws XMLStreamException
    {
        /*
         <source id="Rectangle01-texCoords">
          <float_array id="mesh01-texCoords-array" count="12">...</float_array>
          <technique_common>
            <accessor source="#mesh01-texCoords-array" count="4" stride="3">
              <param name="S" type="float"/>
              <param name="T" type="float"/>
            </accessor>
          </technique_common>
        </source> 
        */
        
        this.texCoordsSourceId = geometryId + "-texCoords";
        this.texCoordsCount = texCoordsCount;
        
        writer.writeStartElement("source");
        writer.writeAttribute("id", texCoordsSourceId);
        
        writer.writeStartElement("float_array");
        writer.writeAttribute("id", vertexSourceId + "-array");
        writer.writeAttribute("count", Integer.toString(texCoordsCount * 2));
        
        // now have to write characters for each coordinates
    }
    
    
    public void endTexCoordsArray() throws XMLStreamException
    {
        writer.writeEndElement(); // close float_array
        
        writer.writeStartElement("technique_common");
        writer.writeStartElement("accessor");
        writer.writeAttribute("source", "#" + texCoordsSourceId + "-array");
        writer.writeAttribute("count", Integer.toString(texCoordsCount));
        writer.writeAttribute("stride", "2");
        
        writer.writeStartElement("param");
        writer.writeAttribute("name", "S");
        writer.writeAttribute("type", "float");
        writer.writeEndElement(); // close param
        
        writer.writeStartElement("param");
        writer.writeAttribute("name", "T");
        writer.writeAttribute("type", "float");
        writer.writeEndElement(); // close param
        
        writer.writeEndElement(); // close accessor
        writer.writeEndElement(); // close technique_common
        writer.writeEndElement(); // close source
    }
    
    
    public void startLines(int lineCount) throws XMLStreamException
    {
        /*
        <vertices id="verts">
          <input semantic="POSITION" source="#mesh01-vertex"/>
        </vertices>        
        <lines count="2">
          <input semantic="VERTEX" source="#verts" offset="0"/>
          <p>0 1 2 1 2 3</p>
        </lines>
       */
       
       writer.writeStartElement("vertices");
       writer.writeAttribute("id", geometryId + "-verts");
       writer.writeStartElement("input");
       writer.writeAttribute("semantic", "POSITION");
       writer.writeAttribute("source", "#" + vertexSourceId);
       writer.writeEndElement(); // close input
       writer.writeEndElement(); // close vertices
       
       writer.writeStartElement("lines");
       writer.writeAttribute("count", Integer.toString(lineCount));
       
       writer.writeStartElement("input");
       writer.writeAttribute("semantic", "VERTEX");
       writer.writeAttribute("source", "#" + geometryId + "-verts");
       writer.writeAttribute("offset", "0");
       writer.writeEndElement(); // close input
       
       writer.writeStartElement("p");
       
       // now write vertex indices
    }
    
    
    public void endLines() throws XMLStreamException
    {
        writer.writeEndElement(); // close p
        writer.writeEndElement(); // close lines
    }
    
    
    public void startTriangles(int triangleCount, boolean texCoords) throws XMLStreamException
    {
        /*
         <vertices id="mesh01-verts">
           <input semantic="POSITION" source="#mesh01-vertex"/>
         </vertices>        
         <triangles count="2" material="WHITE">
           <input semantic="VERTEX" source="#mesh01-verts" offset="0"/>
           <input semantic="NORMAL" source="#mesh01-normals" offset="0"/>
           <input semantic="TEXCOORD" source="#mesh01-texCoords" offset="0"/>
           <p>0 1 2 1 2 3</p>
         </triangles>
        */
        
        writer.writeStartElement("vertices");
        writer.writeAttribute("id", geometryId + "-verts");
        writer.writeStartElement("input");
        writer.writeAttribute("semantic", "POSITION");
        writer.writeAttribute("source", "#" + vertexSourceId);
        writer.writeEndElement(); // close input
        writer.writeEndElement(); // close vertices
        
        writer.writeStartElement("triangles");
        writer.writeAttribute("material", "SYM1");
        writer.writeAttribute("count", Integer.toString(triangleCount));
        
        writer.writeStartElement("input");
        writer.writeAttribute("semantic", "VERTEX");
        writer.writeAttribute("source", "#" + geometryId + "-verts");
        writer.writeAttribute("offset", "0");
        writer.writeEndElement(); // close input
        
        if (texCoords)
        {
            writer.writeStartElement("input");
            writer.writeAttribute("semantic", "TEXCOORD");
            writer.writeAttribute("source", "#" + geometryId + "-texCoords");
            writer.writeAttribute("offset", "0");
            writer.writeEndElement(); // close input
        }
        
        writer.writeStartElement("p");
        
        // now write vertex indices
    }
    
    
    public void endTriangles() throws XMLStreamException
    {
        writer.writeEndElement(); // close p
        writer.writeEndElement(); // close triangles
    }
    
    
    public void writeIndex(int i) throws XMLStreamException
    {
        writer.writeCharacters(Integer.toString(i) + " ");
    }
    
    
    public void writeImageLibrary(int texCount, String[] textureFiles) throws XMLStreamException
    {
        /*
          <library_images>
            <image id="image01">
              <init_from>tex01.png</init_from>
            </image>
            <image id="image02">
              <init_from>tex02.png</init_from>
            </image>
          </library_images> 
        */
        
        writer.writeStartElement("library_images");
        
        for (int i=1; i<=texCount; i++)
        {
            String texName = "tex" + i;
            writer.writeStartElement("image");
            writer.writeAttribute("id", texName);
            this.writeSimpleElement("init_from", textureFiles[i-1]);
            writer.writeEndElement();
        }
        
        writer.writeEndElement();
    }
    
    
    public void writeEffects(int texCount) throws XMLStreamException
    {
        writer.writeStartElement("library_effects");
        
        for (int i=1; i<=texCount; i++)
        {
            String effectName = "effect" + i;
            String surfName = "surf" + i;
            String samplerName = "sampler" + i;
            
            writer.writeStartElement("effect");
            writer.writeAttribute("id", effectName);
            writer.writeStartElement("profile_COMMON");
            
            writer.writeStartElement("newparam");
            writer.writeAttribute("sid", surfName);
            writer.writeStartElement("surface");
            writer.writeAttribute("type", "2D");
            this.writeSimpleElement("init_from", "tex" + i);
            writer.writeEndElement(); // close surface
            writer.writeEndElement(); // close newparam
            
            writer.writeStartElement("newparam");
            writer.writeAttribute("sid", samplerName);
            writer.writeStartElement("sampler2D");
            this.writeSimpleElement("source", surfName);
            writer.writeEndElement(); // close sampler2D
            writer.writeEndElement(); // close newparam
            
            writer.writeStartElement("technique");
            writer.writeAttribute("sid", "COMMON");
            writer.writeStartElement("phong");
            
            writer.writeStartElement("emission");
            this.writeSimpleElement("color", "1.0 1.0 1.0 1.0");            
            writer.writeEndElement(); // close emission
            
            writer.writeStartElement("ambient");
            this.writeSimpleElement("color", "1.0 1.0 1.0 1.0");            
            writer.writeEndElement(); // close ambient
            
            writer.writeStartElement("diffuse");
            writer.writeStartElement("texture");
            writer.writeAttribute("texture", samplerName);
            writer.writeAttribute("texcoord", "UVSET0");
            writer.writeEndElement(); // close texture
            writer.writeEndElement(); // close diffuse
            
            writer.writeStartElement("specular");
            this.writeSimpleElement("color", "1.0 1.0 1.0 1.0");            
            writer.writeEndElement(); // close specular
            
            writer.writeStartElement("shininess");
            this.writeSimpleElement("float", "20.0");            
            writer.writeEndElement(); // close shininess
            
            writer.writeStartElement("reflective");
            this.writeSimpleElement("color", "1.0 1.0 1.0 1.0");            
            writer.writeEndElement(); // close reflective
            
            writer.writeStartElement("reflectivity");
            this.writeSimpleElement("float", "0.5");            
            writer.writeEndElement(); // close reflectivity
            
            writer.writeStartElement("transparent");
            this.writeSimpleElement("color", "1.0 1.0 1.0 1.0");            
            writer.writeEndElement(); // close transparent
            
            writer.writeStartElement("transparency");
            this.writeSimpleElement("float", "0.0");            
            writer.writeEndElement(); // close transparency
            
            writer.writeEndElement(); // close phong
            writer.writeEndElement(); // close technique
            
            writer.writeStartElement("extra");
            writer.writeStartElement("technique");
            writer.writeAttribute("sid", "GOOGLEEARTH");
            this.writeSimpleElement("double_sided", "1");            
            writer.writeEndElement(); // close technique
            writer.writeEndElement(); // close extra
            
            writer.writeEndElement(); // close profile_COMMON
            writer.writeEndElement(); // close effect
        }
        
        writer.writeEndElement(); // close library_effects
    }
    
    
    public void writeMaterials(int texCount) throws XMLStreamException
    {
        writer.writeStartElement("library_materials");
        
        for (int i=1; i<=texCount; i++)
        {
            String matName = "mat" + i;
            String effectName = "effect" + i;
            
            writer.writeStartElement("material");
            writer.writeAttribute("id", matName);
            writer.writeStartElement("instance_effect");
            writer.writeAttribute("url", "#" + effectName);
            writer.writeEndElement(); // close instance_effect
            writer.writeEndElement(); // close material
        }
        
        writer.writeEndElement(); // close library_materials
    }
    
    
    /**
     * Writes a 3D coordinate tuple in the form x,y,z
     * @param x
     * @param y
     * @param z
     * @throws XMLStreamException
     */
    public void writeCoordinates3D(double x, double y, double z) throws XMLStreamException
    {
        /* x,y[,z] tuples */
        
        StringBuffer buffer = new StringBuffer(64);

        buffer.append(numberFormatter.format(x));
        buffer.append(coordSeparator);
        buffer.append(numberFormatter.format(y));
        buffer.append(coordSeparator);
        buffer.append(numberFormatter.format(z));

        buffer.append(tupleSeparator);

        writer.writeCharacters(buffer.toString());
    }
    
    
    /**
     * Writes a 2D coordinate tuple in the form u,v
     * @param u
     * @param v
     * @throws XMLStreamException
     */
    public void writeCoordinates2D(double u, double v) throws XMLStreamException
    {
        /* u,v tuples */
        
        StringBuffer buffer = new StringBuffer(40);

        buffer.append(numberFormatter.format(u));
        buffer.append(coordSeparator);
        buffer.append(numberFormatter.format(v));

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
