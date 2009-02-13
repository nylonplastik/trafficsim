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
import java.util.LinkedList;

// }}}

/**
 * Entry/Exit point for cars
 *
 * @author Mariusz Ceier
 */
@SuppressWarnings("serial")
public class Parking implements Serializable //{{{
{
    // Variables {{{

    /**
     * Lane from Parking to LanesCross
     */
    private Lane laneToCross;

    /**
     * Lane from LanesCross to Parking
     */
    private Lane laneToParking;

    private LinkedList<Car>  carsOnParking;
    private LinkedList<Car>  carsLeavingParking;
    
    // }}}

    public Parking()
    {
    	this.laneToCross = null;
    	this.laneToParking = null;
    	this.carsLeavingParking = new LinkedList<Car>();
    	this.carsOnParking = new LinkedList<Car>();
    }

    /**
     * @param lane_to_cross Lane from Parking to LanesCross
     */
    public Parking(Lane lane_to_cross, Lane lane_to_parking) //{{{
    {
    	this.laneToCross = lane_to_cross;
    	this.laneToParking = lane_to_parking;
    	this.carsLeavingParking = new LinkedList<Car>();
        this.carsOnParking = new LinkedList<Car>();
    }//}}}

    /**
     * Create new car that drives from Parking
     */
    public Car newCar()//{{{
    {
        Car newCar = new Car();
        
        // set route for a car to get out from the parking
        LinkedList<Lane> plannedRoute = new LinkedList<Lane>();
        plannedRoute.add(this.laneToCross);
        newCar.setRoute(plannedRoute);
        
        newCar.park(this);
        return newCar;
    }//}}}
        
    public void park(Car car)//{{{
    {
    	this.carsOnParking.add(car);
    }//}}}
    
    public void goToLeavingQueue(Car car)//{{{
    {
        if (this.carsLeavingParking.contains(car) == false)
        {
        	this.carsLeavingParking.add(car);
        }
    }//}}}
    
    public boolean canLeaveParking(Car car)//{{{
    {
        if (this.carsLeavingParking.isEmpty())
        {
        	this.carsLeavingParking.add(car);
            return true;
        }
        if (this.carsLeavingParking.get(0) == car)
            return true;
        else return false;
    }//}}}
    
    
    public boolean carIsLeaving(Car car)//{{{
    {
        if (canLeaveParking(car))
        {
        	this.carsLeavingParking.remove(0);
            return true;
        }
        else
            return false;
    }//}}}
    public Lane laneOut() //{{{
    {
        return this.laneToCross;
    } //}}}

	public Lane getLaneToCross() {
		return laneToCross;
	}

	public void setLaneToCross(Lane laneToCross) {
		this.laneToCross = laneToCross;
	}

	public Lane getLaneToParking() {
		return laneToParking;
	}

	public void setLaneToParking(Lane laneToParking) {
		this.laneToParking = laneToParking;
	}

	public LinkedList<Car> getCarsOnParking() {
		return carsOnParking;
	}

	public void setCarsOnParking(LinkedList<Car> carsOnParking) {
		this.carsOnParking = carsOnParking;
	}

	public LinkedList<Car> getCarsLeavingParking() {
		return carsLeavingParking;
	}

	public void setCarsLeavingParking(LinkedList<Car> carsLeavingParking) {
		this.carsLeavingParking = carsLeavingParking;
	}

}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
