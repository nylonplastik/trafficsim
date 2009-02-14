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
import java.util.Hashtable;

/**
 *
 * @author Adam Rutkowski
 */
public class ClientViewData { //{{{
    
    // Variables {{{    
    
    /**
     * List of all lanes close enough to controlled cars.
     */
    private Hashtable<Integer, LanesCross>      crosses;
    
    /***
     * List of all lanes close enough to controlled cars.
     */
    private LinkedList<Lane>                    lanes;
    
    private LinkedList<Car>                     cars;     

    /**
     * List of lane crosses which are on the border of this client view.
     * (their nejghbour lanes crosses' lists are empty not becouse there
     *   are no adjecent lanes crosses, but becouse the client view is not
     *   wide enough to contain them).
     */
    private LinkedList<LanesCross>             borderCrosses;
    
    //}}}
    
    public ClientViewData() //{{{
    {
        setBorderCrosses(new LinkedList<LanesCross>());
        setCars(new LinkedList<Car>());
        setLanes(new LinkedList<Lane>());
        setCrosses(new Hashtable<Integer, LanesCross>());
    } //}}}

    public void setCrosses(Hashtable<Integer, LanesCross> crosses) {
        this.crosses = crosses;
    }

    public Hashtable<Integer, LanesCross> getCrosses() {
        return crosses;
    }

    public void setLanes(LinkedList<Lane> lanes) {
        this.lanes = lanes;
    }

    public LinkedList<Lane> getLanes() {
        return lanes;
    }

    public void setCars(LinkedList<Car> cars) {
        this.cars = cars;
    }

    public LinkedList<Car> getCars() {
        return cars;
    }

    public void setBorderCrosses(LinkedList<LanesCross> borderCrosses) {
        this.borderCrosses = borderCrosses;
    }

    public LinkedList<LanesCross> getBorderCrosses() {
        return borderCrosses;
    }
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */

