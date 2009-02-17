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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.Model;
import trafficsim.network.ServerInfo.ServerState;

public class ServerProcessor extends ProcessorThread<ServerInfo> implements Observer
{
    private static Logger s_log = Logger.getLogger(ServerProcessor.class.toString());

	private long currentUpdate = 0;
    private Model model = null;
    
    public ServerProcessor(Model model)
    {
    	currentUpdate = System.nanoTime();
    	setModel(model);
    }

    @Override
    public void processEvent(ServerInfo server) {
    	switch(server.getServerState())
    	{
    		case CONNECTED:
    			server.setServerState(ServerState.WAIT_FOR_CLIENT);
    			break;
    		case WAIT_FOR_CLIENT:
    			//System.out.println("Client: Server waits for client");
    			try
    			{
    				OutputStream os = server.getSocket().getOutputStream();
    				BufferedOutputStream bos = new BufferedOutputStream(os);
    				PrintWriter pw = new PrintWriter(bos);
    				pw.println("Hello world");
    				pw.flush();
    				bos.flush();
    				os.flush();
    				server.setServerState(ServerState.WAITS_FOR_UPDATE);
    			} catch(IOException e)
    			{
					s_log.log(Level.SEVERE,"IO Exception",e);    				
    			}
    			break;
    		case WAITS_FOR_UPDATE:
				try {
					InputStream is = server.getSocket().getInputStream();
					int avail = is.available();
					if (avail>0)
					{
						//processUpdate(is);
						server.setServerState(ServerState.DISCONNECT);
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
    		ServerInfo sinfo = new ServerInfo(server.getSocket());
    		sinfo.setLastUpdate(server.getLastUpdate());
    		sinfo.setServerState(server.getServerState());
    		addEvent(sinfo);
    	} else
    		System.out.println("Not connected to server");
    }
    
    @Override
    public synchronized void update(Observable o, Object arg) {
    	currentUpdate = System.nanoTime();
    }
    
    public void setModel(Model model) {
    	if (this.model!=null)
    		this.model.deleteObserver(this);
        this.model = model;
    	if (this.model!=null)
    		this.model.addObserver(this);
    }
    
    public Model getModel() {
        return model;
    }
    
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
