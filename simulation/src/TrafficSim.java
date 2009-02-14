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


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;

import trafficsim.gui.*;
import trafficsim.*;
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

    private Model m = null;
    
    /**
     * Shows main frame 
     *
     */
    public void run()
    {
                // Create simple model
        m = null;
        try {
			m=Model.loadModel("model.xml");
		} catch (FileNotFoundException e1) {
		}
		if (m==null)
		{
                    m=new Model();

                    int cross1 = m.addCross(10,10);
                    int cross2 = m.addCross(200,200);
                    int cross3 = m.addCross(200, 300);
                    Lane lane1 = m.addLane(cross1, cross2, 50, 2000);
                    Lane lane2 = m.addLane(cross2, cross1, 50, 2000);
                    Lane lane3 = m.addLane(cross2, cross3, 50, 2000);
                    Lane lane4 = m.addLane(cross3, cross2, 50, 2000);
                    if (lane1 != null)
                        lane1.setDefaultNextLane(lane3);
                    if (lane2 != null)
                        lane4.setDefaultNextLane(lane2);


                    m.addParking(lane1, lane2);
                    m.addParking(lane3, lane4);
		}
		
        ClientViewClientSide clientSideView   = new ClientViewClientSide();
        ClientController1    clientController = 
                new ClientController1(m, clientSideView);
        ClientViewServerSide v = new ClientViewServerSide(clientSideView, m);
        clientSideView.setController(clientController);
        clientSideView.setServerSideView(v);
        
        clientController.start();
        
        @SuppressWarnings("unused")
        Client c = new Client(m, clientSideView, v);
   
        // Create time controller
        TimeController tc = TimeController.getTimeController(m);
        final Thread timeControlThread = new Thread(tc);
        timeControlThread.start();
        
        MainFrame frame = new MainFrame("Simulation of road traffic");
        frame.setModel(m);
        frame.pack();
        frame.addWindowListener(new WindowListener(){

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowClosed(WindowEvent e) {
                timeControlThread.interrupt();
                try {
					m.saveModel("model.xml");
				} catch (FileNotFoundException e1) {
				}
                System.exit(0);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        frame.setVisible(true);      
    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
