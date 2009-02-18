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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
//}}}


/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class Car implements Serializable //{{{
{
    // Variables {{{
    private final int    id;
    private static int   carsCount = 0;
    private float        nextCarDistance;
    private Position     position;
    private float        speed;
    private float        acceleration; 
    private float        maxSpeed = 40;
    private final float  maxAcceleration;
    private boolean      collided = false;    // if there was a collision
    
    private Parking      currentParking;
    
    /**
     * The route that this car is about to move on.
     */
    private LinkedList<Lane> plannedRoute;
    
     //}}}
    
    private synchronized int getNewId() //{{{
    {
        return Car.carsCount++;
    } //}}}
    
    /**
     * Default constructor - Car doesn't know anything
     */
    public Car() //{{{
    {
        this.id              = getNewId();
        this.currentParking  = null;
        this.maxAcceleration = 5;
        this.position        = new Position();
    } //}}}
    
    public Car(int id) //{{{
    {
        this.id              = id;
        this.currentParking  = null;
        this.maxAcceleration = 5;        
        this.position        = new Position();
    } //}}}
    
    public Car(CarData data, Model model)
    {//{{{
        this.id              = data.id;
        this.speed           = data.speed;
        this.acceleration    = data.acceleration;
        this.collided        = data.collided;
        this.currentParking  = model.getParkingById(data.parkingId);
        this.maxAcceleration = data.maxAcceleration;
        this.maxSpeed        = data.maxSpeed;
        this.plannedRoute    = new LinkedList<Lane>();
        
        for(int i : data.plannedRoute)
        {
            plannedRoute.add(model.getLaneById(i));
        }
        
        if (data.parkingId == -1)
            this.currentParking = null;
        else
            this.currentParking = model.getParkingById(data.parkingId);
        
        this.position        = new Position();
        this.position.setCoord(data.positionCoord);
        this.position.setLane(model.getLaneById(data.positionLane));
        this.position.setInfo(data.positionInfo);
    }//}}}
    
    public synchronized boolean isParked() //{{{
    {
        return (this.currentParking != null);
    } //}}}
    
    public synchronized void park(Parking parking) //{{{
    {
        this.currentParking = parking;
        this.position.setLane(null);
        this.position.info = Position.e_info.NOT_DRIVING;
        parking.park(this);
    } //}}}
    
    public synchronized  void goToParkingOutQueue() //{{{
    {
        if (isParked() == false)
            return ;
        this.currentParking.goToLeavingQueue(this);
        this.plannedRoute = new LinkedList<Lane>();
        this.plannedRoute.add(this.currentParking.laneOut());
    }//}}}
    
    public boolean leaveParking() //{{{
    {
        if (this.currentParking == null)
            return false;   
        
       this.currentParking.goToLeavingQueue(this);
       if (currentParking.canLeaveParking(this))
       {
           // TODO: check if lane out of parking is empty
           // and set it as the only element of planned route
           this.currentParking.carIsLeaving(this);
           this.currentParking = null;
           this.position.setLane(this.plannedRoute.get(0));
           this.plannedRoute.remove(0);
           this.position.setCoord(0);
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
    public synchronized void move(int timeMiliseconds) //{{{
    {
        if (this.position.info == Position.e_info.NOT_DRIVING)
            return;
        
        // check what will be the car position after one time period
        Position newPosition = positionAfterTime(timeMiliseconds, 1);
        
        // if position has been calculated successfully - set the new position
        if (newPosition.info == Position.e_info.OK)
        {   
            // car approached new lane?
            if (this.position.getLane() != newPosition.getLane())  
            {
                // try to put the car on new lane
                this.position.getLane().carIsLeaving(this.position.getCoord());
                if (!newPosition.getLane().putCar(newPosition.getCoord(), this))
                    return;
            }
            else
                // try to move car on current lane
                if (!this.position.getLane().moveCar(
                                this.position.getCoord(), 
                                newPosition.getCoord()
                                ))
                    return;
            
            this.position.setLane(newPosition.getLane());
            this.position.setCoord(newPosition.getCoord());
            this.speed += this.acceleration / 1000 * timeMiliseconds;
        }
    } //}}}
    
    
    // Not synchronized - id is a final field
    public int getId() //{{{
    {
        return this.id;
    } //}}}
    
    public synchronized void setRoute(LinkedList<Lane> route) //{{{
    {
        this.plannedRoute = route;
    } //}}}
    
    public synchronized LinkedList<Lane> getRoute() //{{{
    {
        return this.plannedRoute;
    } //}}}
    
    public synchronized boolean canLeaveParking(int safeDistance) //{{{
    {
        if (this.currentParking != null)
            if (this.currentParking.canLeaveParking(this))
            {
                if (this.plannedRoute.isEmpty())
                    return false;
                
                Lane laneOut = this.currentParking.laneOut();
                if (this.plannedRoute.get(0) != laneOut)
                    return false;
                
                getNextCar();
                float nextDistance = getNextCarDistance();
                if (nextDistance > laneOut.getLength())
                    return true;
                
                if (nextDistance >= safeDistance)
                    return true;
                else
                    return false;
            }
        return false;
    } //}}}  
    
    public synchronized Position positionAfterTime(
            int timeMiliseconds, 
            int periodsCount
             
             ) 
    {//{{{
        Position result = new Position();
        result.setLane(this.position.getLane());
        result.setCoord(this.position.getCoord());
        
        float timeSeconds = (float) timeMiliseconds / 1000;
        
        if (this.position.getLane() == null)
        {
            result.info = Position.e_info.NOT_DRIVING;
            return result;
        }
        
        float newCoordinate;
        float current_speed        = this.speed;
        float current_acceleration = this.acceleration;
        
        for (int i=0; i<periodsCount; i++)
        {
            newCoordinate =result.getCoord() + timeSeconds*current_speed;
            current_speed +=  (timeSeconds * current_acceleration);   
            if (current_speed > this.maxSpeed)
            {
                current_speed = this.maxSpeed;
                current_acceleration = 0;
            }
            else if (current_speed < 0)
            {
                current_speed = 0;
                current_acceleration = 0;
            }
            
            result.info = Position.e_info.OK;
            if (newCoordinate <= result.getLane().getLength())
            {
                result.setCoord(newCoordinate);
            }
            else
            {
                /* calculate on which lane we are at the moment */
                int sum = result.getLane().getLength();
                while(sum <= newCoordinate)
                {
                    // get the next lane in g
                    boolean routeIsEmpty = this.plannedRoute.isEmpty();
                    boolean endOfRoute = 
                            (this.plannedRoute.indexOf(result.getLane())
                                                   == this.plannedRoute.size()-1
                            );
                    
                    if (routeIsEmpty || endOfRoute)
                    {   
                        Lane laneAterRoute = result.getLane().getDefaultLane();    
                        if (laneAterRoute == null)
                        {
                            result.setCoord(result.getLane().getLength());
                            this.collided = true;  // Collision - end of
                                                        //  route
                            return result;
                        }
                        else
                        {
                            result.info = Position.e_info.OUT_OF_RANGE;
                            result.setLane(laneAterRoute);
                        }
                    }
                    else
                    {
                        if(plannedRoute.contains(result.getLane()))
                        {
                            result.setLane(this.plannedRoute.get(
                                    this.plannedRoute.indexOf(result.getLane())
                                    ));
                        }
                        else
                            result.setLane(plannedRoute.get(0));
                    }
                        
                    sum += result.getLane().getLength();
                }
                result.setCoord(newCoordinate - (sum - result.getLane().getLength()));
            } 
        }
        return result;
    }//}}}
    
    public synchronized Position getPosition() //{{{
    {
        return this.position;
    } //}}}
    
    /**
     * Returns car preceding this one on its planned route.
     * @return
     */
    public synchronized Car getNextCar() //{{{
    {
        if (this.position.getLane() != null)    // car is on some lane
        {
            SortedMap<Integer, Car> carsOnLane = this.position.getLane().getCars();

            // is the lane empty?
            if (carsOnLane.isEmpty() == false)
                // is the car on this lane? (if no, something is wrong)
                if (carsOnLane.containsValue(this))
                    // is 'car' not the last car on the lane?
                    if (carsOnLane.lastKey() != this.position.getCoord())
                    {
                        // get the part of car list starting with this car
                        Iterator<Car> iter = carsOnLane.tailMap(
                                (int) this.position.getCoord()
                                ).values().iterator();
                        
                        // get this car element of the list
                        iter.next();
                        if (iter.hasNext())
                        {
                            // get next element of the list
                            Car next = iter.next();
                            // calculate distance to that car
                            this.nextCarDistance = 
                                        next.getPosition().getCoord() - 
                                            position.getCoord();
                            // return the next car
                            return next;
                        }
                    }
            // preceding car not found on current lane. Calculate distance from
            // this car to the end of the lane for use in further calculations.
            this.nextCarDistance = this.position.getLane().getLength() - 
                                                    this.position.getCoord();
        }
        else
            this.nextCarDistance = 0; //since the car is not riding, the 
                    // distance  to next car will be calculated basing on the 
                    // planned route <B>only</B>, so for now the distance is 0.
                                
        
        if (this.plannedRoute.isEmpty() == false)  // are there some lanes 
        {                                          // planned to move on?
            // try to find the closest car on planned route
            int i = 0;
            Lane laneOnRoute = null;
            while(i < this.plannedRoute.size())
            {
                laneOnRoute  = this.plannedRoute.get(i);
                if (laneOnRoute.isEmpty() == false)
                    break;
                // No car on this lane, distance to next car is increased by
                // this lane length.
                this.nextCarDistance += laneOnRoute.getLength();
                i++;
            }
            if (i==this.plannedRoute.size()) // whole planned car route is empty
                return null;
            
            // distance to the next car is increased by that car position 
            // on its lane
            this.nextCarDistance = 
                           laneOnRoute.getFirstCar().getPosition().getCoord();
            return laneOnRoute.getFirstCar();  
        }
        else
            return null;
    } //}}}
    
    /**
     * Works properly only after successful call to  getNextCar().
     * @return distance to the next car on this car planned route.
     */
     public synchronized float getNextCarDistance() //{{{
    {
        return this.nextCarDistance;
    } //}}}
    
    /**
     * Sets the car on the beginning of its planned route
     */
    public synchronized void startMoving(float accel) //{{{
    {
        // check if we can move on
        if ( this.position.getLane() != null || 
             this.plannedRoute.isEmpty() || 
             !this.currentParking.canLeaveParking(this))
        {
            return;
        }
        
        // try to put car on the lane
        if (!this.plannedRoute.get(0).putCar(0, this))
            return;
        
        // set up new position
        this.position.setLane(this.plannedRoute.get(0));
        this.position.setCoord(0);
        this.position.info = Position.e_info.OK;
        this.plannedRoute.remove(0);


        // leave the parking
        this.currentParking.carIsLeaving(this);
        this.currentParking = null;
        
        // set new state
        if (accel > maxAcceleration)
            accel = maxAcceleration;
        this.speed         = 0;
        this.acceleration  = accel;
    } //}}}
    
    public synchronized float getAcceleration() //{{{
    {
        return this.acceleration;
    } //}}}
    
    public float getMaxAcceleration()
    {
        return this.maxAcceleration;
    }
    
    public float getSpeed() //{{{
    {
        return this.speed;
    } //}}}        
    
    public void changeAcceleration(float newAcceleration) //{{{
    {

    } //}}}
    
    public synchronized static int getCarsCount() {
            return carsCount;
    }

    public synchronized float getMaxSpeed() {
            return maxSpeed;
    }

    public synchronized Parking getCurrentParking() {
            return currentParking;
    }

    public synchronized LinkedList<Lane> getPlannedRoute() {
            return plannedRoute;
    }

    public synchronized void setPlannedRoute(LinkedList<Lane> plannedRoute) 
    {
            this.plannedRoute = plannedRoute;
    }

    public synchronized void setAcceleration(float newAcceleration) 
    {
        if (newAcceleration > maxAcceleration || 
                        -1.0*newAcceleration > maxAcceleration)
        {
            if (newAcceleration < 0)
                this.acceleration = (float)-1.0*maxAcceleration;
            else
                this.acceleration = maxAcceleration;
        }
        else
                this.acceleration = newAcceleration;
    }


    public synchronized boolean isCollided() {
            return collided;
    }

    public synchronized void setCollided(boolean collided) {
            this.collided = collided;
    }
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
