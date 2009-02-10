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

package trafficsim;

// imports {{{
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
// }}}

/**
 * Server main
 * @author Mariusz Ceier
 */
public class Server implements Runnable //{{{
{

    // Variables {{{
    private static Logger s_log = Logger.getLogger(Server.class.toString());
    private int p_infoPort;
    private int p_requestPort;
    private String p_serverName;
    // }}}

    public Server(int infoPort, int requestPort, String serverName)//{{{
    {
        p_infoPort = infoPort;
        p_requestPort = requestPort;
        p_serverName = serverName;
    }//}}}
    
    @SuppressWarnings("serial")
    private static class InfoRequestPacket //{{{
        implements Protocol.Packet
    {
        public enum WhatInfo
        {
            MODEL,
            STATS
        };
        public WhatInfo whatInfo;
    }//}}}

    public void run() //{{{
    {
        try
        {
            DatagramSocket infoSocket = new DatagramSocket(p_infoPort);
            infoSocket.setBroadcast(true);
            InfoRequestPacket packet = new InfoRequestPacket();
            byte []infoBuffer = Protocol.serializePacket(packet);
            while(true)
            {
                DatagramPacket infoPacket = new DatagramPacket(infoBuffer,infoBuffer.length);
                infoSocket.receive(infoPacket);
                int receivedLength = infoPacket.getLength();
                if (receivedLength!=0)
                {
                    s_log.log(Level.INFO, "received packet");
                    packet = null;
                    try
                    {
                        packet = (InfoRequestPacket)Protocol.deserializePacket(infoPacket.getData(),0,infoPacket.getLength());
                    } catch(IOException e)
                    {
                        s_log.log(Level.WARNING,"ignoring exception", e);
                    }
                    if (packet!=null)
                    {
                        switch(packet.whatInfo)
                        {
                            case MODEL:
                                // TODO: sendModel(infoPacket.getSocketAddress());
                                break;
                            case STATS:
                                // TODO: sendStats(infoPacket.getSocketAddress());
                                break;
                            default:
                                s_log.log(Level.WARNING,"ignoring packet - unknown request");
                                break;
                        };
                    }
                } else
                    s_log.log(Level.WARNING,"invalid packet length");
                //Thread.sleep(1000,0);
            }
        } 
        catch(SocketException e)
        {
            s_log.log(Level.SEVERE, "socket exception", e);
        }
        catch(IOException e)
        {
            s_log.log(Level.SEVERE, "input output exception", e);
        }
        /*
        catch(InterruptedException e)
        {
        }
        */
    }//}}}

    public static void main(String []args)//{{{
    {
        // 1. defaults
        int infoPort = 12122; // UDP for new clients - allow discovery of server
        int requestsPort = 12123; // UDP for state update and notification
        String serverName = "Unnamed server"; // Default server name

        // 2. read configuration - noop for now
        
        // 3. initialise simulation - what should we do here ?
        // a) create world = model
        Model m = new Model();

        // b) open ports / create server
        Server server = new Server(infoPort, requestsPort, serverName );
        Thread serverThread = new Thread(server);
        serverThread.start();

        // c) FIXME: lights state controller on server or on some client ?
        //  * I think better is on server, as we will not have to deal 
        //    with special cases, e.g. when there is only one type of
        //    connected clients - cars - without lights state controller.
        //    if this is the case, we should initialise lights state 
        //    controller, maybe as new thread.
        LightsStateController lc = new LightsStateController(m);

        // 4. main loop:
        // a) listen for new messages (UDP)
        // b) update state
        // c) after "timeout" send current state to all clients - or send noop :p
        try
        {
            serverThread.join();
        } catch(InterruptedException e)
        {
        }
    }//}}}
}//}}}

/* vim: set ts=4 sts=4 sw=4 et foldmethod=marker: */
