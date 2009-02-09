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

/**
 * Lights on Lane
 * @author Mariusz Ceier
 */
public class Lights //{{{
{
    // Variables {{{
    
    /**
     * Lane
     */
    private Lane p_lane;
    
    /**
     * Distance from LanesCross
     */
    private int p_distance;

    /**
     * Current state shared with other lights on the same lane
     */
    private LightsState p_state;

    // }}}

    /**
     * Constructs new lights
     */
    public Lights(Lane lane, int distance, LightsState state)//{{{
    {
        p_lane = lane;
        p_distance = distance;
        p_state = state;
        
        //p_state.addObserver(this);
    }//}}}

    /**
     * @return Lights state
     */
    public LightsState getState()//{{{
    {
        return p_state;
    }//}}}

    /**
     * @return Lane
     */
    public Lane getLane()//{{{
    {
        return p_lane;
    }//}}}

    /**
     * @return Distance from LanesCross
     */
    public int getDistance()//{{{
    {
        return p_distance;
    }//}}}

    
    /**
     * Lights update - e.g. displaying new state etc.
     */
   /*
    public void update(Observable ov, Object arg)//{{{
    {
    }//}}}
    */

}//}}}
  
/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
