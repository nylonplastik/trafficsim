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
import trafficsim.data.*;

 public class ClientsProcessor
	extends trafficsim.network.ConnectionProcessor 
{

	private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());

	private Model model = null;
    private Server server = null;
	
	public ClientsProcessor(Model model, Server server)
	{
	    super();
		setModel(model);
        this.server = server;
	}

	public ClientsProcessor(ClientsProcessor p) {
		super(p);
		setModel(p.getModel());
        this.server = p.server;
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
					switch(request.getType())
					{
						case PacketTypes.NEW_MODEL_DATA_TYPEID:
							System.out.println("Update request");
							Model m = getModel();
							synchronized(m)
							{
								long lastUpdate = m.getLastUpdate();
								Object answer = new Packet(PacketTypes.NEW_MODEL_DATA,m);
								client.setLastUpdate(lastUpdate);
								client.writeObject(answer);
							}
							break;
						case PacketTypes.REGISTER_CLIENT_TYPEID:
							System.out.println("Register request");
                            Integer newId = server.newClient();
                            client.setClientId(newId);
							Packet answer = new Packet(PacketTypes.REGISTRED_TYPEID, newId);
                            client.writeObject(answer);
							break;  
						case PacketTypes.SPAWN_NEW_CAR:
                            System.out.println("Spawn car request");
                            int newCarId = model.newCar((Integer)request.getData());
                            server.addObservedCar(client.getClientId(), newCarId);
                            answer = new Packet(PacketTypes.NEW_CAR_SPAWNED, newCarId);
                            client.writeObject(answer);
							break;  
                        case PacketTypes.PUT_CAR_IN_QUEUE:
                            System.out.println("Put car in queue request");
                            m = getModel();
                            m.gotoParkingQueue((Integer)request.getData());
                            break;
                        case PacketTypes.START_MOVING:
                            startMovingData data = (startMovingData)request.getData();
                            model.startMoving(data.carId, data.acceleration);
                            break;
                        case PacketTypes.CHANGE_ACCELER_TYPEID:
                            System.out.println("Change acceleration request");
                            data = (startMovingData)request.getData();
                            model.setAcceleration(data.carId, data.acceleration);
                            break;
                        case PacketTypes.CHANGE_ROUTE_TYPEID:
                            System.out.println("Change route request");
                            changePlannedRouteData routeData = (changePlannedRouteData)request.getData();
                            model.setRoute(routeData.carId, routeData.plannedRoute);
                            break;
					}
				}
			}

            boolean bRet = server.wasViewUpdated(client.getClientId());
            if (bRet)
            {
                ClientViewData data = server.getViewData(client.getClientId());
                Packet update = new Packet(PacketTypes.MODEL_DATA_UPDATE_TYPEID, data);
                client.writeObject(update);
            }                        
                        
		} catch(ClassNotFoundException e)
		{
			s_log.log(Level.SEVERE,"Class not found exception",e);
		} catch (IOException e) {
			s_log.log(Level.SEVERE,"Clients processor IO exception",e);
		}
                
		try {
			addEvent((ConnectionInfo)client.clone());
		} catch (InterruptedException e) {
			s_log.log(Level.SEVERE,"Can't add event",e);
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
