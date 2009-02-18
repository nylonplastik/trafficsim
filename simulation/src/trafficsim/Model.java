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

package trafficsim;

// imports {{{
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.*;
//}}}

/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class Model extends Observable implements Serializable //{{{
{
    // Variables {{{

    private int nextCrossId = 1;
    private long lastUpdate = 0;

    /**
     * Hashtable of all lane crosses.
     */
    private Hashtable<Integer, LanesCross>            crosses;
    
    /***
     * List of all lanes.
     */
    private LinkedList<Lane>                          lanes;
    
    /***
     * List of all cars on the map
     */
    private LinkedList<Car>                           cars;
    
    /**
     * List of all parkings.
     */
    private LinkedList<Parking>                       parkings;
    
    
    private ConcurrentHashMap<Integer, Car>                   carsById;
    private ConcurrentHashMap<Integer, Lane>                  lanesById;  
    private ConcurrentHashMap<Integer, Parking>               parkingById;
    
    private static final int            FAILED = -1;


    //}}}

    /**
     * It is passed as argument to observers
     */
    public enum WhatHasChanged//{{{
    {
        Nothing,
        Crosses,
        Lanes,
        Cars,
        Parkings,
        TimeUpdate
    }//}}}

    public Model()//{{{
    {
        crosses     = new Hashtable<Integer, LanesCross>();
        lanes       = new LinkedList<Lane>();
        cars        = new LinkedList<Car>();
        parkings    = new LinkedList<Parking>();
        carsById    = new ConcurrentHashMap<Integer, Car>();
        lanesById   = new ConcurrentHashMap<Integer, Lane>(); 
        parkingById = new ConcurrentHashMap<Integer, Parking>(); 
    }//}}}
    
    public synchronized int addCross(int X, int Y)//{{{
    {
        int crossId = nextCrossId++;
        crosses.put(crossId, new LanesCross(crossId, X, Y) );
        this.setChanged();
        return crossId;
        //this.notifyObservers(WhatHasChanged.Crosses);
    }//}}}
    
    public Lane addLane(int start, int end, int maxSpeed)//{{{
    {
        if (!crosses.containsKey(start) || !crosses.containsKey(end))
            return null;
        
        LanesCross cStart = crosses.get(start);
        LanesCross cEnd = crosses.get(end);
        
        Lane newLane = new Lane(maxSpeed, cStart, cEnd);
        
        cStart.addConnection(end, newLane );
        cEnd.addIncoming(newLane);
        cStart.addOutgoing(newLane);
        
        lanes.add(newLane);
        lanesById.put(newLane.getId(), newLane);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
        
        return newLane;
    }//}}}
    
    public synchronized Parking addParking(Lane lane_to_cross, Lane lane_to_parking)//{{{
    {
        Parking parking = new Parking(lane_to_cross, lane_to_parking);
        parkings.add(parking);
        parkingById.put(parking.getId(), parking);
        this.setChanged();
        return parking;
        //this.notifyObservers(WhatHasChanged.Parkings);
    }//}}}
    
    public synchronized void setAdjecent(Lane lane1, Lane lane2)//{{{
    {
        lane1.addAdjecent(lane2);
        lane2.addAdjecent(lane1);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
    }//}}}
    
    public synchronized LinkedList<Car> getCars()//{{{
    {
        return cars;
    }//}}}
    
    public synchronized LinkedList<Parking> getParkings()//{{{
    {
        return parkings;
    }//}}}
    
    public synchronized Hashtable<Integer, LanesCross> getLanesCrosses()//{{{
    {
        return crosses;
    }//}}}
    
    public synchronized LinkedList<Lane> getLanes()//{{{
    {
        return lanes;
    }//}}}
    
    public synchronized void updateCarsState(LinkedList<Car> cars)//{{{
    {
        
    }//}}}
     
    public synchronized int newCar(int parkinId)//{{{
    {
        if (!parkingById.keySet().contains(parkinId))
            return FAILED;
        Parking parking = parkingById.get(parkinId);
        
        Car newCar = parking.newCar();  
  
        synchronized(cars)
        {
            cars.add(newCar);
        }
        
        carsById.put(newCar.getId(), newCar);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Cars);
        return newCar.getId();
    }//}}}    

    public synchronized void finishedTimeUpdate()//{{{
    {
        this.setChanged();
        this.notifyObservers(WhatHasChanged.TimeUpdate);
    }//}}}

    public static Model loadModel(String fileName) throws FileNotFoundException
    {
        Model newModel = null;
        FileInputStream is = new FileInputStream(fileName);
        XMLDecoder decoder = new XMLDecoder(is);
        newModel = (Model)decoder.readObject();
        decoder.close();
        return newModel;
    }
    
    public synchronized void saveModel(String fileName) throws FileNotFoundException
    {
        FileOutputStream os = new FileOutputStream(fileName);
        XMLEncoder encoder = new XMLEncoder(os);
        encoder.writeObject(this);
        encoder.close();
    }
    
    public synchronized void setPlannedRoute(int carId, LinkedList<Integer> route)
    {
        Car car;
        
        car = carsById.get(carId);
        
        LinkedList<Lane> newRoute = new LinkedList<Lane>();
        
        for (int i : route)
            newRoute.add(lanesById.get(i));
        
        car.setPlannedRoute(newRoute);
        setChanged();
    }

    public synchronized Hashtable<Integer, LanesCross> getCrosses() {
        return crosses;
    }

    public synchronized void setCrosses(Hashtable<Integer, LanesCross> crosses) {
        this.crosses = crosses;
    }

    public synchronized void setLanes(LinkedList<Lane> lanes) {
        this.lanes = lanes;
    }

    public synchronized void setCars(LinkedList<Car> cars) {
        this.cars = cars;
    }

    public synchronized void setParkings(LinkedList<Parking> parkings) {
        this.parkings = parkings;
    }
        
        public synchronized Car getCarById(int id)
        {
            return carsById.get(id);
        }
        
        public synchronized Lane getLaneById (int id)
        {
            return lanesById.get(id);
        }
        
        public synchronized Parking getParkingById (int id)
        {
            return parkingById.get(id);
        }
        

    void startMoving(int carId, int acceleration) 
    {
        Car c = carsById.get(carId);
        synchronized (c)
        {
            c.startMoving(acceleration);
        }
        setChanged();
    }
    
    synchronized void setAcceleration(int carId, float acceleration)
    {
        Car c = carsById.get(carId);
        synchronized(c)
        {
            c.setAcceleration(acceleration);
        }
        setChanged();
    }
    
    synchronized void setRoute(int carId, LinkedList<Integer> route)
    {
        LinkedList<Lane> newRoute = new LinkedList<Lane>();
        
        // reconstuct the route
        for (Integer i : route)
        {
            newRoute.add(lanesById.get(i));
        }
        
        Car c = carsById.get(carId);
        synchronized(c)
        {
            c.setRoute(newRoute);
        }      
        setChanged();
    }
        
    @Override
    public synchronized void setChanged()
    {
    	lastUpdate = System.nanoTime();
    	super.setChanged();
    }

    synchronized void gotoParkingQueue(int id) { //{{{
        Car car = carsById.get(id);
        car.goToParkingOutQueue();
    } //}}}  
        
        
        
    /* TODO TO BE REMOVED WHEN COMMUNICATION IS ON*/
    public synchronized int addCross(int X, int Y, int id)//{{{
    {
        crosses.put(id, new LanesCross(id, X, Y) );
        this.setChanged();
        return id;
        //this.notifyObservers(WhatHasChanged.Crosses);
    }//}}}
    
    /* TODO TO BE REMOVED WHEN COMMUNICATION IS ON*/
    public Lane addLane(int start, int end, int maxSpeed, int id)//{{{
    {
        if (!crosses.containsKey(start) || !crosses.containsKey(end))
            return null;
        
        LanesCross cStart = crosses.get(start);
        LanesCross cEnd = crosses.get(end);
        
        Lane newLane = new Lane(maxSpeed, cStart, cEnd, id);
        
        cStart.addConnection(end, newLane );
        cEnd.addIncoming(newLane);
        cStart.addOutgoing(newLane);
        
        lanes.add(newLane);
        lanesById.put(newLane.getId(), newLane);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
        
        return newLane;
    }//}}}
    
    /* TODO TO BE REMOVED WHEN COMMUNICATION IS ON*/
    public synchronized Parking addParking(Lane lane_to_cross, Lane lane_to_parking, int id)  
    {//{{{
        Parking parking = new Parking(lane_to_cross, lane_to_parking, id);
        parkings.add(parking);
        parkingById.put(parking.getId(), parking);
        this.setChanged();
        return parking;
        //this.notifyObservers(WhatHasChanged.Parkings);
    }//}}}  

	public synchronized void update(Model updated) {
		// updates this model from updated model
	}
    
    public synchronized long getLastUpdate()
    {
    	return lastUpdate;
    }
 
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
