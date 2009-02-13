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
        
        createClientView(model);
            
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
        p_data.getBorderCrosses().clear();
        p_data.getCars().clear();
        p_data.getCrosses().clear();
        p_data.getLanes().clear();
        p_data.getObservedCars().clear();
        
        
        // TODO: it's a shallow copy only. Doesn't make much sense.
        synchronized  (model)
        {
            p_data.setCrosses((Hashtable<Integer, LanesCross>) model.getLanesCrosses().clone());
            p_data.setLanes((LinkedList<Lane>) model.getLanes().clone());
            p_data.setCars((LinkedList<Car>) model.getCars().clone());
        }
         
        // TODO : add some real client view creation        
/*          
        foreach (Car c : p_observedCars)
        {
            addClosestLanes()
        }
 */
    }
    
} //}}}


/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
