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

import java.util.*;
import java.util.Hashtable;
import java.util.Observable;

// }}}

/**
 * Represents place where at least two lanes cross or join.
 * 
 * @author Adam Rutkowski
 */
public class LanesCross extends Observable //{{{
{
    // Variables {{{
    public final int    X_coordinate;
    public final int    Y_coordinate;
    
    private static int    s_count;
    private int           p_id;
    
    /* adjacent lane crosses with lanes to them */
    private Hashtable <Integer,Lane>    p_adjecentCrosses = new Hashtable<Integer, Lane>();
    private LinkedList<Lane>            p_incomingLanes = new LinkedList<Lane>();
    // }}}

    public LinkedList<Lane> getOutgoingLanes()//{{{
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }//}}}
    
    public LanesCross()//{{{
    {
        p_id = s_count++;
        X_coordinate = 0;
        Y_coordinate = 0;
    }//}}}
    
    public LanesCross(int id, int X, int Y)//{{{
    {
        p_id = id;
        X_coordinate = X;
        Y_coordinate = Y;        
        s_count++;
    }//}}}
    
    /**
     * add connection to other lanes cross through given lane.
     * @param destCross
     * @param lane
     * @return true if added connection, false if connection to destCross already exists
     */
    public boolean addConnection(int destCross, Lane lane)//{{{
    {
        if (p_adjecentCrosses.containsKey(destCross))
            return false;
        p_adjecentCrosses.put(destCross, lane);
        return true;
    }//}}}
    
    /**
     * add lane to list of lanes which destination is this crosss.
     * @param lane
     */
    public void addIncoming(Lane lane)//{{{
    {
       p_incomingLanes.add(lane);
    }//}}}
     
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
