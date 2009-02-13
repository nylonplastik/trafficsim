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
     * for graph algorithms to operate easily on streHets structure.
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
    
    public Lane()
    {
    	this.speedLimit = Lane.UNLIMITED_SPEED;
        this.length = Lane.DEFAULT_LENGTH;
        this.destination = null;
        this.source = null;
    }
    
    public Lane(LanesCross source,LanesCross destination) //{{{
    {
    	this.speedLimit = Lane.UNLIMITED_SPEED;
        this.length = Lane.DEFAULT_LENGTH;
        this.destination = destination;
        this.source = source;
    } //}}}
    
    public Lane(int speedLimit, int length, LanesCross source, LanesCross destination) //{{{
    {
        this.speedLimit = speedLimit;
        this.length = length;
        this.destination = destination;
        this.source = source;
    } //}}}
    
    public void addAdjecent(Lane lane) //{{{
    {
        adjacentLanes.add(lane);
    } //}}}

    public Lights addLights(int distance, LightsState state) //{{{
    {
        if (this.lights.containsKey(distance))
            return this.lights.get(distance);
        Lights lights = new Lights(this,distance,state);
        this.lights.put(distance,lights);
        return lights;
    } //}}}
    
    public boolean isEmpty() //{{{
    {
        return this.carsOnLane.isEmpty();
    } //}}}
    
    public boolean isCarOnLane(Car car) //{{{
    {
        return this.carsOnLane.containsValue(car);
    } //}}}

    public Car getFirstCar() //{{{
    {
        if (this.carsOnLane.isEmpty())
            return null;
        else
        {
            int firstCarCoordinate = this.carsOnLane.firstKey();
            return this.carsOnLane.get(firstCarCoordinate);
        }
    } //}}}
    
    public boolean hasLights(int distance) //{{{
    {
        return this.lights.containsKey(distance);
    } //}}}

    public Lights getLights(int distance) //{{{
    {
        /*
        if (!p_lights.containsKey(distance))
        return null;
         */
        return this.lights.get(distance);
    } //}}}
    
    public boolean putCar(int coord, Car car) //{{{
    {
        if (coord > this.length)
            return false;
        this.carsOnLane.put(coord, car);
        return true;
    } //}}}

    void carIsLeaving(int coordinate)
    {
        if (this.carsOnLane.containsKey(coordinate))
        	this.carsOnLane.remove(coordinate);
    }

    boolean moveCar(Integer coord1, Integer coord2) 
    {
        // if start and end position are the same, we're done.
        if (coord1 == coord2 && this.carsOnLane.containsKey(coord1))
            return true;

        if (coord1 > coord2)
            return false;

        // check if all coords from coord1+1 to coord2 are free
        for(Integer d : carsOnLane.keySet())
        {
            if ((d>coord1)&&(d<=coord2))
                return false;
            if (d>coord2)
                break;
        }
        // They are so put car on coord2
        carsOnLane.put(coord2, carsOnLane.get(coord1));
        carsOnLane.remove(coord1);
        return true;
        /*
        // check if there is a car on coord1 and if coord2 is free
        if (this.carsOnLane.containsKey(coord1) && !this.carsOnLane.containsKey(coord2))
        {   
            if ((coord1 < this.carsOnLane.firstKey())&&
                (this.carsOnLane.get(this.carsOnLane.firstKey()) != this.carsOnLane.get(coord2)))
            {
                return false;
            }
            this.carsOnLane.put(coord2, this.carsOnLane.get(coord1));
            this.carsOnLane.remove(coord1);
            return true;
        }
        else return false;
        */
    }

    /**
     * 
     * @return source of lane
     */
    public LanesCross getLaneSource()
    {
        return this.source;
    }
    
    /**
     * 
     * @return destination of lane
     */
    public LanesCross getLaneDestination()
    {
        return this.destination;
    }
    
    public boolean setDefaultNextLane(Lane next)
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
    
    public Lane getDefaultLane()
    {
        return this.defaultNextLane;
    }

	public LanesCross getDestination() {
		return destination;
	}

	public void setDestination(LanesCross destination) {
		this.destination = destination;
	}

	public LanesCross getSource() {
		return source;
	}

	public void setSource(LanesCross source) {
		this.source = source;
	}

	public LinkedList<Lane> getAdjacentLanes() {
		return adjacentLanes;
	}

	public void setAdjacentLanes(LinkedList<Lane> adjacentLanes) {
		this.adjacentLanes = adjacentLanes;
	}

	public SortedMap<Integer, Car> getCarsOnLane() {
		return carsOnLane;
	}

	public void setCarsOnLane(SortedMap<Integer, Car> carsOnLane) {
		this.carsOnLane = carsOnLane;
	}

	public Hashtable<Integer, Lights> getLights() {
		return lights;
	}

	public void setLights(Hashtable<Integer, Lights> lights) {
		this.lights = lights;
	}

	public Lane getDefaultNextLane() {
		return defaultNextLane;
	}

	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}
    
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
