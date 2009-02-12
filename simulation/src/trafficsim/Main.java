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


/**
 *
 * @author Adam Rutkowski
 */
public class Main //{{{
{

    /**
     * @param args the command line arguments
     *  Single routine for running both client side and server side logic as 
     *  temporary walkaround.
     */
    public static void main(String[] args) //{{{
    {
        // Create simple model
        Model m = new Model();
        
        int cross1 = m.addCross(10,10);
        int cross2 = m.addCross(10,200);
        int lane1 = m.addLane(cross1, cross2, 50, 200);
        int lane2 = m.addLane(cross2, cross1, 50, 200);
        m.addParking(m.getLanes().get(lane1), m.getLanes().get(lane2));
        
        ClientViewClientSide clientSideView   = new ClientViewClientSide();
        ClientController1    clientController = 
                new ClientController1(m, clientSideView);
        ClientViewServerSide v = new ClientViewServerSide(clientSideView, m);
        clientSideView.setController(clientController);
        clientSideView.setServerSideView(v);
        
        @SuppressWarnings("unused")
        Client c = new Client(m, clientSideView, v);
   
        // Create time controller
        TimeController tc = TimeController.getTimeController(m);
        Thread timeControlThread = new Thread(tc);
        timeControlThread.start();
    }//}}}

}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
