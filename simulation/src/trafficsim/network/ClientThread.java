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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread implements Runnable
{
    private static Logger s_log = Logger.getLogger(ClientThread.class.toString());
    
    private Socket clientSocket = null;
    private InetSocketAddress address = null;
    private ProcessorThread<ConnectionInfo> connectionProcessor = null;
    private boolean running = false;
    
    public ClientThread(InetSocketAddress address)
    {
        setAddress(address);
    }
    
    @Override
    public void run() {
        InetSocketAddress address = getAddress();
        try
        {
            clientSocket = new Socket();
            System.out.println("Connecting..."+address.getHostName()+":"+address.getPort());
            clientSocket.connect(address);
            if (clientSocket.isConnected())
            {
                System.out.println("Connected...");
                ProcessorThread<ConnectionInfo> sp = getConnectionProcessor();
                if (sp!=null)
                {
                    sp.addEvent(new ConnectionInfo(clientSocket));
                }
            } else
                System.out.println("Not connected...");
        } catch(SocketException e)
        {
            s_log.log(Level.SEVERE,"Client Socket Exception", e);
        } catch (IOException e)
        {
            s_log.log(Level.SEVERE,"Client IO Exception", e);
        } catch (InterruptedException e) {
            s_log.log(Level.SEVERE,"Client interrupted", e);
		}
    }

    public void setAddress(final InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setConnectionProcessor(ProcessorThread<ConnectionInfo> connectionProcessor) {
        this.connectionProcessor = connectionProcessor;
    }

    public synchronized ProcessorThread<ConnectionInfo> getConnectionProcessor() {
        return connectionProcessor;
    }
    
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
