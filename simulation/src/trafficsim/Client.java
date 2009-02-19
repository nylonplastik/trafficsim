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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import trafficsim.network.ClientThread;
import trafficsim.network.client.ServerProcessor;

/**
 *
 * @author Adam Rutkowski
 */
public class Client //{{{
{
    // Variables {{{
    private ClientViewClientSide p_view;
    private ClientController1    p_controller;
    private Model                p_model;
    
    private static Logger s_log = Logger.getLogger(Client.class.toString());
    
    private ClientThread ct = null;
    private ServerProcessor sp = null;
    //}}}

    public Client()
    {
        this(0);
    }
 
    public Client(int nAdditionalProcessors)//{{{
    {
        p_model = new Model();
        p_view = new ClientViewClientSide(p_model);
        p_controller =new ClientController1(p_view, p_model);
        p_view.setController(p_controller);
        
        // Client
      
        sp = new ServerProcessor(p_controller, p_view, p_model);
        
        try {
            ct = new ClientThread(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),23456));
            ct.setConnectionProcessor(sp);
        } catch (UnknownHostException e2) {
            s_log.log(Level.SEVERE,"wtf?",e2);
            System.exit(1);
        }

        new Thread(sp).start();
        for(int i=0;i<nAdditionalProcessors;++i)
            new Thread(new ServerProcessor(sp));
        //new Thread(ct).start();
        ct.run();

        p_controller.setNetworkClient(sp);
        p_controller.start();
        /*
        try {
        	for(ConnectionInfo cc : sp.getEvents())
        		cc.writeObject(new Packet(PacketTypes.UPDATE_REQUEST_TYPEID));
		} catch(IOException e)
        {
			s_log.log(Level.SEVERE,"Can't send request",e);        	
        }
         * */
        
    }//}}}

    public void close() {
    }
    
    public static void main(String []args)
    {
        new Client(5);
    }
    
    
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
