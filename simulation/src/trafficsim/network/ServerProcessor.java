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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.network.ServerInfo.ServerState;

public class ServerProcessor extends ProcessorThread<ServerInfo>
{
    private static Logger s_log = Logger.getLogger(ServerProcessor.class.toString());

    private LinkedBlockingQueue<Object> requests = 
    		new LinkedBlockingQueue<Object>(); 
    
    protected LinkedBlockingQueue<Object> getRequests()
    {
    	return requests;
    }
    
    public ServerProcessor()
    {
    }

    public void addRequest(Object request) throws InterruptedException
    {
		requests.put(request);
    }

    public void processAnswer(ServerInfo server, Object answer)
    {
    	server.setServerState(ServerState.WAITS_FOR_REQUEST);
    }

    @Override
    public void processEvent(ServerInfo server) {
        switch(server.getServerState())
        {
            case CONNECTED:
                server.setServerState(ServerState.WAITS_FOR_REQUEST);
                break;
            case WAITS_FOR_REQUEST:
                //System.out.println("Client: Server waits for client");
            	Object request = requests.poll();
            	if (request != null)
            	{
            		try
            		{
	            		OutputStream os = server.getSocket().getOutputStream();
	            		ObjectOutputStream oos = new ObjectOutputStream(os);
	            		oos.writeObject(request);
	            		oos.close();
	                    server.setServerState(ServerState.SENDS_ANSWER);            		
            		} catch(IOException e)
            		{
            			s_log.log(Level.SEVERE,"IO Exception",e);
            		}
            	}
                break;
            case SENDS_ANSWER:
                try {
                    InputStream is = server.getSocket().getInputStream();
                    int avail = is.available();
                    if (avail>0)
                    {
                    	ObjectInputStream ois = new ObjectInputStream(is);
                    	try
                    	{
                    		Object answer = ois.readObject();
                    		processAnswer(server, answer);
                    	} catch(ClassNotFoundException e)
                    	{
                    		s_log.log(Level.SEVERE,"Class not found",e);
                    	}
                    	ois.close();
                    }
                } catch (IOException e1) {
                    s_log.log(Level.SEVERE,"IO Exception while waiting for update",e1);
                }
                break;
            case DISCONNECT:
                try {
                    server.getSocket().close();
                } catch (IOException e) {
                    s_log.log(Level.WARNING,"Exception while closing socket",e);
                }
                return;
            default:
                break;
        }
        if (server.getSocket().isConnected())
        {
            addEvent((ServerInfo)server.clone());
        } else
            System.out.println("Not connected to server");
    }
    
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
