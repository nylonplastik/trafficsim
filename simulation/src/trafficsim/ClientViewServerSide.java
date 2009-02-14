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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import trafficSim.CarData;
import trafficSim.ParkingData;
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
    
    private static final int     VIEW_RADIUS = 100;
    
    private ClientViewData       p_data;
    
    private Model                p_model;
        
    private LinkedList<Car>      p_observedCars;
     
    
    // TODO: this field should be removed and it's methods invocation replaced
    // by interprocess communication
    private ClientViewClientSide p_clientSideView;
    
    //}}}
    
    public ClientViewServerSide(
            ClientViewClientSide clientSideView,
            Model model
            ) //{{{
    {
        p_clientSideView = clientSideView;
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
    
    public void update(Observable o, Object arg) //{{{
    {
        if (! (o instanceof  Model))
            return;
        Model model = (Model)o;
        
        /* Set information about current state of cars */
        p_data.setCarData(new Hashtable<Integer, CarData>());
        Hashtable<Integer, CarData> carData = p_data.getCarData();
        for (Car c : p_observedCars)
        {
            carData.put(c.getId(), new CarData(c));
        }
        p_data.setCarData(carData);
        
        /* Set information about current state of parkings */
        p_data.setParkingData(new Hashtable<Integer, trafficSim.ParkingData>());
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
        
        updateClientSideView();
    } //}}}
    
    void updateClientSideView()
    {
        p_clientSideView.viewChanged(p_data);
    }
    
    @Override
    public void finalize() throws Throwable
    {
        p_model.deleteObserver(this);
    }
        
    private void createClientView(Model model)
    {
        /*
        p_data.getBorderCrosses().clear();
        p_data.getCars().clear();
        p_data.getCrosses().clear();
        p_data.getLanes().clear();
        
        LinkedList<Lane>        closestLanes   = new LinkedList<Lane>();
        LinkedList<LanesCross>  closestCrosses = new LinkedList<LanesCross>();
        LinkedList<Car>         closestCars    = new LinkedList<Car>(); 
        
        for (Car c : p_observedCars)
        {
            addClosestLanesAndCrossses(c, closestLanes, closestCrosses);
            closestCars.add(c);
        }
        
        
        // Get all cars from close lanes
        for (Lane l : closestLanes)
        {
            SortedMap<Integer, Car> carsOnLane = l.getCarsOnLane();
            Set<Integer> ids;
            
            synchronized(carsOnLane)
            {
                ids = carsOnLane.keySet();
            
                for(Integer id : ids)
                    if (!closestCars.contains(carsOnLane.get(id)))
                        closestCars.add(carsOnLane.get(id));
            }       
        }
        
        p_data.setCars(closestCars);
        p_data.setLanes(closestLanes);
        
        for(LanesCross c : closestCrosses)
            p_data.getCrosses().put(c.getId(), c);
        
        updateClientSideView();
         * */
    }
/*    
    private void addClosestLanesAndCrossses(
                                 Car car, 
                                 LinkedList<Lane> lanes,
                                 LinkedList<LanesCross> crosses
                                 )
    { 
        if (car.getPosition().info == Position.e_info.NOT_DRIVING)
            return;
        
        Lane currentLane = car.getPosition().getLane();
        LinkedList<Lane> adjecentLanes = currentLane.getAdjacentLanes();
        
        // add adjecent lines
        for (Lane l : adjecentLanes)
            if (!lanes.contains(l))
                lanes.add(l);
        
        // add crosses on the end of added lanes
        LinkedList<LanesCross> closestCrosses = new LinkedList<LanesCross>();
        LinkedList<LanesCross> endingCrosses  = new LinkedList<LanesCross>();
        LinkedList<LanesCross> borderCrosses  = new LinkedList<LanesCross>();
        
        for (Lane l : adjecentLanes)
        {
            if (!closestCrosses.contains(l.getDestination()))
            {
                 closestCrosses.add(l.getDestination());       
                 endingCrosses.add(l.getDestination());
            }
            if (!closestCrosses.contains(l.getSource()))
                closestCrosses.add(l.getSource());                  
        }
        
        // add lanes outgoing from adjecent crosses
        for (LanesCross c : endingCrosses)
        {
            if (!crosses.contains(c))
                crosses.add(c);
            
            for( Lane l : c.getOutgoingLanes() )
                if (!lanes.contains(l))
                    lanes.add(l);
        }
          
    }
    */
} //}}}


/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
