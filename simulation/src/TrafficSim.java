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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.gui.*;
import trafficsim.network.ClientThread;
import trafficsim.network.ClientsProcessor;
import trafficsim.network.ServerProcessor;
import trafficsim.network.ServerThread;
import trafficsim.*;
import javax.swing.*;

public class TrafficSim implements Runnable
{

    private static Logger s_log = Logger.getLogger(TrafficSim.class.toString());
    private ServerThread st = null;
    private ClientsProcessor cp = null;
    private ClientThread ct = null;
    private ServerProcessor sp = null;
    
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

    private Model serverModel = null;
    private Model clientModel = null;
    
    /**
     * Shows main frame 
     *
     */
    public void run()
    {
        // Create simple model
        serverModel = null;
        //try {
        //    m=Model.loadModel("model.xml");
        //} catch (FileNotFoundException e1) {
        //}
        //if (m==null)
        {
                    serverModel=new Model();


                    int cross1 = serverModel.addCross(10,10);
                    int cross2 = serverModel.addCross(200,200);
                    int cross3 = serverModel.addCross(200, 300);
                    Lane lane1 = serverModel.addLane(cross1, cross2, 20, 500);
                    Lane lane2 = serverModel.addLane(cross2, cross1, 20, 500);
                    Lane lane3 = serverModel.addLane(cross2, cross3, 20, 500);
                    Lane lane4 = serverModel.addLane(cross3, cross2, 20, 500);
                    if (lane1 != null)
                        lane1.setDefaultNextLane(lane3);
                    if (lane2 != null)
                        lane4.setDefaultNextLane(lane2);


                    serverModel.addParking(lane1, lane2);
                    serverModel.addParking(lane3, lane4);
                    
                    clientModel=new Model();

                    cross1 = clientModel.addCross(10,10, 0);
                    cross2 = clientModel.addCross(200,200, 1);
                    cross3 = clientModel.addCross(200, 300, 2);
                    lane1 = clientModel.addLane(cross1, cross2, 20, 500, 0);
                    lane2 = clientModel.addLane(cross2, cross1, 20, 500, 1);
                    lane3 = clientModel.addLane(cross2, cross3, 20, 500, 2);
                    lane4 = clientModel.addLane(cross3, cross2, 20, 500, 3);
                    if (lane1 != null)
                        lane1.setDefaultNextLane(lane3);
                    if (lane2 != null)
                        lane4.setDefaultNextLane(lane2);


                    clientModel.addParking(lane1, lane2, 0);
                    clientModel.addParking(lane3, lane4, 1);                    
        }
        
        // Server
        try {
            cp = new ClientsProcessor(serverModel);
            st = new ServerThread(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),23456));
            st.setClientsProcessor(cp);
        } catch (UnknownHostException e2) {
            s_log.log(Level.SEVERE,"wtf?",e2);
            System.exit(1);
        }
        
        new Thread(cp).start();
        new Thread(st).start();
        
        sp = new ServerProcessor(clientModel);
        try {
            ct = new ClientThread(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),23456));
            ct.setServerProcessor(sp);
        } catch (UnknownHostException e2) {
            s_log.log(Level.SEVERE,"wtf?",e2);
            System.exit(1);
        }

        new Thread(sp).start();
        new Thread(ct).start();
        
        ClientViewClientSide clientSideView   = new ClientViewClientSide(clientModel);
        ClientController1    clientController = 
                new ClientController1(clientModel, clientSideView);
        ClientViewServerSide v = new ClientViewServerSide(clientSideView, serverModel);
        clientSideView.setController(clientController);
        clientSideView.setServerSideView(v);

        @SuppressWarnings("unused")
        Client c = new Client(serverModel, clientSideView, v);
        
        /* TODO TO BE REMOVED */
        clientController.setServerModel(serverModel);
        clientController.start();
        
   
        // Create time controller
        TimeController tc = TimeController.getTimeController(serverModel);
        final Thread timeControlThread = new Thread(tc);
        timeControlThread.start();
        
        MainFrame frame = new MainFrame("Simulation of road traffic");
        frame.setModel(serverModel);
        frame.pack();
        frame.addWindowListener(new WindowListener(){

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowClosed(WindowEvent e) {
                st.setRunning(false);
                cp.setProcessing(false);
                timeControlThread.interrupt();
                try {
                    serverModel.saveModel("model.xml");
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
