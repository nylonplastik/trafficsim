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
// }}}

/**
 * Lights state - shared among Lights
 * @author Mariusz Ceier
 */
public class LightsState //{{{
{
    // Variables {{{

    /**
     * Lights state
     */
    public enum State
    {  
        RED, // Car must stop before lights in this state
        RED_AND_YELLOW, // Car can start moving
        YELLOW, // Car should stop
        BLINKING_YELLOW, // Car should decide if it can safely 
                         // pass the lights ...
        GREEN // Car can freely pass the lights
    };

    /**
     * Current state - when this changes every observer is notified
     * Default state is RED - as we don't want any collisions,
     * even if there is no LightsStateController
     */
    private State p_state = State.RED;

    // }}}

    /**
     * Default constructor
     */
    public LightsState()//{{{
    {
    }//}}}

    /**
     * Non-default lights state constructor
     * We notify observers if state is really changed
     */
    public LightsState(State state)//{{{
    {
        p_state = state;
    }//}}}

    /**
     * Sets new state
     */
    public void setState(final State state)//{{{
    {
        boolean changed = false;
        if (p_state!=state) changed=true;
        p_state = state;
        
/*        
        if (changed)
        {
            setChanged();
            notifyObservers();
        }
 */
    }//}}}

    /**
     * We don't allow to change state in other way than by setState
     * @return Current state of lights
     */
    public final State getState()//{{{
    {
        return p_state;
    }//}}}

}//}}}

/* vim: set ts=4 sts=4 sw=4 et foldmethod=marker: */
