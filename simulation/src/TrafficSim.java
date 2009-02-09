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


import gui.*;
import javax.swing.*;

public class TrafficSim implements Runnable
{

    /**
     * Simulation main
     *
     * @param args command line arguments, currently not parsed
     */
    public static void main(String []args) throws java.lang.reflect.InvocationTargetException
    {
        try
        {
            SwingUtilities.invokeAndWait(new TrafficSim());
        } 
        catch(InterruptedException e)
        {
            // Just ignore it
        }
    }

    /**
     * Shows main frame 
     *
     */
    public void run()
    {
        TrafficSimFrame frame = new TrafficSimFrame("Simulation of road traffic");
        frame.pack();
        frame.setVisible(true);
    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
