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

 
 //   private Hashtable<Integer, LanesCross>            crosses;
    
    /***
     * List of all cars on the map
     */
    private LinkedList<Car>                           cars;
    
    /**
     * List of all parkings.
     */
    private LinkedList<Parking>                       parkings;
    
    /***
     * List of all cars on the map
     */        
    private ConcurrentHashMap<Integer, Car>                   carsById;
    
    /***
     * List of all lanes.
     */
    private ConcurrentHashMap<Integer, Lane>                  lanesById;  
    
    /**
     * List of all parkings.
     */    
    private ConcurrentHashMap<Integer, Parking>               parkingById;
  
    /**
     * Hashtable of all lane crosses.
     */    
    private ConcurrentHashMap<Integer, LanesCross>            crossesById;
    
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
        lanesById   = new ConcurrentHashMap<Integer, Lane>();
        cars        = new LinkedList<Car>();
        parkings    = new LinkedList<Parking>();
        carsById    = new ConcurrentHashMap<Integer, Car>();
        lanesById   = new ConcurrentHashMap<Integer, Lane>(); 
        parkingById = new ConcurrentHashMap<Integer, Parking>(); 
        crossesById = new ConcurrentHashMap<Integer, LanesCross>();
    }//}}}
    
    public synchronized int addCross(int X, int Y)//{{{
    {
        int crossId = nextCrossId++;
        crossesById.put(crossId, new LanesCross(crossId, X, Y) );
        this.setChanged();
        return crossId;
        //this.notifyObservers(WhatHasChanged.Crosses);
    }//}}}
    
    public synchronized Lane addLane(int start, int end, int maxSpeed)//{{{
    {
        if (!crossesById.containsKey(start) || !crossesById.containsKey(end))
            return null;
        
        LanesCross cStart = crossesById.get(start);
        LanesCross cEnd = crossesById.get(end);
        
        Lane newLane = new Lane(maxSpeed, cStart, cEnd);
        
        cStart.addConnection(end, newLane );
        cEnd.addIncoming(newLane);
        cStart.addOutgoing(newLane);
        
        lanesById.put(newLane.getId(), newLane);
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
    
    public synchronized ConcurrentHashMap<Integer, LanesCross> getLanesCrosses()//{{{
    {
        return crossesById;
    }//}}}
    
    public synchronized ConcurrentHashMap<Integer, Lane> getLanes()//{{{
    {
        return lanesById;
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
        newCar.goToParkingOutQueue();
        
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

    public synchronized ConcurrentHashMap<Integer, LanesCross> getCrosses() {
        return crossesById;
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
        

    public void startMoving(int carId, float acceleration) 
    {
        Car c = carsById.get(carId);
        synchronized (c)
        {
            c.startMoving(acceleration);
        }
        setChanged();
    }
    
    public synchronized void setAcceleration(int carId, float acceleration)
    {
        Car c = carsById.get(carId);
        c.setAcceleration(acceleration);
        setChanged();
    }
    
    public  synchronized void setRoute(int carId, LinkedList<Integer> route)
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

    public synchronized void gotoParkingQueue(int id) { //{{{
        Car car = carsById.get(id);
        car.goToParkingOutQueue();
    } //}}}  
        

    public synchronized void update(Model updated) {
        // shallow copy of received model
        this.cars = updated.cars;
        this.carsById = updated.carsById;
        this.crossesById = updated.crossesById;
        this.lanesById = updated.lanesById;
        this.lanesById = updated.lanesById;
        this.lastUpdate = updated.lastUpdate;
        this.nextCrossId = updated.nextCrossId;
        this.parkingById = updated.parkingById;
        this.parkings = updated.parkings;      
    }

    public void notifyObservers()
    {
        this.setChanged();
        this.notify();
    }
    
    public synchronized long getLastUpdate()
    {
    	return lastUpdate;
    }
 
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
