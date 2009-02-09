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
import java.util.logging.Level;
import java.util.logging.Logger;

//}}}

/**
 * Time controller - singleton
 * @author Adam Rutkowski
 */
public class TimeController implements Runnable //{{{
{
    // Variables {{{
    private static Logger s_log = Logger.getLogger(TimeController.class.toString());
    private static TimeController  s_controller = null;
    private Model   p_model;
    private int     p_timeTick = 1;
    private int     p_timeMilisecs = 100;
    //}}}
    
    private TimeController(Model model) //{{{
    {
        p_model = model;
    } //}}}
    
    public static TimeController getTimeController(Model m) //{{{
    {
        if (s_controller == null)
            s_controller = new TimeController(m);
         return s_controller;
    } //}}}
    
    private void updateView() //{{{
    {
        synchronized(p_model)
        {
            LinkedList<Car> cars  = p_model.getCars();
            for (Car car : p_model.getCars())
            {
                try
                {
                    car.move(p_timeTick);
                }
                catch (Exception ex)
                {
                    // FIXME: should be break ? 
                    return;
                }
            }
        }
        // TODO: update lights state
        p_model.finishedTimeUpdate();
    } //}}}
    
    public void run()  //{{{
    {
        while(true)
        {
            updateView();
            try {
                Thread.sleep(p_timeMilisecs);
            } catch (InterruptedException ex) {
                s_log.log(Level.SEVERE, null, ex);
            }
        }
    } //}}}
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */

