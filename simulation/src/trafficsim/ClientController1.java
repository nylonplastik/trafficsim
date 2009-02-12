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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Singleton class responsible for somulation control.
 * @author Adam Rutkowski
 */
public class ClientController1 implements IController
{       
    private Model                      p_model;
    private ClientViewClientSide       p_view;
    private LinkedList<Car>            p_controlledCars;
    // safe distance between the cars
    private static final int           SAFE_DISTANCE = 10;  
    
    
    public ClientController1(Model model, ClientViewClientSide view)
    {
        p_model          = model;
        p_view           = view;
        p_controlledCars = new LinkedList<Car>();
        Car newCar;
        LinkedList<Parking> parkings;
        
        if (!(parkings = model.getParkings()).isEmpty())
        {
            // start one new cars on first available parking
            for (int i = 0; i <1; i++) 
            {
                // new car parked on first parking
                newCar = model.newCar(parkings.get(0));
                // car is trying to leave the parking
                newCar.goToParkingOutQueue();
                
                p_controlledCars.add(newCar);
                p_view.addObservedCar(newCar);  
            }
        }
    }
    
    /***
     * Client's controller is notified about it's view change
     */
    public void viewChanged()
    {
        LinkedList<Car> carsInView = p_view.data.cars;
        Car car;
        
        // for each controlled car in client's view
        for (Iterator<Car> it = carsInView.iterator(); it.hasNext(); )
        {
            car = it.next();
               
            if (car.isParked())
            {
                 // if car is parked, try to leave the parking
                if (car.canLeaveParking())
                {
                    Lane laneOut = null;
                    int  distanceToNextCar;
                    Car nextCar = car.getNextCar();
                    LinkedList<Lane> carsPlannedRoute = car.getRoute();
                    
                    if (nextCar == null)
                        distanceToNextCar = 2*SAFE_DISTANCE;
                    else
                        distanceToNextCar = car.getNextCarDistance();
                    
                    if (distanceToNextCar > SAFE_DISTANCE && !carsPlannedRoute.isEmpty())
                    {
                        // there is enough space on lane to move and we know where to go
                        car.startMoving(1);
                    }  
                }
                continue;
            }
            else  // if (car.isParked()) 
            {
                Car nextCar = car.getNextCar();
                if (nextCar != null)
                {
                    // very simple move control
                    int distanceToNextCar = car.getNextCarDistance();
                    if (distanceToNextCar < SAFE_DISTANCE)
                        car.changeAcceleration(car.getAcceleration()/2);
                }
                
            }        
        }
    }
}
