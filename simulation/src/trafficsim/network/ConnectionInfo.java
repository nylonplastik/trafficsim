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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ConnectionInfo implements Cloneable
{
    
    @SuppressWarnings("unused")
    private static Logger s_log = Logger.getLogger(ConnectionInfo.class.toString());
    
    private Socket socket = null;
    private long lastUpdate = 0;
    private InputStream inputStream = null;
	private OutputStream outputStream = null;
    private int clientId;
	private Object data = null;

	private ConnectionInfo()
	{	
	}
	
    public ConnectionInfo(Socket socket) throws IOException
    {
        this.setSocket(socket);
        this.setLastUpdate(System.nanoTime());
    }

    @Override
    public Object clone()
    {
		try {
	    	ConnectionInfo copy = new ConnectionInfo();
	    	copy.setSocket(getSocket());
	    	copy.setLastUpdate(getLastUpdate());
            copy.setClientId(getClientId());
            copy.setData(getData());
	    	return copy;
		} catch (IOException e) {
			return null;
		}
    }

    public void setSocket(Socket socket) throws IOException {
        if (socket==null)
        {
        	inputStream = null;
        	outputStream = null;
        }
        this.socket = socket;
        if (this.socket!=null)
        {
        	inputStream = this.socket.getInputStream();
        	outputStream = this.socket.getOutputStream();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public boolean isDataAvailable() throws IOException
    {
    	return (inputStream.available()>0);
    }

    public synchronized void writeObject(Object object) throws IOException
    {
    	if (outputStream!=null)
    	{
    		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
    		oos.writeObject(object);
    	}
    }
    
    public synchronized Object readObject() throws IOException, ClassNotFoundException
    {
    	if (inputStream == null)
    		 return null;
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		return ois.readObject();
    }

    public synchronized int getClientId() {
        return clientId;
    }

    public synchronized void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public synchronized void setData(Object data)
    {
        this.data = data;
    }

    public synchronized Object getData()
    {
        return data;
    }

};

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
