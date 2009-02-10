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

/**
 * Position of car
 * @author Adam Rutkowski
 */
public class Position {
    public Lane     lane;
    public Integer  coord;
    public enum     e_info
    {
        OK,             // lane and coord fields describe accurate position after time
        OUT_OF_RANGE,   // planned route is to short, the car will get out
        NOT_DRIVING     // the car is not on the street
    };
    e_info          info;
}
