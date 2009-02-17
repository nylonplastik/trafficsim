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

import java.net.Socket;

public class ServerInfo implements Cloneable
{
    
    public enum ServerState
    {
        CONNECTED,
        WAIT_FOR_CLIENT,
        WAITS_FOR_UPDATE,
        DISCONNECT
    };

    private ServerState serverState = ServerState.CONNECTED;
    private Socket socket = null;
    private long lastUpdate = 0;
    
    public ServerInfo(Socket socket)
    {
        this.setSocket(socket);
        this.setLastUpdate(System.nanoTime());
    }

    public ServerInfo(Socket socket,ServerState state)
    {
        this.setSocket(socket);
        this.setServerState(state);
        this.setLastUpdate(System.nanoTime());
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setServerState(ServerState clientState) {
        this.serverState = clientState;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
};

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */