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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import trafficsim.Model;
import trafficsim.network.ClientInfo.ClientState;

public class ClientsProcessor extends ProcessorThread<ClientInfo> implements Observer
{
    private static Logger s_log = Logger.getLogger(ClientsProcessor.class.toString());

    private long currentUpdate = 0;
    private Model model = null;
    
    public ClientsProcessor(Model model)
    {
        currentUpdate = System.nanoTime();
        setModel(model);
    }

    public void sendUpdate(ClientInfo client)
    {
        // TODO: really send update
        client.setLastUpdate(currentUpdate);
    }

    @Override
    public void processEvent(ClientInfo client) {
        switch(client.getClientState())
        {
            case NEW_CLIENT:
                System.out.println("New client");
                client.setClientState(ClientState.WAIT_FOR_CLIENT);
                break;
            case WAIT_FOR_CLIENT:
                //System.out.println("Waiting for client");
                try {
                    InputStream is = client.getSocket().getInputStream();
                    int avail = is.available();
                    if (avail>0)
                    {
                        System.out.println("New data for client");
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader bis = new BufferedReader(isr);
                        System.out.println(bis.readLine());
                        client.setClientState(ClientState.WAITS_FOR_UPDATE);
                    }
                } catch (IOException e) {
                    s_log.log(Level.SEVERE,"Waiting for client IO exception",e);
                }
                break;
            case WAITS_FOR_UPDATE:
                //System.out.println("Client waits for update");
                if (currentUpdate>client.getLastUpdate())
                    sendUpdate(client);
                break;
            case DISCONNECT:
                System.out.println("Disconnecting client");
                try {
                    client.getSocket().close();
                } catch (IOException e) {
                    s_log.log(Level.WARNING,"Exception while closing socket",e);
                }
                break;
            default:
                break;
        }
        if ((client.getSocket().isConnected())&&
            (client.getClientState()!=ClientState.DISCONNECT))
        {
            //System.out.println("Adding client");
            ClientInfo cinfo = new ClientInfo(client.getSocket());
            cinfo.setClientState(client.getClientState());
            cinfo.setLastUpdate(client.getLastUpdate());
            addEvent(cinfo);
            //System.out.println("Client added");
        }
    }
    
    @Override
    public synchronized void update(Observable o, Object arg) {
        currentUpdate = System.nanoTime();
    }
    
    public void setModel(Model model) {
        if (this.model!=null)
            this.model.deleteObserver(this);
        this.model = model;
        if (this.model!=null)
            this.model.addObserver(this);
    }
    
    public Model getModel() {
        return model;
    }
    
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
