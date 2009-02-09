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

import java.util.LinkedList;

// }}}

/**
 * Entry/Exit point for cars
 *
 * @author Mariusz Ceier
 */
public class Parking //{{{
{
    // Variables {{{

    /**
     * Lane from Parking to LanesCross
     */
    private Lane p_lane_to_cross;

    /**
     * Lane from LanesCross to Parking
     */
    private Lane p_lane_to_parking;

    private LinkedList<Car>  p_carsOnParking;
    private LinkedList<Car>  p_carsLeavingParking;
    
    // }}}

    /**
     * @param lane_to_cross Lane from Parking to LanesCross
     */
    public Parking(Lane lane_to_cross, Lane lane_to_parking) //{{{
    {
        p_lane_to_cross = lane_to_cross;
        p_lane_to_parking = lane_to_parking;
        p_carsLeavingParking = new LinkedList<Car>();
        p_carsOnParking = new LinkedList<Car>();
    }//}}}

    /**
     * Create new car that drives from Parking
     */
    public Car newCar()//{{{
    {
        Car newCar = new Car();
        
        // set route for a car to get out from the parking
        LinkedList<Lane> plannedRoute = new LinkedList<Lane>();
        plannedRoute.add(p_lane_to_cross);
        newCar.setRoute(plannedRoute);
        
        newCar.park(this);
        return newCar;
    }//}}}
        
    public void park(Car car)//{{{
    {
        p_carsOnParking.add(car);
    }//}}}
    
    public void goToLeavingQueue(Car car)//{{{
    {
        if (p_carsLeavingParking.contains(car) == false)
        {
            p_carsLeavingParking.add(car);
        }
    }//}}}
    
    public boolean canLeaveParking(Car car)//{{{
    {
        if (p_carsLeavingParking.isEmpty())
        {
            p_carsLeavingParking.add(car);
            return true;
        }
        if (p_carsLeavingParking.get(0) == car)
            return true;
        else return false;
    }//}}}
    
    
    public boolean carIsLeaving(Car car)//{{{
    {
        if (canLeaveParking(car))
        {
            p_carsLeavingParking.remove(0);
            return true;
        }
        else
            return false;
    }//}}}
    public Lane laneOut() //{{{
    {
        return this.p_lane_to_cross;
    } //}}}

}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
