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
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.ClientViewClientSide;
import trafficsim.data.ClientViewData;
import trafficsim.ICarController;
import trafficsim.Model;
import trafficsim.network.ConnectionInfo;
import trafficsim.network.Packet;
import trafficsim.network.PacketTypes;
import trafficsim.network.ProcessorThread;
import trafficsim.network.server.ClientsProcessor;
import trafficsim.data.*;

public class ServerProcessor extends trafficsim.network.ConnectionProcessor
{

	private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());
	private Model model = null;
        private ICarController controller = null;
        private ClientViewClientSide view = null;
        private int clientId = 0;
        private boolean registered;
        private boolean controllerStarted = false;
        private ConnectionInfo currentClient = null;
	
	public ServerProcessor(ICarController controller, ClientViewClientSide view, Model model)
	{
                this.model = model;
                this.view = view;
                this.controller = controller;
	}

	public ServerProcessor(ICarController controller, 
                               ClientViewClientSide view,
                               Model model,
                               ProcessorThread<ConnectionInfo> p
                               ) 
        {
		super(p);
                this.model = model;
                this.view = view;
                this.controller = controller;                
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
					case PacketTypes.NEW_MODEL_DATA:
                                            // server sends model
                                            model.update((Model)answer.getData());
                                            if (!isControllerStarted())
                                            {
                                                setControllerStarted(true);
                                                // tell the controller that
                                                // we're ready to start
                                                controller.registered();
                                            }
                                            break;
                                        case PacketTypes.REGISTRED_TYPEID:
                                            // save assigned id number
                                            setClientId((int) (Integer)answer.getData());
                                            // send model update request
                                            Packet p = new Packet(PacketTypes.NEW_MODEL_DATA_TYPEID);
                                            client.writeObject(p);
                                            break;
                                        case PacketTypes.NEW_CAR_SPAWNED:
                                            System.out.println("New car spawned");
                                            controller.newCarCallback((Integer)answer.getData());
                                            break;
                                        case PacketTypes.MODEL_DATA_UPDATE_TYPEID:
                                            currentClient = client;
                                            view.viewChanged((ClientViewData)answer.getData());
                                            currentClient = null;
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
        
        protected void sendData (Serializable data, int packetType)
        {
            ConnectionInfo server = getEvents().poll();
            if (server!=null)
            {
                Packet packet = new Packet(packetType, data);
                try
                {
                        server.writeObject(packet);
                } catch(IOException e)
                {
                        s_log.log(Level.SEVERE,"Can't send data to server",e);
                }
                try
                {
                    addEvent((ConnectionInfo)server.clone());
                } catch (InterruptedException e)
                {
                    s_log.log(Level.SEVERE,"Can't reinsert server",e);
                }
            }
        }
        
        protected void sendData (int packetType)
        {
            
            ConnectionInfo server = getEvents().poll();
            if (server!=null)
            {
                Packet packet = new Packet(packetType);
                try
                {
                        server.writeObject(packet);
                } catch(IOException e)
                {
                        s_log.log(Level.SEVERE,"Can't send data to server",e);
                }
                try
                {
                    addEvent((ConnectionInfo)server.clone());
                } catch (InterruptedException e)
                {
                    s_log.log(Level.SEVERE,"Can't reinsert server",e);
                }
            }
        }        
        
        public void register()
        {
            sendData(new Integer(0), PacketTypes.REGISTER_CLIENT_TYPEID);
        }
        
        public void gotoParkingQueue(int newCarId) {
            sendData(newCarId, PacketTypes.PUT_CAR_IN_QUEUE);
        }
        
        public void changeAcceleration(float newAcceleration, int carId)
        {
            if (currentClient != null)
            {
                startMovingData data = new startMovingData(newAcceleration, carId);
                Packet packet = new Packet(PacketTypes.CHANGE_ACCELER_TYPEID, data);
                try
                {
                    currentClient.writeObject(packet);
                }
                catch (IOException e)
                {}
            }
        }
        
        public void changePlanneRoute(LinkedList<Integer> route, int carId)
        {
            if (currentClient != null)
            {
                changePlannedRouteData data = new changePlannedRouteData(route, carId);
                Packet packet = new Packet(PacketTypes.CHANGE_ROUTE_TYPEID,data);
                try
                {
                    currentClient.writeObject(packet);
                }
                catch (IOException e)
                {}
            }
        }    
        
        public void startMoving(float initialAcceleration, int id)
        {
            if (currentClient != null)
            {
                startMovingData data = new startMovingData(initialAcceleration, id);
                Packet packet = new Packet(PacketTypes.START_MOVING,data);
                try
                {
                    currentClient.writeObject(packet);
                }
                catch (IOException e)
                {}
            }
        }        

        public void newCar(int parkingId) {
            sendData(parkingId,PacketTypes.SPAWN_NEW_CAR);
        }

    public synchronized int getClientId() {
        return clientId;
    }

    public synchronized void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public synchronized boolean isRegistered() {
        return registered;
    }

    public synchronized void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isControllerStarted() {
        return controllerStarted;
    }

    public void setControllerStarted(boolean controllerStarted) {
        this.controllerStarted = controllerStarted;
    }
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
