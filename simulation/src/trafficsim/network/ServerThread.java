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

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread implements Runnable {

    private static Logger s_log = Logger.getLogger(ServerThread.class.toString());
    
    private InetSocketAddress address = null;
    private int backlog = 0;
    private ProcessorThread<Socket> clientsProcessor = null;
    private boolean running = false;
    
    public ServerThread(InetSocketAddress address)
    {
        setAddress(address);
    }
    
    public ServerThread(InetSocketAddress address, int backlog)
    {
        setAddress(address);
        setBacklog(backlog);
    }

    @Override
    public void run() {
        InetSocketAddress address = getAddress();
        int backlog = getBacklog();
        try
        {
            ServerSocket socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.setSoTimeout(1000);
            socket.bind(address,backlog);
            setRunning(true);
            while(isRunning())
            {
                try
                {
                    Socket client = socket.accept();
                    ProcessorThread<Socket> cp = getClientsProcessor();
                    if (cp!=null)
                        cp.addEvent(client);
                    else
                        client.close();
                } catch(SocketTimeoutException e)
                {                    
                }
            }
            socket.close();
        } catch(SocketException e)
        {
            s_log.log(Level.SEVERE,"Server Socket Exception", e);
        } catch (IOException e)
        {
            s_log.log(Level.SEVERE,"Server IO Exception", e);
        }
    }

    public void setAddress(final InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getBacklog() {
        return backlog;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setClientsProcessor(ProcessorThread<Socket> clientsProcessor) {
        this.clientsProcessor = clientsProcessor;
    }

    public synchronized ProcessorThread<Socket> getClientsProcessor() {
        return clientsProcessor;
    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
