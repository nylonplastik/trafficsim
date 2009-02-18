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

import java.io.Serializable;
import java.util.*;

//}}}

/**
 * Represents a single lane on fragment of road with no other lanes 
 *  crossing through it.
 * 
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class Lane implements Serializable //{{{
{  
    // Variables {{{
    private int                  id;
    private static int           lanesCount = 0;
    private static final int     UNLIMITED_SPEED = Integer.MAX_VALUE;
    private static final int     DEFAULT_LENGTH = 100;
    private int                  speedLimit = Lane.UNLIMITED_SPEED;
    private int                  length = Lane.DEFAULT_LENGTH;
    private LanesCross           destination = null;
    private LanesCross           source = null;
    private Lane                 defaultNextLane = null;
    
    /**
     * Virtual lanes are inserted to the streets structure just to 
     * represent the fact that a car can get from the starting lanes cross
     * to the ending one by switching from one lane to an adjecent one.
     * Purpose of holding information about virtual lanes is to make it possible
     * for graph algorithms to operate easily on streets structure.
     */
    private boolean              isVirtual = false;
    
    /**
     * list of adjacent lanes
     */
    private LinkedList<Lane>     adjacentLanes = new LinkedList<Lane>();
    
    /**
     * List of cars on this lane. The key is each car's coordinate on the lane.
     */
    private SortedMap<Integer, Car>  carsOnLane = new TreeMap<Integer, Car>();
            
    /**
     * List of lights.
     */
    private Hashtable<Integer, Lights>  lights = new Hashtable<Integer, Lights>();
    
    //}}}
    
    public int getSpeedLimit() //{{{
    {
        return this.speedLimit;
    } //}}}
    
    public int getLength() //{{{
    {    
        return this.length;
    } //}}}
    
    public SortedMap<Integer, Car> getCars() //{{{
    {
        return this.carsOnLane;
    } //}}}
    
    public boolean isVirtual() //{{{
    {
        return this.isVirtual;
    } //}}}
    
    private synchronized int getNewId()
    {
        return lanesCount++;
    }
    
    public Lane()
    {
        this.speedLimit = Lane.UNLIMITED_SPEED;
        this.length = Lane.DEFAULT_LENGTH;
        this.destination = null;
        this.source = null;
        this.id = getNewId();
    }
    
    public Lane(LanesCross source,LanesCross destination) //{{{
    {
        this.speedLimit = Lane.UNLIMITED_SPEED;
        this.length = distance(source, destination);
        this.destination = destination;
        this.source = source;
        this.id = getNewId();
    } //}}}
    
    public Lane(int speedLimit, LanesCross source, LanesCross destination) //{{{
    {
        this.speedLimit = speedLimit;
        this.length = distance(source, destination);
        this.destination = destination;
        this.source = source;
        this.id = getNewId();
    } //}}}
    
    public synchronized void addAdjecent(Lane lane) //{{{
    {
        adjacentLanes.add(lane);
    } //}}}

    public synchronized Lights addLights(int distance, LightsState state) //{{{
    {
        if (this.lights.containsKey(distance))
            return this.lights.get(distance);
        Lights lights = new Lights(this,distance,state);
        this.lights.put(distance,lights);
        return lights;
    } //}}}
    
    public synchronized boolean isEmpty() //{{{
    {
        return this.carsOnLane.isEmpty();
    } //}}}
    
    public synchronized boolean isCarOnLane(Car car) //{{{
    {
        return this.carsOnLane.containsValue(car);
    } //}}}

    public synchronized Car getFirstCar() //{{{
    {
        if (this.carsOnLane.isEmpty())
            return null;
        else
        {
            int firstCarCoordinate = this.carsOnLane.firstKey();
            return this.carsOnLane.get(firstCarCoordinate);
        }
    } //}}}
    
    public synchronized boolean hasLights(int distance) //{{{
    {
        return this.lights.containsKey(distance);
    } //}}}

    public synchronized Lights getLights(int distance) //{{{
    {
        /*
        if (!p_lights.containsKey(distance))
        return null;
         */
        return this.lights.get(distance);
    } //}}}
    
    public synchronized boolean putCar(float coord, Car car) //{{{
    {
        int coordinate = (int) coord;
        if (coordinate > this.length)
            return false;
        this.carsOnLane.put(coordinate, car);
        return true;
    } //}}}

    public synchronized void carIsLeaving(float coord)
    {
        int coordinate = (int)coord;
        if (this.carsOnLane.containsKey(coordinate))
            this.carsOnLane.remove(coordinate);
    }

    public synchronized boolean moveCar(float coord1, float coord2) 
    {
        int coordinate1 = (int) coord1;
        int coordinate2 = (int) coord2;
        // if start and end position are the same, we're done.
        if (coordinate1 == coordinate2 && this.carsOnLane.containsKey(coordinate1))
            return true;

        if (coordinate1 > coordinate2)
            return false;

        // check if all coords from coord1+1 to coord2 are free
        for(Integer d : carsOnLane.keySet())
        {
            if ((d>coordinate1)&&(d<=coordinate2))
                return false;
            if (d>coordinate2)
                break;
        }
        // They are so put car on coord2
        carsOnLane.put(coordinate2, carsOnLane.get(coordinate1));
        carsOnLane.remove(coordinate1);
        return true;
    }

    /**
     * 
     * @return source of lane
     */
    public synchronized LanesCross getLaneSource()
    {
        return this.source;
    }
    
    /**
     * 
     * @return destination of lane
     */
    public synchronized LanesCross getLaneDestination()
    {
        return this.destination;
    }
    
    public synchronized boolean setDefaultNextLane(Lane next)
    {
        if (next == null)
        {
            return false;
        }
        if (!this.destination.getOutgoingLanes().contains(next))   
        {
            return false;
        }
        else
        {
            this.defaultNextLane = next;
            return true;
        }
    }
    
    public synchronized Lane getDefaultLane()
    {
        return this.defaultNextLane;
    }

    public synchronized LanesCross getDestination() {
            return destination;
    }

    public synchronized void setDestination(LanesCross destination) {
            this.destination = destination;
    }

    public synchronized LanesCross getSource() {
            return source;
    }

    public synchronized void setSource(LanesCross source) {
            this.source = source;
    }

    public synchronized LinkedList<Lane> getAdjacentLanes() {
            return adjacentLanes;
    }

    public synchronized void setAdjacentLanes(LinkedList<Lane> adjacentLanes) {
            this.adjacentLanes = adjacentLanes;
    }

    public synchronized SortedMap<Integer, Car> getCarsOnLane() {
            return carsOnLane;
    }

    public synchronized void setCarsOnLane(SortedMap<Integer, Car> 
                                           carsOnLane
                                           ) 
    {
            this.carsOnLane = carsOnLane;
    }

    public synchronized Hashtable<Integer, Lights> getLights() {
            return lights;
    }

    public synchronized void setLights(Hashtable<Integer, Lights> lights) {
            this.lights = lights;
    }

    public synchronized Lane getDefaultNextLane() {
            return defaultNextLane;
    }

    public synchronized void setSpeedLimit(int speedLimit) {
            this.speedLimit = speedLimit;
    }

    public synchronized void setVirtual(boolean isVirtual) {
            this.isVirtual = isVirtual;
    }

    public int getId() {
        return id;
    }
    
        
    private int distance(LanesCross c1, LanesCross c2)
    {
        int x = c1.getX() - c2.getX();
        int y = c1.getY() - c2.getY();
        return (int) Math.sqrt(x*x+y*y);
    }
    
   /* TODO TO BE REMOVED WHEN COMMUNICATION IS ON*/
    public Lane(int speedLimit, LanesCross source, LanesCross destination, int id) //{{{
    {
        this.speedLimit = speedLimit;
        this.length = distance(source, destination);
        this.destination = destination;
        this.source = source;
        this.id = id;
    } //}}}
    
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
