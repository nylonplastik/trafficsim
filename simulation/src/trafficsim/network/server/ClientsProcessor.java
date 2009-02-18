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

package trafficsim.network.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.Model;
import trafficsim.Server;
import trafficsim.network.ConnectionInfo;
import trafficsim.network.Packet;
import trafficsim.network.PacketTypes;
import trafficsim.network.ProcessorThread;

 public class ClientsProcessor
	extends trafficsim.network.ConnectionProcessor 
{

	private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());

	private Model model = null;
        private Server server = null;
	
	public ClientsProcessor(Model model, Server server)
	{
		setModel(model);
                this.server = server;
	}

	public ClientsProcessor(Model model, Server server, ProcessorThread<ConnectionInfo> p) {
		super(p);
		setModel(model);
                this.server = server;
	}

	@Override
	public void processRequest(ConnectionInfo client) {
		try
		{
			if (client.isDataAvailable())
			{
				Object reqObject = client.readObject();
				if (reqObject!=null)
				{
					Packet request = (Packet)reqObject;
					Object answer = null;
					switch(request.getType())
					{
						case PacketTypes.UPDATE_REQUEST_TYPEID:
							System.out.println("Update request");
							long lastUpdate = getModel().getLastUpdate();
							answer = new Packet(PacketTypes.UPDATE_ANSWER_TYPEID,getModel());
							client.setLastUpdate(lastUpdate);
							break;
						case PacketTypes.REGISTER_CLIENT_TYPEID:
							System.out.println("Register request");
                                                        Integer newId = server.newClient();
                                                        client.setClientId(newId);
							answer = new Packet(PacketTypes.REGISTRED_TYPEID, newId);
							break;                                                        
                                                        
					}
					if (answer!=null)
					{
						try
						{
							client.writeObject(answer);
						} catch(IOException e)
						{
							s_log.log(Level.SEVERE,"Can't send answer",e);
						}
					}
				}
			}
		} catch(ClassNotFoundException e)
		{
			s_log.log(Level.SEVERE,"Class not found exception",e);
		} catch (IOException e) {
			s_log.log(Level.SEVERE,"Cant read object",e);
		}
                
                /*
		if (client.getLastUpdate()<model.getLastUpdate())
		{
			try
			{
				long lastUpdate = getModel().getLastUpdate();
				client.writeObject(new Packet(PacketTypes.UPDATE_ANSWER_TYPEID,getModel()));
				client.setLastUpdate(lastUpdate);
			} catch(IOException e)
			{
				s_log.log(Level.SEVERE,"Can't send update",e);
			}
		}
                 * */
                
		try {
			addEvent((ConnectionInfo)client.clone());
		} catch (InterruptedException e) {
			s_log.log(Level.SEVERE,"Can't add event",e);
		}
	}

	public void setModel(Model model) {
		/*
		if (this.model != null)
			this.model.deleteObserver(this);
		*/
		this.model = model;
		/*
		lastUpdate = System.nanoTime();
		if (this.model != null)
			this.model.addObserver(this);
		*/
	}

	public Model getModel() {
		return model;
	}

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
