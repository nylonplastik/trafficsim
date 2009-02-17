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

package trafficsim.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.network.ClientInfo.ClientState;

public class ClientsProcessor extends ProcessorThread<ClientInfo>
{
    private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());

    public ClientsProcessor()
    {
    }

    public Object processRequest(ClientInfo client)
    {
    	client.setRequest(null);
    	client.setClientState(ClientState.SENDS_REQUEST);
    	return null;
    }
    
    @Override
    public void processEvent(ClientInfo client) {
        switch(client.getClientState())
        {
            case NEW_CLIENT:
                System.out.println("New client");
                client.setClientState(ClientState.SENDS_REQUEST);
                break;
            case SENDS_REQUEST:
                //System.out.println("Waiting for client");
                try {
                    InputStream is = client.getSocket().getInputStream();
                    int avail = is.available();
                    if (avail>0)
                    {
                        System.out.println("New request from client");
                        try
                        {
                        	ObjectInputStream ois = new ObjectInputStream(is);
                        	Object request = ois.readObject();
                        	client.setRequest(request);
                        	ois.close();
                        	client.setClientState(ClientState.WAITS_FOR_ANSWER);
                        } catch(ClassNotFoundException e)
                        {
                        	s_log.log(Level.SEVERE,"Class not found!!!",e);
                        }
                    }
                } catch (IOException e) {
                    s_log.log(Level.SEVERE,"Waiting for client IO exception",e);
                }
                break;
            case WAITS_FOR_ANSWER:
            	Object answer = processRequest(client);
            	if (answer!=null)
            	{
            		try
            		{
            			OutputStream os = client.getSocket().getOutputStream();
            			ObjectOutputStream oos = new ObjectOutputStream(os);
            			oos.writeObject(answer);
            			oos.close();
            		} catch(IOException e)
            		{
            			s_log.log(Level.SEVERE,"IO Exception",e);
            		}
            	}
                break;
            case DISCONNECT:
                System.out.println("Disconnecting client");
                try {
                    client.getSocket().close();
                } catch (IOException e) {
                    s_log.log(Level.WARNING,"Exception while closing socket",e);
                }
                break;
            default:
                break;
        }
        if ((client.getSocket().isConnected())&&
            (client.getClientState()!=ClientState.DISCONNECT))
        {
            addEvent((ClientInfo)client.clone());
        }
    }
        
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
