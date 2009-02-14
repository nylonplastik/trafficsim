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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
//}}}

/**
 *
 * @author Adam Rutkowski
 */
public class Model extends Observable //{{{
{
    // Variables {{{

    private int nextCrossId = 1;
    
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
    
    
    private Hashtable<Integer, Car>                   carsById;
    private Hashtable<Integer, Lane>                  lanesById;  
    private Hashtable<Integer, Parking>               parkingById;
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
        carsById    = new Hashtable<Integer, Car>();
        lanesById   = new Hashtable<Integer, Lane>(); 
        parkingById = new Hashtable<Integer, Parking>(); 
    }//}}}
    
    public int addCross(int X, int Y)//{{{
    {
        int crossId = nextCrossId++;
        crosses.put(crossId, new LanesCross(crossId, X, Y) );
        this.setChanged();
        return crossId;
        //this.notifyObservers(WhatHasChanged.Crosses);
    }//}}}
    
    public Lane addLane(int start, int end, int maxSpeed, int length)//{{{
    {
        if (!crosses.containsKey(start) || !crosses.containsKey(end))
            return null;
        
        LanesCross cStart = crosses.get(start);
        LanesCross cEnd = crosses.get(end);
        
        Lane newLane = new Lane(maxSpeed, length, cStart, cEnd);
        
        cStart.addConnection(end, newLane );
        cEnd.addIncoming(newLane);
        cStart.addOutgoing(newLane);
        
        lanes.add(newLane);
        lanesById.put(newLane.getId(), newLane);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
        
        return newLane;
    }//}}}
    
    public Parking addParking(Lane lane_to_cross, Lane lane_to_parking)//{{{
    {
        Parking parking = new Parking(lane_to_cross, lane_to_parking);
        parkings.add(parking);
        parkingById.put(parking.getId(), parking);
        this.setChanged();
        return parking;
        //this.notifyObservers(WhatHasChanged.Parkings);
    }//}}}
    
    public void setAdjecent(Lane lane1, Lane lane2)//{{{
    {
        lane1.addAdjecent(lane2);
        lane2.addAdjecent(lane1);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
    }//}}}
    
    public LinkedList<Car> getCars()//{{{
    {
        return cars;
    }//}}}
    
    public LinkedList<Parking> getParkings()//{{{
    {
        return parkings;
    }//}}}
    
    public Hashtable<Integer, LanesCross> getLanesCrosses()//{{{
    {
        return crosses;
    }//}}}
    
    public LinkedList<Lane> getLanes()//{{{
    {
        return lanes;
    }//}}}
    
    public void updateCarsState(LinkedList<Car> cars)//{{{
    {
        
    }//}}}
     
    public int newCar(Parking where)//{{{
    {
        Car newCar = where.newCar();
        cars.add(newCar);
        carsById.put(newCar.getId(), newCar);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Cars);
        return newCar.getId();
    }//}}}

    public void finishedTimeUpdate()//{{{
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
    
    public void saveModel(String fileName) throws FileNotFoundException
    {
    	FileOutputStream os = new FileOutputStream(fileName);
    	XMLEncoder encoder = new XMLEncoder(os);
    	encoder.writeObject(this);
    	encoder.close();
    }
    
    public void setPlannedRoute(int carId, LinkedList<Integer> route)
    {
        Car car;
        
        synchronized (carsById)
        {
            car = carsById.get(carId);
        }
        
        LinkedList<Lane> newRoute = new LinkedList<Lane>();
        
        synchronized(lanesById)
        {
            for (int i : route)
                newRoute.add(lanesById.get(i));
        }
        
        car.setPlannedRoute(newRoute);
    }

	public Hashtable<Integer, LanesCross> getCrosses() {
		return crosses;
	}

	public void setCrosses(Hashtable<Integer, LanesCross> crosses) {
		this.crosses = crosses;
	}

	public void setLanes(LinkedList<Lane> lanes) {
		this.lanes = lanes;
	}

	public void setCars(LinkedList<Car> cars) {
		this.cars = cars;
	}

	public void setParkings(LinkedList<Parking> parkings) {
		this.parkings = parkings;
	}
        
        public Car getCarById(int id)
        {
            return carsById.get(id);
        }
        
        public Lane getLaneById (int id)
        {
            return lanesById.get(id);
        }
        
        public Parking getParkingById (int id)
        {
            return parkingById.get(id);
        }
 
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
