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

    /**
     * LinkedList of all lane crosses.
     */
    private Hashtable<Integer, LanesCross>      p_crosses;
    
    /***
     * List of all lanes.
     */
    private LinkedList<Lane>                          p_lanes;
    
    /***
     * List of all cars on the map
     */
    private LinkedList<Car>                           p_cars;
    
    /**
     * List of all parkings.
     */
    private LinkedList<Parking>                       p_parkings;
    
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
        p_crosses  = new Hashtable<Integer, LanesCross>();
        p_lanes    = new LinkedList<Lane>();
        p_cars     = new LinkedList<Car>();
        p_parkings = new LinkedList<Parking>();
    }//}}}
    
    public void addCross(int id, int X, int Y)//{{{
    {
        p_crosses.put(id, new LanesCross(id, X, Y) );
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Crosses);
    }//}}}
    
    public int addLane(int start, int end, int maxSpeed, int length)//{{{
    {
        if (!p_crosses.containsKey(start) || !p_crosses.containsKey(end))
            return -1;
        
        LanesCross cStart = p_crosses.get(start);
        LanesCross cEnd = p_crosses.get(end);
        
        Lane newLane = new Lane(maxSpeed, length, cStart, cEnd);
        
        cStart.addConnection(end, newLane );
        cEnd.addIncoming(newLane);
        
        p_lanes.add(newLane);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Lanes);
        
        return p_lanes.indexOf(newLane);
    }//}}}
    
    public Parking addParking(Lane lane_to_cross, Lane lane_to_parking)//{{{
    {
        Parking parking = new Parking(lane_to_cross, lane_to_parking);
        p_parkings.add(parking);
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
        return p_cars;
    }//}}}
    
    public LinkedList<Parking> getParkings()//{{{
    {
        return p_parkings;
    }//}}}
    
    public Hashtable<Integer, LanesCross> getLanesCrosses()//{{{
    {
        return p_crosses;
    }//}}}
    
    public LinkedList<Lane> getLanes()//{{{
    {
        return p_lanes;
    }//}}}
    
    public void updateCarsState(LinkedList<Car> cars)//{{{
    {
        
    }//}}}
     
    public Car newCar(Parking where)//{{{
    {
        Car newCar = where.newCar();
        p_cars.add(newCar);
        this.setChanged();
        //this.notifyObservers(WhatHasChanged.Cars);
        return newCar;
    }//}}}

    void finishedTimeUpdate()//{{{
    {
        this.setChanged();
        this.notifyObservers(WhatHasChanged.TimeUpdate);
    }//}}}

}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
