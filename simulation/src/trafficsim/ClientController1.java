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
    private static final int           SAFE_START_DISTANCE = 5;  
   
    // Each car performs prediction of other cars movement basing on their
    // current speed and acceleration. This constant determines length of
    // time period used to calculate future position of other car
    private static final int           PREDICTION_TIME_FRAME = 3;
    
    // When other's car movenent prediction is done, the decission about
    // speed reduction is made basing on predicted distance between one car
    // and another after PREDICTION_TIME_FRAME. This constant determines a 
    // treshold value of predicted distance between cars.
    private static final int           SAFE_MOVE_DISTANCE = 50;  
    
    private static final int           CONTROLLED_CARS    = 3;
    
    private static final int           MAX_SPEED_ON_90_DEGREE_ANGLE = 3;
        
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
                    float  distanceToNextCar;
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
                    float predictedDiscance =  distancePrediction(
                                                car.getSpeed(), 
                                                nextCar.getSpeed(), 
                                                car.getNextCarDistance(), 
                                                PREDICTION_TIME_FRAME
                                                );
                   
                    if (predictedDiscance < SAFE_MOVE_DISTANCE)
                    {
                        float accCorection = accelerationToMove(
                                                PREDICTION_TIME_FRAME,
                                                SAFE_MOVE_DISTANCE - predictedDiscance);
                        newAcc = (float) (car.getAcceleration() - accCorection);
                        if (car.getSpeed() + newAcc < 0)
                            newAcc = (int) car.getSpeed();
                        if (newAcc > 3)
                            newAcc = 3;
                        car.setAcceleration(newAcc);
                    }
                }
                
              
                // Determining if there is a need to reduce speed because
                // we need to stop (end of route, red light, ...)

                
                // get distance to the end of planned route
                LinkedList<Lane> plannedRoute = car.getRoute();
                Position currentPosition = car.getPosition();
                float distanceToEnd = currentPosition.getLane().getLength()
                                               - currentPosition.getCoord();
                
                if (distanceToEnd == 0)
                {
                    if (car.getSpeed() > 0)
                        car.setAcceleration(car.getSpeed());
                    break;
                }
                
                if (plannedRoute != null)
                {
                    for(Lane l : plannedRoute)
                        distanceToEnd += l.getLength();
                }
                
                float accFactor1 = adjustAcceleration(distanceToEnd, 
                                                        car.getSpeed(), 
                                                        car.getAcceleration()
                                                        );
                
                float accFactor2 = adjustAccelerationToCornerAngle(
                                                        car.getSpeed(), 
                                                        car.getMaxSpeed(), 
                                                        car.getPosition(), 
                                                        car.getPlannedRoute(), 
                                                        accFactor1
                                                        );
                float newAcceleration =accFactor2;
                if (accFactor1 < accFactor2)
                    newAcceleration  = accFactor1;
                    
                
                // When approaching to the end, keep slowing down
                if (newAcceleration < -1*car.getMaxAcceleration()/2 && distanceToEnd > car.getMaxAcceleration())
                {
                    car.setAcceleration(newAcceleration);
                }
                else car.setAcceleration(3);

                // When close enough, stop slowing down
                if (distanceToEnd < car.getMaxAcceleration())
                    car.setAcceleration(0);
                
                // final stop
                if (2*car.getSpeed() + car.getAcceleration() > distanceToEnd)
                {
                    float newAcc2 = distanceToEnd - 2*((int)(car.getSpeed()));
                    car.setAcceleration(newAcc2);
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
            float distance,
            int timeFrame)
    {
        return (int)( distance + speed2*timeFrame - speed1*timeFrame);
    }
    
    private static float adjustAcceleration(float distance, 
                                            float speed, 
                                            float maxAcc                                      
                                            )
    {
        return acccelerationToStopAt(speed, distance);
    }
    
    private static float timeToReachPoint(float dist, float speed, float acc)
    {
        float delta = (2*speed-acc)*(2*speed-acc) + 4*acc*dist;
        float time = (acc - 2*speed + (float)Math.sqrt(delta)) / (2*acc);
        return time;
    }
    
    private static float acccelerationToStopAt(float speed, float dist)
    {
        return speed/(1-2*dist/speed);
    }
    
    private static float accelerationToChangeSpeed(
                                float initialSpeed,
                                float endSpeed,
                                float dist)
    {
        return acccelerationToStopAt(endSpeed - initialSpeed, dist);
    }
    
    private static float accelerationToMove(int time, float dist)
    {
        return 2*dist/(float)(time*time);
    }
    
    private static float angle(
                            int x1, int y1, 
                            int x2, int y2,
                            int x3, int y3 )
    {
        int v1x = x1-x2;
        int v1y = y1-y2;
        int v2x = x3-x2;
        int v2y = y3-y2;
        
        int product = v1x*v2x + v1y*v2y;
        
        if (product == 0)
            return (float) Math.PI/2;
        float d1 = (float) Math.sqrt(v1x*v1x+v1y*v1y);
        float d2 = (float) Math.sqrt(v2x*v2x+v2y*v2y);
        
        float angle = (float) Math.acos(product/(d1*d2));
        if (angle > Math.PI)
            angle = 2* (float)Math.PI - angle;
        return angle;
    }
    
    private float angle (Lane l1, Lane l2)
    {
        if (l1.getDestination() != l2.getSource())
            return 0;
        
        return angle(
                l1.getSource().getX(), l1.getSource().getY(),
                l2.getSource().getX(), l2.getSource().getY(),
                l2.getDestination().getX(), l2.getDestination().getY()
                );
    }
    
    private float adjustAccelerationToCornerAngle (
                            float currentSpeed,
                            float maxSpeed,
                            Position position,
                            LinkedList<Lane> plannedRoute,
                            float defaultAcceleration)
    {
        if (plannedRoute.isEmpty())
            return defaultAcceleration;
        
        float distanceToCorner = position.getLane().getLength() - position.getCoord();
        float angle = angle(position.getLane(), plannedRoute.get(0));
        float newSpeed = (float) ((Math.PI - angle)/(Math.PI/2) * 
                                    MAX_SPEED_ON_90_DEGREE_ANGLE);
        if (newSpeed < 0)
            newSpeed = 0;
        if (newSpeed < currentSpeed)
        {
            return accelerationToChangeSpeed(
                                        currentSpeed, 
                                        newSpeed, 
                                        distanceToCorner
                                        );
        }
        else return defaultAcceleration;
    }

}
