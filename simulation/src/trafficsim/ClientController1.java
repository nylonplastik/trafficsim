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

import java.lang.Math;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Singleton class responsible for simulation control.
 * @author Adam Rutkowski
 */
public class ClientController1 implements IController
{       
    private Model                      p_model;
    private ClientViewClientSide       p_view;
    private LinkedList<Car>            p_controlledCars;
    
    // Safe distance between the cars to make one car start moving.
    private static final int           SAFE_START_DISTANCE = 10;  
   
    // Each car performs prediction of other cars movement basing on their
    // current speed and acceleration. This constant determines length of
    // time period used to calculate future position of other car
    private static final int           PREDICTION_TIME_FRAME = 3;
    
    // When other's car movement prediction is done, the decision about
    // speed reduction is made basing on predicted distance between one car
    // and another after PREDICTION_TIME_FRAME. This constant determines a 
    // threshold value of predicted distance between cars .
    private static final int           SAFE_MOVE_DISTANCE = 10;  
        
    
    
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
            for (int i = 0; i <2; i++) 
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
        LinkedList<Car> carsInView = p_view.data.getCars();
        Car car;
        
        // for each controlled car in client's view
        for (Iterator<Car> it = carsInView.iterator(); it.hasNext(); )
        {
            car = it.next();
               
            if (car.isParked())
            {
                 // if car is parked, try to leave the parking
                if (car.canLeaveParking(SAFE_START_DISTANCE))
                {
                    int  distanceToNextCar;
                    Car nextCar = car.getNextCar();
                    LinkedList<Lane> carsPlannedRoute = car.getRoute();
                    
                    if (nextCar == null)
                        distanceToNextCar = 2*SAFE_START_DISTANCE;
                    else
                        distanceToNextCar = car.getNextCarDistance();
                    
                    if (distanceToNextCar > SAFE_START_DISTANCE && 
                        !carsPlannedRoute.isEmpty()
                        )
                    {
                        // there is enough space on lane to move and we know 
                        // where to go.
                        car.startMoving(1);
                    }  
                }
                continue;
            }
            else  // if (car.isParked()) 
            {
                // Acceleration adjustment.

                // Determining if there is a need to reduce speed because
                // the net car is moving slowly.
                Car nextCar = car.getNextCar();
                if (nextCar != null)
                {
                    int predictedDiscance =  distancePrediction(
                                                car.getSpeed(), 
                                                nextCar.getSpeed(), 
                                                car.getNextCarDistance(), 
                                                PREDICTION_TIME_FRAME
                                                );
                   
                    int newAcc = predictedDiscance - SAFE_MOVE_DISTANCE;
                    if (car.getSpeed() + newAcc < 0)
                        newAcc = (int) car.getSpeed();
                    car.changeAcceleration(newAcc);
                }
                
               
                // Determining if there is a need to reduce speed because
                // we need to stop (end of route, red light, ...)

                
                // get distance to the end of planned route
                LinkedList<Lane> plannedRoute = car.getRoute();
                Position currentPosition = car.getPosition();
                int distanceToEnd = currentPosition.getLane().getLength()
                                               - currentPosition.getCoord();
                if (plannedRoute != null)
                {
                    for(Lane l : plannedRoute)
                        distanceToEnd += l.getLength();
                }
                
                float acceleration = adjustAcceleration(distanceToEnd, 
                                                        car.getSpeed(), 
                                                        car.getAcceleration()
                                                        );
                
           }        
        }
    }
    
    // Trying to predict distance between two cars basing on their current speed
    // and current distance between them.
    private static int distancePrediction(
            float speed1, 
            float speed2, 
            int distance,
            int timeFrame)
    {
        return (int)( distance + speed2*timeFrame - speed1*timeFrame);
    }
    
    private static float adjustAcceleration(int distance, 
                                            float speed, 
                                            float maxAcc
                                            
                                            )
    {
        int t1 = (int)Math.floor(timeToReachPoint(distance, speed, -1*  maxAcc));
        
        if (speed*t1 - t1*t1*(maxAcc/2)/2 > distance)
        	return 0;
        return maxAcc; //4*(speed*t1-distance)/t1*t1;
    }
    
    private static double timeToReachPoint(int dist, float speed, float acc)
    {
        return Math.floor ( 
            speed*(1-Math.sqrt(1 + 2*(acc/2)*dist/speed*speed))/(acc/2) 
            ); 
    }
    
    private static double positionAfterTime(int speed, int acc, int time)
    {
        return acc*acc*time/2 + speed * time;
    }

}
