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

import java.util.Observable;
import java.util.Observer;

import trafficsim.Model;
import trafficsim.network.Answer;
import trafficsim.network.ClientInfo;
import trafficsim.network.Request;

public class ClientsProcessor
	extends trafficsim.network.ClientsProcessor 
	implements Observer
{

	private Model  model = null;
	
	public ClientsProcessor(Model model)
	{
		setModel(model);
	}

	@Override
	public Object processRequest(ClientInfo client) {
		Object reqObject = client.getRequest();
		if (reqObject==null)
			return super.processRequest(client);
		Request request = (Request)reqObject;
		Object answer = null;
		switch(request.getType())
		{
			case 1:
				answer = new Answer(1,getModel());
				break;
		}
		super.processRequest(client);
		return answer;
	}

	public void setModel(Model model) {
		if (this.model != null)
			this.model.deleteObserver(this);
		this.model = model;
		if (this.model != null)
			this.model.addObserver(this);
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void update(Observable o, Object arg) {
	}
	
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
