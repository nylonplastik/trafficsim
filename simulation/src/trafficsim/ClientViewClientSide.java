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

import java.util.LinkedList;


/**
 *
 * @author Adam Rutkowski
 */
public class ClientViewClientSide { //{{{
    // Variables {{{
    
    public ClientViewData       data;
    
    private IController         p_controller;

    
    // TODO: this field should be removed and it's methods invocation replaced
    // by interprocess communication
    private ClientViewServerSide p_serverSideView;
    
    //}}}    
    
    // TODO: when communication implemented, this should be removed as well.
    public void setServerSideView(ClientViewServerSide serverSideView)
    {
        p_serverSideView = serverSideView;
    }
    
    public void setController(IController controller)
    {
        p_controller = controller;
    }    

    public void addObservedCar(Car car)
    {
        if (p_serverSideView != null)
            p_serverSideView.addObservedCar(car.getId());
    }
    
    public void delObservedCar(Car car)
    {
        p_serverSideView.delObservedCar(car.getId());
    }
    
    // TODO: eventually this class should be made private when interprocess
    // communication is implemented. It should be invoked by communication 
    // routine upon receiving data from server.
    public void viewChanged(ClientViewData data)
    { //{{{
        this.data = data;
        p_controller.viewChanged();
    } //}}}
} //}}}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
