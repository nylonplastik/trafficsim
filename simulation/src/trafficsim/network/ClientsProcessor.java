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

import java.net.Socket;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import trafficsim.Model;

public class ClientsProcessor extends ProcessorThread<Socket> implements Observer
{

	private LinkedList<Socket> clients = new LinkedList<Socket>();
	private Model model = null;
	
	public ClientsProcessor(Model model)
	{
	}
	
	@Override
	public void processEvent(final Socket event) {
		// = onConnection
		// sendModel(event,this.model);
		synchronized(clients)
		{
			clients.add(event);
		}
	}
	@Override
	public synchronized void update(Observable o, Object arg) {
		// TODO: send update to all clients
		for(Socket s : clients)
		{
			// sendUpdate(s);
		}
	}
	
	public void setModel(Model model) {
		this.model = model;
	}
	
	public Model getModel() {
		return model;
	}
	
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
