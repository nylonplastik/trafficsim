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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;

import trafficsim.data.ClientViewData;
import java.util.Hashtable;

/**
 *
 * @author Adam Rutkowski
 */
public class ClientViewClientSide { //{{{
    // Variables {{{
    
    protected Hashtable<Integer, Car>     p_cars;            
    
    private IController         p_controller;
    
    private Model               p_model;

    
    public ClientViewClientSide(Model model)
    {
        p_model = model;
        p_cars = new Hashtable<Integer, Car>();
    }
    
    
    public void setController(ICarController controller)
    {
        p_controller = controller;
    }    

    
    // TODO: eventually this class should be made private when interprocess
    // communication is implemented. It should be invoked by communication 
    // routine upon receiving data from server.
    public void viewChanged(ClientViewData data)
    { //{{{
        
        for (int i: p_cars.keySet())
        {
            Car car = p_cars.get(i);
            if (car.isParked())
                car.getCurrentParking().carIsLeaving(car);
            else
                car.getPosition().getLane().carIsLeaving(
                        car.getPosition().getCoord()
                        );
        }
        
        p_cars = new Hashtable<Integer, Car>();
        
        // Create Car class instances basing on data from server
        for(Integer id : data.getCarData().keySet())
        {
            Car newCar = new Car(data.getCarData().get(id), p_model);
            if (!newCar.isParked())
            {
                p_cars.put(id, newCar);
                Lane l= p_model.getLaneById(newCar.getPosition().getLane().getId());
                l.putCar(newCar.getPosition().getCoord(), newCar);
            }
            else
            {
                Parking parking = newCar.getCurrentParking();
                parking.park(newCar);
            }
            p_cars.put(id, newCar);
        }
        
        // update parking info basing on data from server
        for (Integer id : data.getParkingData().keySet())
        {
            p_model.getParkingById(id).updateData(
                    data.getParkingData().get(id),
                    p_cars
                    );
        }
        
        p_controller.viewChanged();
    } //}}}

    public Hashtable<Integer, Car> getCars() {
        return p_cars;
    }

    protected synchronized Model getModel() {
        return p_model;
    }

    public synchronized void setMmodel(Model p_model) {
        this.p_model = p_model;
    }

} //}}}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
