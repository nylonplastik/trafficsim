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

import java.io.Serializable;
import java.util.*;

// }}}

/**
 * Represents place where at least two lanes cross or join.
 * 
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class LanesCross extends Observable implements Serializable //{{{
{
    // Variables {{{
    private int    X_coordinate;
    private int    Y_coordinate;
    
    private static int    s_count;
    private final int     p_id;
    
    /* adjacent lane crosses with lanes to them */
    private Hashtable <Integer,Lane>    p_adjecentCrosses = new Hashtable<Integer, Lane>();
    private LinkedList<Lane>            p_incomingLanes = new LinkedList<Lane>();
    private LinkedList<Lane>            p_outgoingLanes = new LinkedList<Lane>();
    // }}}

    public LinkedList<Lane> getOutgoingLanes()//{{{
    {
        return p_outgoingLanes;
    }//}}}
    
    private synchronized int getNewId()
    {
        return s_count++;
    }
    
    public LanesCross()//{{{
    {
        this.p_id =  getNewId();
        X_coordinate = 0;
        Y_coordinate = 0;
    }//}}}
    
    public LanesCross(int id, int X, int Y)//{{{
    {
        this.p_id =  id;;
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
    public synchronized  boolean addConnection(int destCross, Lane lane)//{{{
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
    public synchronized void addIncoming(Lane lane)//{{{
    {
       p_incomingLanes.add(lane);
    }//}}}
    
    public synchronized void addOutgoing(Lane lane)
    {
        p_outgoingLanes.add(lane);
    }
    public synchronized int getX() {
        return X_coordinate;
    }

    public synchronized void setX(int newX) {
        X_coordinate = newX;
    }

    public synchronized int getY() {
        return Y_coordinate;
    }

    public synchronized void setY(int newY) {
        Y_coordinate = newY;
    }

    public int getId() {
        return p_id;
    }
    
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
