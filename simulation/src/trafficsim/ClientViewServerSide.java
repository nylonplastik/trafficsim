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

import trafficsim.data.ClientViewData;
import trafficsim.data.CarData;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;
import trafficsim.Position.e_info;
import trafficsim.data.ParkingData;

//}}}



/**
 *
 * @author Adam Rutkowski
 * Active client view class. Filters data from model to form client's view and
 * sends view data do client(controller) process.
 */
public class ClientViewServerSide implements Observer  //{{{
{
    // Variables {{{
    
    @SuppressWarnings("unused")
    private static final int     VIEW_RADIUS = 100;
    
    private ClientViewData       p_data;
    
    private Model                p_model;
        
    private LinkedList<Car>      p_observedCars;
    
    private boolean changed = false;
    
    //}}}
    
    public ClientViewServerSide(
            Model model
            ) //{{{
    {
        p_data           = new ClientViewData();
        p_model          = model;
        p_observedCars   = new LinkedList<Car>();
        model.addObserver(this);
    } //}}}
    
    public void addObservedCar(int carId) //{{{
    { 
        synchronized(p_model.getCars())
        {
            p_observedCars.add(p_model.getCars().get(carId));
        }
    } //}}}
    
    public void delObservedCar(int carId) //{{{
    {
        Car car;
        
        synchronized (p_model.getCars())
        {
             car = p_model.getCars().get(carId);
        }
        
        synchronized (car)
        {
            if (p_observedCars.contains(car))
                p_observedCars.remove(car);
        }
    } //}}}
    
    public synchronized void update(Observable o, Object arg) //{{{
    {
        System.out.println("Starting view update, view id = ");
        synchronized (p_data)
        {  
            if (! (o instanceof  Model))
                return;

            /* Set information about current state of cars */
            p_data.setCarData(new Hashtable<Integer, CarData>());
            Hashtable<Integer, CarData> carData = p_data.getCarData();
            for (Car c : p_observedCars)
            {
                synchronized(c)
                {
                    carData.put(c.getId(), new CarData(c));

                    // copy data about cars on this car current lane
                    if (c.getPosition().getLane() != null && c.getPosition().getInfo() == e_info.OK)
                    {                    
                           Lane current = c.getPosition().getLane(); 
                           SortedMap<Integer, Car> carsOnLane = current.getCars();
                           synchronized(carsOnLane)
                           {
                               if (carsOnLane != null)
                               {
                                   for (Car car : carsOnLane.values())
                                   {
                                       if (car != null)
                                       synchronized(car)
                                       {
                                            if (!carData.containsKey(car.getId()))
                                            carData.put(car.getId(), new CarData(car));
                                       }
                                   }
                               }
                           }
                    }

                    // copy data about cars on next line planned to visit
                    Lane nextLane = null;
                    if (c.getPlannedRoute() != null)
                    {
                        if (!c.getPlannedRoute().isEmpty())
                            nextLane = c.getPlannedRoute().getFirst();
                    }
                    if (nextLane != null)
                    {
                       SortedMap<Integer, Car> carsOnLane = nextLane.getCars();
                       if (carsOnLane != null)
                       {
                           for (Integer position : carsOnLane.keySet())
                           {
                               Car car = carsOnLane.get(position);
                               if (car!=null)
                               {
                                    if (!carData.containsKey(car.getId()))     
                                    carData.put(car.getId(), new CarData(car));
                               }
                           }
                       }                    
                    }
                }
                System.out.println("Finished view update, view id = ");
            }
            
            
            p_data.setCarData(carData);

            /* Set information about current state of parkings */
            p_data.setParkingData(new Hashtable<Integer, trafficsim.data.ParkingData>());
            Hashtable<Integer, ParkingData> parkingData  = p_data.getParkingData();

            /* find parkings that our cars are parked at */
            LinkedList<Parking> observedParkings = new LinkedList<Parking>();
            for (Car c : p_observedCars)
                if (c.isParked())
                    if (!observedParkings.contains(c.getCurrentParking()))
                        observedParkings.add(c.getCurrentParking());

            /* copy data about parkings */
            for (Parking p : observedParkings)
            {
                parkingData.put(p.getId(),  new ParkingData(p));
            }
            p_data.updateId();
            setChanged();
        }
    } //}}}
    
    
    @Override
    public void finalize() throws Throwable
    {
        p_model.deleteObserver(this);
    }

    public synchronized  boolean hasChanged() {
        boolean bRet = changed;
        changed = false;
        return bRet;
    }

    public synchronized  void setChanged() {
        this.changed = true;
    }
    
    public synchronized ClientViewData getData()
    {
        return p_data;
    }

} //}}}


/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
