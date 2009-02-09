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

package trafficSim;

// imports {{{

import java.util.*;
import java.util.SortedMap;

//}}}

/**
 * Represents a single lane on fragment of road with no other lanes 
 *  crossing through it.
 * 
 * @author Adam Rutkowski
 */
public class Lane  //{{{
{  
    // Variables {{{
    private static final int     UNLIMITED_SPEED = Integer.MAX_VALUE;
    private static final int     DEFAULT_LENGTH = 100;
    private int                  p_speedLimit = Lane.UNLIMITED_SPEED;
    private int                  p_length = Lane.DEFAULT_LENGTH;
    private LanesCross           p_destination;
    
    /**
     * Virtual lanes are inserted to the streets structure just to 
     * represent the fact that a car can get from the starting lanes cross
     * to the ending one by switching from one lane to an adjecent one.
     * Purpose of holding information about virtual lanes is to make it possible
     * for graph algorithms to operate easily on streets structure.
     */
    private boolean              p_isVirtual = false;
    
    /**
     * list of adjecent lanes
     */
    private LinkedList<Lane>     p_adjecentLines = new LinkedList<Lane>();
    
    /**
     * List of cars on this lane. The key is each car's coordinate on the lane.
     */
    private SortedMap<Integer, Car>  p_carsOnLane = new TreeMap<Integer, Car>();
            
    /**
     * List of lights.
     */
    private Hashtable<Integer, Lights>  p_lights = new Hashtable<Integer, Lights>();
    
    //}}}
    
    public int getSpeedLimit() //{{{
    {
        return p_speedLimit;
    } //}}}
    
    public int getLength() //{{{
    {    
        return p_length;
    } //}}}
    
    public SortedMap<Integer, Car> getCars() //{{{
    {
        return p_carsOnLane;
    } //}}}
    
    public boolean isVirtual() //{{{
    {
        return p_isVirtual;
    } //}}}
    
    public Lane(LanesCross destination) //{{{
    {
    	p_speedLimit = Lane.UNLIMITED_SPEED;
	p_length = Lane.DEFAULT_LENGTH;
	p_destination = destination;
    } //}}}
    
    public Lane(int speedLimit, int length, LanesCross destination) //{{{
    {
        p_speedLimit = speedLimit;
        p_length = length;
        p_destination = destination;
    } //}}}
    
    public void addAdjecent(Lane lane) //{{{
    {
        p_adjecentLines.add(lane);
    } //}}}

    public Lights addLights(int distance, LightsState state) //{{{
    {
    	if (p_lights.containsKey(distance))
		return p_lights.get(distance);
    	Lights lights = new Lights(this,distance,state);
    	p_lights.put(distance,lights);
	return lights;
    } //}}}
    
    public boolean isEmpty() //{{{
    {
        return p_carsOnLane.isEmpty();
    } //}}}
    
    public boolean isCarOnLane(Car car) //{{{
    {
        return p_carsOnLane.containsValue(car);
    } //}}}

    public Car getFirstCar() //{{{
    {
        if (p_carsOnLane.isEmpty())
            return null;
        else
            return p_carsOnLane.get(0);
    } //}}}
    
    public boolean hasLights(int distance) //{{{
    {
    	return p_lights.containsKey(distance);
    } //}}}

    public Lights getLights(int distance) //{{{
    {
    	/*
    	if (!p_lights.containsKey(distance))
		return null;
	*/
	return p_lights.get(distance);
    } //}}}
    
    public boolean putCar(int coord, Car car) //{{{
    {
        if (coord > p_length)
            return false;
        p_carsOnLane.put(coord, car);
        return true;
    } //}}}

    void carIsLeaving(int coordinate)
    {
        if (p_carsOnLane.containsKey(coordinate))
            p_carsOnLane.remove(coordinate);
    }

    boolean moveCar(Integer coord1, Integer coord2) 
    {
        // if start and end position are the same, we're done.
        if (coord1 == coord2 && p_carsOnLane.containsKey(coord1))
            return true;
        
        // check if threre is a car on coord1 and if coord2 is free
        if (p_carsOnLane.containsKey(coord1) && !p_carsOnLane.containsKey(coord2))
        {
            p_carsOnLane.put(coord2, p_carsOnLane.get(coord1));
            p_carsOnLane.remove(coord1);
            return true;
        }
        else return false;
    }

} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
