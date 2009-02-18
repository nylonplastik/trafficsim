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

package trafficsim.network.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.Model;
import trafficsim.network.ConnectionInfo;
import trafficsim.network.Packet;
import trafficsim.network.PacketTypes;
import trafficsim.network.ProcessorThread;
import trafficsim.network.server.ClientsProcessor;

public class ServerProcessor extends trafficsim.network.ConnectionProcessor
{

	private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());
	private Model model = null;
	
	public ServerProcessor(Model model)
	{
		this.model = model;
	}

	public ServerProcessor(Model model, ProcessorThread<ConnectionInfo> p) {
		super(p);
		this.model = model;
	}
	
	@Override
	public void processRequest(ConnectionInfo client) {
		try {
			if (client.isDataAvailable())
			{
				Object ansObject = client.readObject();
				if (ansObject==null)
				{
					addEvent((ConnectionInfo)client.clone());
					return;
				}
				Packet answer = (Packet)ansObject;
				switch(answer.getType())
				{
					case PacketTypes.UPDATE_ANSWER_TYPEID:
						// server sends model
						model.update((Model)answer.getData());
						break;
				}
			}
		} catch (IOException e) {
			s_log.log(Level.SEVERE,"IO exception",e);
		} catch (ClassNotFoundException e) {
			s_log.log(Level.SEVERE,"Class not found exception",e);
		} catch (InterruptedException e) {
			s_log.log(Level.SEVERE,"ServerProcessor Interrupted",e);
		}
		try {
			addEvent((ConnectionInfo)client.clone());
		} catch (InterruptedException e) {
		}
	}
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
