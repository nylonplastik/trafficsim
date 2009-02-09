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
import java.util.Observable;
import java.util.Observer;
import java.util.Hashtable;

//}}}

/**
 *
 * @author Adam Rutkowski
 */
public class ClientView implements Observer  //{{{
{
    // Variables {{{
    private IController     p_controller;
    private LinkedList<Car> p_observerCars;
    
    // TODO: following fields should not be be public (writeable?).
    
    /**
     * List of all lanes close enough to controlled cars.
     */
    public Hashtable<Integer, LanesCross>      p_crosses;
    
    /***
     * List of all lanes close enough to controlled cars.
     */
    public LinkedList<Lane>                     p_lanes;
    
    /**
     * List of lane crosses which are on the border of this client view.
     * (their nejghbour lanes crosses' lists are empty not becouse there
     *   are no adjecent lanes crosses, but becouse the client view is not
     *   wide enough to contain them).
     */
    public LinkedList<LanesCross>               p_borderCrosses;
    
    public LinkedList<Car>                      p_cars; 
    
    //}}}
    
    public ClientView() //{{{
    {
        p_controller    = null;
        p_observerCars  = new LinkedList<Car>();
        p_borderCrosses = new LinkedList<LanesCross>();
        p_cars          = new LinkedList<Car>();
    } //}}}
    
    public void addObservedCar(Car car) //{{{
    {
        p_observerCars.add(car);
    } //}}}
    
    public void delObservedCar(Car car) //{{{
    {
        if (p_observerCars.contains(car))
            p_observerCars.remove(car);
    } //}}}
    
	public void update(Observable o, Object arg) //{{{
    {
        if (! (o instanceof  Model))
            return;
        Model model = (Model)o;
        
        synchronized  (model)
        {
            p_crosses = (Hashtable<Integer, LanesCross>) model.getLanesCrosses().clone();
            p_lanes   = (LinkedList<Lane>) model.getLanes().clone();
            p_cars    = (LinkedList<Car>) model.getCars().clone();
            // TODO : add some real client view creation
        }
         
        if (p_controller!=null)
            p_controller.viewChanged();
    } //}}}
    
    public void setController(IController controller) //{{{
    {
        p_controller = controller;
    } //}}}
} //}}}


/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
