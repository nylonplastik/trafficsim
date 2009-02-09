/*
    TrafficSim is simulation of road traffic
    Copyright (C) 2009  Mariusz Ceier, Adam Rutkowski

    This file is part of TrafficSim

    TrafficSim is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TrafficSim is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TrafficSim.  If not, see <http://www.gnu.org/licenses/>.
*/

package trafficSim;

// imports {{{
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
// }}}

/**
 * Client - Server Protocol Base
 * @author Mariusz Ceier
 */
public class Protocol
{

    // Variables {{{
    private static Logger s_log = Logger.getLogger(Protocol.class.toString());
    // }}}

    public static interface Packet //{{{
        extends Serializable
    {
    }//}}}

    public static byte []serializePacket(Packet packet)//{{{
        throws IOException
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(packet);
        objectStream.close();
        return byteStream.toByteArray();
    }//}}}

    public static Packet deserializePacket(byte []buffer, int offset, int length)//{{{ 
        throws IOException
    {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer,offset,length);
        ObjectInputStream objectStream = new ObjectInputStream(byteStream);
        Packet result = null;
        try
        {
            Object tmp = objectStream.readObject();
            if ((tmp!=null)&&(tmp instanceof Packet))
                result = (Packet)tmp;
        } catch(ClassNotFoundException e)
        {
            s_log.log(Level.SEVERE, "class not found", e);
        } finally
        {
            objectStream.close();
        }
        return result;
    }//}}}

}

/* vim: set ts=4 sts=4 sw=4 et foldmethod=marker: */
