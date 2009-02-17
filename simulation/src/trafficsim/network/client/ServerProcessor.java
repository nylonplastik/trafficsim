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

import trafficsim.network.Answer;
import trafficsim.network.ServerInfo;

public class ServerProcessor extends trafficsim.network.ServerProcessor
{

	@Override
	public void processAnswer(ServerInfo server, Object ansObject) {
		if (ansObject==null)
		{
			super.processAnswer(server, ansObject);
			return;
		}
		Answer answer = (Answer)ansObject;
		switch(answer.getType())
		{
			case 1:
				// server sends model
				System.out.println("updating model");
				break;
		}
		super.processAnswer(server, ansObject);
	}
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
