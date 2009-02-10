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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;

//}}}

/**
 *
 * @author Adam Rutkowski
 */
public class Car //{{{
{
    // Variables {{{
    private int          p_id;
    private static int   carsCount = 0;
    private int          p_nextCarDistance;
    private Position     p_position;
    private float        p_speed;
    private float        p_acceleration; 
    private static float p_maxSpeed = 12;
    private static float p_maxAcceleration = 5;
    
    private Parking      p_currentParking;
    
    /**
     * The route that this car is about to move on.
     */
    private LinkedList<Lane> p_plannedRoute;
        
     //}}}
    
    private synchronized int getNewId() //{{{
    {
        return carsCount++;
    } //}}}
    
    /**
     * list of lanes that this car is observing
     */
    private LinkedList<Lane>   p_observedLanes;
    

    /**
     * Default constructor - Car doesn't know anything
     */
    public Car() //{{{
    {
        p_id = getNewId();
        p_currentParking = null;
        p_position = new Position();
    } //}}}
    
    public boolean isParked() //{{{
    {
        return (p_currentParking != null);
    } //}}}
    
    public void park(Parking parking) //{{{
    {
        p_currentParking = parking;
        p_position.lane = null;
        p_position.info = Position.e_info.NOT_DRIVING;
        parking.park(this);
    } //}}}
    
    public void goToParkingOutQueue()
    {
        if (isParked() == false)
            return ;
        p_currentParking.goToLeavingQueue(this);
        p_plannedRoute = new LinkedList<Lane>();
        p_plannedRoute.add(p_currentParking.laneOut());
    }
    
    public boolean leaveParking() //{{{
    {
       if (p_currentParking == null)
           return false;
       p_currentParking.goToLeavingQueue(this);
       if (p_currentParking.canLeaveParking(this))
       {
           // TODO: check if lane out of parking is empty
           // and set it as the only element of planned route
           p_currentParking.carIsLeaving(this);
           p_currentParking = null;
           p_position.lane = p_plannedRoute.get(0);
           p_plannedRoute.remove(0);
           p_position.coord = 0;
           return true;
       }
       else
           return false;
    } //}}}

    
    /***
     * Calculates new position and speed after specified time period.
     * @param timeUnits
     * @throws java.lang.Exception
     */
    public void move(int timeUnits) //{{{
    {
        if (p_position.info == Position.e_info.NOT_DRIVING)
            return;
        
        // check what will be the car position after one time period
        Position newPosition = positionAfterTime(timeUnits, 1);
        
        // if position has been calculated successfully - set the new position
        if (newPosition.info == Position.e_info.OK)
        {
            if (p_position.lane != newPosition.lane)  // car approached new lane?
            {
                // try to put the car on new lane
                p_position.lane.carIsLeaving(this.p_position.coord);
                if (!newPosition.lane.putCar(newPosition.coord, this))
                    return;
            }
            else
                // try to move car on current lane
                if (!p_position.lane.moveCar(p_position.coord, newPosition.coord))
                    return;
            
            p_position.lane  = newPosition.lane;
            p_position.coord = newPosition.coord;
            p_speed += p_acceleration * timeUnits;
        }
    } //}}}
    
    /**
     * modify the observed lanes list according to current position.
     */
    public void updateObserverList() //{{{
    {
        // here the car object should subscribe and unsubsrcribe for information from
        // selected Lanes.
    } //}}}
    
    public int getId() //{{{
    {
        return p_id;
    } //}}}
    
    public void setRoute(LinkedList<Lane> route) //{{{
    {
        p_plannedRoute = route;
    } //}}}
    
    public LinkedList<Lane> getRoute() //{{{
    {
        return p_plannedRoute;
    } //}}}
    
    public boolean canLeaveParking() //{{{
    {
        if (p_currentParking != null)
            return p_currentParking.canLeaveParking(this);
        else return false;
    } //}}}  
    
    public Position positionAfterTime(int timePeriod, int periodsCount) //{{{
    {
        Position result = new Position();
        result.lane = p_position.lane;
        result.coord = p_position.coord;
        
        if (p_position.lane == null)
        {
            result.info = Position.e_info.NOT_DRIVING;
            return result;
        }
        
        int newCoordinate;
        float speed = p_speed,  acceleration = p_acceleration;
        
        for (int i=0; i<periodsCount; i++)
        {
            newCoordinate = result.coord + (int)(timePeriod * speed);
            speed +=  (timePeriod * p_acceleration);   
            if (speed > p_maxSpeed)
            {
                speed = p_maxSpeed;
                acceleration = 0;
            }
            else if (speed < 0)
            {
                speed = 0;
                acceleration =0;
            }
            
            result.info = Position.e_info.OK;
            if (newCoordinate <= result.lane.getLength())
            {
                result.coord = newCoordinate;
            }
            else
            {
                /* calculate on which lane we are at the moment */
                int sum = result.lane.getLength();
                while(sum <= newCoordinate)
                {
                    // get the next lane in g
                    if (p_plannedRoute.isEmpty())
                    {
                        result.info = Position.e_info.OUT_OF_RANGE;
                        return result;
                    }
                    else if (p_plannedRoute.indexOf(result.lane) == p_plannedRoute.size())
                    {
                        result.info = Position.e_info.OUT_OF_RANGE;
                        return result;                        
                    }
                    else
                        result.lane = p_plannedRoute.get(p_plannedRoute.indexOf(result.lane));
                    
                    sum += result.lane.getLength();
                }
                result.coord = newCoordinate - (sum - result.lane.getLength());
            } 
        }
        return result;
    }//}}}
    
    public Position getPosition() //{{{
    {
        return p_position;
    } //}}}
    
    /**
     * Returns car preceding this one on its planned route.
     * @return
     */
    public Car getNextCar() //{{{
    {
        if (p_position.lane != null)    // car is on some lane
        {
            SortedMap<Integer, Car> carsOnLane = p_position.lane.getCars();

            // is the lane empty?
            if (carsOnLane.isEmpty() == false)
                // is the car on this lane? (if no, something is wrong)
                if (carsOnLane.containsValue(this))
                    // is 'car' not the last car on the lane?
                    if (carsOnLane.lastKey() != p_position.coord)
                    {
                        // get the part of car list startin with this car
                        Iterator<Car> iter = carsOnLane.tailMap(p_position.coord).values().iterator();
                        // get this car element of the list
                        iter.next();
                        // get next element of the list
                        Car next = iter.next();
                        // caluculate distance to that car
                        p_nextCarDistance = next.getPosition().coord - p_position.coord;
                        // return the next car
                        return next;
                    }
            // preceding car not found on current lane. Calculate distance from
            // this car to the end of the lane for use in further calculations.
            p_nextCarDistance = p_position.lane.getLength() - p_position.coord;
        }
        else
            p_nextCarDistance = 0; //since the car is not riding, the distance 
                    // to next car will be calculated basing on the planned route
                    // <B>only</B>, so for now the distance is 0.
                                
        
        if (p_plannedRoute.isEmpty() == false)  // are there some lanes planned to move on?
        {
            // try to find the closest car on planned route
            int i = 0;
            Lane laneOnRoute = null;
            while(i < p_plannedRoute.size())
            {
                laneOnRoute  = p_plannedRoute.get(i);
                if (laneOnRoute.isEmpty() == false)
                    break;
                // No car on this lane, distance to next car is increased by
                // this lane length.
                p_nextCarDistance += laneOnRoute.getLength();
                i++;
            }
            if (i==p_plannedRoute.size())     // whole planned car route is empty
                return null;
            // distance to the next car is increased by that car position on its lane
            p_nextCarDistance = laneOnRoute.getFirstCar().getPosition().coord;
            return laneOnRoute.getFirstCar();  
        }
        else
            return null;
    } //}}}
    
    /**
     * Works properly only after successful call to  getNextCar().
     * @return distance to the next car on this car planned route.
     */
    public int getNextCarDistance() //{{{
    {
        return p_nextCarDistance;
    } //}}}
    
    /**
     * Sets the car on the beginning of its planned route
     */
    public void startMoving(float acceleration) //{{{
    {
        // check if we can move on
        if ( p_position.lane != null || 
             p_plannedRoute.isEmpty() || 
             !p_currentParking.canLeaveParking(this))
        {
            return;
        }
        
        // try to put car on the lane
        if (!p_plannedRoute.get(0).putCar(0, this))
            return;
        
        // set up new position
        p_position.lane  = p_plannedRoute.get(0);
        p_position.coord = 0;
        p_position.info = Position.e_info.OK;
        p_plannedRoute.remove(0);


        // leave the parking
        p_currentParking.carIsLeaving(this);
        p_currentParking = null;
        
        // set new state
        if (acceleration > p_maxAcceleration)
            acceleration = p_maxAcceleration;
        p_speed         = 0;
        p_acceleration  = acceleration;
    } //}}}
    
    public float getAcceleration() //{{{
    {
        return p_acceleration;
    } //}}}
    
    public void changeAcceleration(float newAcceleration) //{{{
    {
        if (newAcceleration > p_maxAcceleration || -1.0*newAcceleration > p_maxAcceleration)
        {
            if (newAcceleration < 0)
                p_acceleration = (float)-1.0*p_maxAcceleration;
            else
                p_acceleration = p_maxAcceleration;
        }
        else
            p_acceleration = newAcceleration;
    } //}}}
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
