/*
 * CADI Software - a JPIP Client/Server framework
 * Copyright (C) 2007  Group on Interactive Coding of Images (GICI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Group on Interactive Coding of Images (GICI)
 * Department of Information and Communication Engineering
 * Autonomous University of Barcelona
 * 08193 - Bellaterra - Cerdanyola del Valles (Barcelona)
 * Spain
 *
 * http://gici.uab.es
 * gici-info@deic.uab.es
 */

package org.vast.jpip.cache;

import java.io.PrintStream;


/**
 *
 * @author Group on Interactive Coding of Images (GICI)
 * @version beta 0.2
 */
public class CacheDescriptor
{

    /**
     * Indicates the data-bin class.
     * 
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     * Cambiar la clase DataBinClass para que solo tenga estes valores, y entonces,
     * los valores EXTENDED activar una opcion en las correspondientes funciones donde
     * se indique que es un valor EXTENDED.
     */
    public int Class;
    public static final int PRECINCT = 0;
    public static final int TILE_HEADER = 2;
    public static final int TILE_DATA = 4;
    public static final int MAIN_HEADER = 6;
    public static final int METADATA = 8;

    /**
     * Indicates whether explicit form or implicit form is used.
     * <p>
     * Default value: true
     */
    public boolean explicitForm;

    /**
     * 
     */
    public boolean aditive = true;

    /**
     * Definition in {@link CADI.Common.Network.JPIP.JPIPMessageHeader#inClassIdentifier}
     * <p>
     * Only is used with the form. If the value is -1, then indicates a wildcard. 
     */
    public long InClassIdentifier;

    /**
     * Indicates the precinct when the implicit form is used.
     */
    public int[] tileRange = new int[2];
    public int[] componentRange = new int[2];
    public int[] resolutionLevelRange = new int[2];
    public int[] precinctRange = new int[2];

    /**
     * Indicates the number of layers (packets)
     * <p>
     *  Default value for both index are -1. This value indicates that the
     * descriptor is specified by means of the index range. And if both,
     * indexRange and numberOfLayers are -1, then the descriptor is a wildcard.
     */
    public int numberOfLayers;

    /**
     * Indicates the number of bytes.
     * <p>
     * Default value is -1.
     */
    public int numberOfBytes;


    /**************************************************************************/
    /**                        PRINCIPAL METHODS                             **/
    /**************************************************************************/

    /**
     * Constructor.
     */
    public CacheDescriptor()
    {
        reset();
    }


    /**
     * Sets the attritutes to its initial values.
     */
    public void reset()
    {
        Class = -1;
        explicitForm = true;
        aditive = true;
        InClassIdentifier = -1;
        tileRange[0] = tileRange[1] = -1;
        componentRange[0] = componentRange[1] = -1;
        resolutionLevelRange[0] = resolutionLevelRange[1] = -1;
        precinctRange[0] = precinctRange[1] = -1;
        numberOfLayers = -1;
        numberOfBytes = -1;
    }


    /**
     * For debugging purposes. 
     */
    public String toString()
    {
        String str = "";

        str = getClass().getName() + " [";

        str += "Class: ";
        switch (Class)
        {
        case PRECINCT:
            str += "Precinct\n";
            break;
        case TILE_HEADER:
            str += "Tile header\n";
            break;
        case TILE_DATA:
            str += "Tile\n";
            break;
        case MAIN_HEADER:
            str += "Main header\n";
            break;
        case METADATA:
            str += "Metadata\n";
            break;
        }

        str += "Form: " + (explicitForm ? "explicit\n" : "implicit\n");

        str += "Identifier: ";
        if (explicitForm)
        {
            str += InClassIdentifier + "\n";
        }
        else
        {
            str += "Tile: " + tileRange[0] + "-" + tileRange[1] + "\n";
            str += "Component: " + componentRange[0] + "-" + componentRange[1] + "\n";
            str += "Resolution Level: " + resolutionLevelRange[0] + "-" + resolutionLevelRange[1] + "\n";
            str += "Precinct: " + precinctRange[0] + "-" + precinctRange[1] + "\n";
        }

        str += aditive ? "Aditive\n" : "Sustractive\n";

        str += "Qualifier: ";

        if (numberOfLayers != -1)
        {
            str += numberOfLayers + " Layers\n";
        }
        else
        {
            if (numberOfBytes != -1)
            {
                str += numberOfBytes + " Bytes\n";
            }
            else
            {
                str += "none\n";
            }
        }

        str += "]";

        return str;
    }


    /**
     * Prints this Cache Descriptor fields out to the
     * specified output stream. This method is useful for debugging.
     * 
     * @param out an output stream.
     */
    public void list(PrintStream out)
    {

        out.println("-- Cache Descriptor --");

        out.print("Class: ");
        switch (Class)
        {
        case PRECINCT:
            out.println("precinct");
            break;
        case TILE_HEADER:
            out.println("tile header");
            break;
        case TILE_DATA:
            out.println("tile");
            break;
        case MAIN_HEADER:
            out.println("main header");
            break;
        case METADATA:
            out.println("metadata");
            break;
        }

        out.println("Form: " + (explicitForm ? "explicit" : "implicit"));

        out.print("Identifier: ");
        if (explicitForm)
        {
            out.println(InClassIdentifier);
        }
        else
        {
            out.println("Tile: " + tileRange[0] + "-" + tileRange[1]);
            out.println("Component: " + componentRange[0] + "-" + componentRange[1]);
            out.println("Resolution Level: " + resolutionLevelRange[0] + "-" + resolutionLevelRange[1]);
            out.println("Precinct: " + precinctRange[0] + "-" + precinctRange[1]);
        }

        out.println(aditive ? "Aditive\n" : "Sustractive");

        out.println("Qualifier: ");

        if (numberOfLayers != -1)
        {
            out.println(numberOfLayers + " Layers");
        }
        else
        {
            if (numberOfBytes != -1)
            {
                out.println(numberOfBytes + " Bytes");
            }
            else
            {
                out.println("none");
            }
        }

        out.flush();
    }

}
