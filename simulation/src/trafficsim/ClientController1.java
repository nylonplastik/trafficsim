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
import java.util.Random;
import java.util.Date;
import java.util.Hashtable;

/**
 * Singleton class responsible for somulation control.
 * @author Adam Rutkowski
 */
public class ClientController1 implements IController
{       
    // TODO: to be removed when communication is implemented
    private Model                serverModel;
    public void setServerModel(Model model) { serverModel = model; }
    
    private Model                      p_model;
    private ClientViewClientSide       p_view;
    private LinkedList<Integer>        p_controlledCars;
    
    // Safe distance between the cars to make one car start moving.
    private static final int           SAFE_START_DISTANCE = 50;  
   
    // Each car performs prediction of other cars movement basing on their
    // current speed and acceleration. This constant determines length of
    // time period used to calculate future position of other car
    private static final int           PREDICTION_TIME_FRAME = 3;
    
    // When other's car movenent prediction is done, the decission about
    // speed reduction is made basing on predicted distance between one car
    // and another after PREDICTION_TIME_FRAME. This constant determines a 
    // treshold value of predicted distance between cars.
    private static final int           SAFE_MOVE_DISTANCE = 50;  
    
    private static final int           CONTROLLED_CARS    = 10;
        
    private Random                     randomizer;
    
    public ClientController1(Model model, ClientViewClientSide view)
    {
        p_model          = model;
        p_view           = view;
        p_controlledCars = new LinkedList<Integer>();
        randomizer       = new Random(new Date().getTime());   
    }
    
    public void start()
    {
        int newCarId;
        Car newCar;
        LinkedList<Parking> parkings;
        
        if (!(parkings = p_model.getParkings()).isEmpty())
        {
            // start one new cars on first available parking
            for (int i = 0; i <CONTROLLED_CARS; i++) 
            {
                // new car parked on first parking
                // TODO: this will be replaced by sending request to server.
                // with no return value.
                newCarId = serverModel.newCar(parkings.get(0).getId());
                
                // TODO: when communication implemented, these lines 
                // are to be removed. This operation will be done
                // in newCarCallback method.
                serverModel.gotoParkingQueue(newCarId);
                p_controlledCars.add(newCarId);
                p_view.addObservedCar(newCarId);                  
            }
        }       
        
        serverModel.refresh();
    }
    
    public void newCarCallback(int newCarId)
    {
          p_controlledCars.add(newCarId);
          p_view.addObservedCar(newCarId);  
    }
    
    /***
     * Client's controller is notified about it's view change
     */
    public void viewChanged()
    {
        Hashtable<Integer, Car> carsInView = p_view.getCars();
        
        Car car;
        
        // for each controlled car in client's view
        for (int carId : p_controlledCars)
        {
            car = carsInView.get(carId);
            boolean routeHasChanged = false;
            
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
                        serverModel.startMoving(car.getId(), 3);
                    }  
                }
                continue;
            }
            else  // if (car.isParked()) 
            {
                // remove used lanes from planned route
                if(car.getPlannedRoute().contains(car.getPosition().getLane()))
                {
                    int index = car.getPlannedRoute().indexOf(
                                        car.getPosition().getLane());
                    for (int i=0; i<=index; i++)
                    {
                        routeHasChanged = true;
                        car.getPlannedRoute().remove();
                    }
                    
                }
                
                // Add random lane to car's planned route if it's empty
                if (car.getPlannedRoute().isEmpty())
                {
                    LanesCross next = car.getPosition().getLane().
                                                            getDestination();
                    LinkedList<Lane> possibleRoutes = next.getOutgoingLanes();
                    if (!possibleRoutes.isEmpty())
                    {
                        int rand = randomizer.nextInt(possibleRoutes.size());
                        LinkedList<Integer> newRoute = new LinkedList<Integer>();
                        newRoute.add(possibleRoutes.get(rand).getId());
                        serverModel.setPlannedRoute(car.getId(), newRoute);
                    }
                }
                
                // Acceleration adjustment.                
                // Determining if there is a need to reduce speed because
                // the net car os moving slowly.
                Car nextCar = car.getNextCar();
                
                float newAcc;
                if (nextCar != null)
                {
                    int predictedDiscance =  distancePrediction(
                                                car.getSpeed(), 
                                                nextCar.getSpeed(), 
                                                car.getNextCarDistance(), 
                                                PREDICTION_TIME_FRAME
                                                );
                   
                    if (predictedDiscance < SAFE_MOVE_DISTANCE)
                    {
                        double accCorection = accelerationToMove(
                                                PREDICTION_TIME_FRAME,
                                                SAFE_MOVE_DISTANCE - predictedDiscance);
                        newAcc = (float) (car.getAcceleration() - accCorection);
                        if (car.getSpeed() + newAcc < 0)
                            newAcc = (int) car.getSpeed();
                        if (newAcc > 3)
                            newAcc = 3;
                        car.changeAcceleration(newAcc);
                    }
                }
                
              
                // Determining if there is a need to reduce speed because
                // we need to stop (end of route, red light, ...)

                
                // get distance to the end of planned route
                LinkedList<Lane> plannedRoute = car.getRoute();
                Position currentPosition = car.getPosition();
                int distanceToEnd = currentPosition.getLane().getLength()
                                               - currentPosition.getCoord();
                
                if (distanceToEnd == 0)
                {
                    if (car.getSpeed() > 0)
                        car.changeAcceleration(car.getSpeed());
                    break;
                }
                
                if (plannedRoute != null)
                {
                    for(Lane l : plannedRoute)
                        distanceToEnd += l.getLength();
                }
                
                double acceleration = adjustAcceleration(distanceToEnd, 
                                                        car.getSpeed(), 
                                                        car.getAcceleration()
                                                        );
                
                // When approaching to the end, keep slowing down
                if (acceleration < -1*car.getMaxAcceleration()/2 && distanceToEnd > car.getMaxAcceleration())
                    car.changeAcceleration((float)acceleration);
                
                // When close enough, stop slowing down
                if (distanceToEnd < car.getMaxAcceleration())
                    car.changeAcceleration(0);
                
                // final stop
                if (2*car.getSpeed() + car.getAcceleration() > distanceToEnd)
                {
                    float newAcc2 = distanceToEnd - 2*((int)(car.getSpeed()));
                    car.changeAcceleration(newAcc2);
                }  
                
            }        
            
            serverModel.setAcceleration(car.getId(), car.getAcceleration());
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
    
    private static double adjustAcceleration(int distance, 
                                            double speed, 
                                            double maxAcc                                      
                                            )
    {
        return acccelerationToStopAt(speed, distance);
    }
    
    private static double timeToReachPoint(int dist, double speed, double acc)
    {
        double delta = (2*speed-acc)*(2*speed-acc) + 4*acc*dist;
        double time = (acc - 2*speed + Math.sqrt(delta)) / (2*acc);
        return time;
    }
    
    private static double acccelerationToStopAt(double speed, int dist)
    {
        return speed/(1-2*dist/speed);
    }
    
    private static double accelerationToMove(int time, int dist)
    {
        return 2*dist/(double)(time*time);
    }

}
