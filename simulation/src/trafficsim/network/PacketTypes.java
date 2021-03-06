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

package trafficsim.network;

public class PacketTypes
{
    public static final int EMPTY_TYPEID             = 0;
    public static final int NEW_MODEL_DATA           = 1;
    public static final int MODEL_DATA_UPDATE_TYPEID = 2;
    public static final int CAR_DATA_TYPEID 	     = 3;
    public static final int MODEL_DATA_TYPEID        = 4;
    public static final int PARKING_DATA_TYPEID      = 5;
    public static final int CLIENTVIEW_DATA_TYPEID   = 6;
    public static final int REGISTER_CLIENT_TYPEID   = 7;
    public static final int REGISTRED_TYPEID         = 8;
    public static final int PUT_CAR_IN_QUEUE         = 9;
    public static final int SPAWN_NEW_CAR            = 10;
    public static final int NEW_CAR_SPAWNED          = 11;
    public static final int NEW_MODEL_DATA_TYPEID    = 12;
    public static final int CHANGE_ACCELER_TYPEID    = 13; 
    public static final int CHANGE_ROUTE_TYPEID      = 14; 
    public static final int START_MOVING             = 15; 
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
