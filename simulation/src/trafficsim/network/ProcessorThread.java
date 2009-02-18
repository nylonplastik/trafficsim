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

import java.util.concurrent.LinkedBlockingDeque;

public abstract class ProcessorThread<T extends Cloneable> implements Runnable
{

    private static final int POLLING_TIME_MS = 100;

    private boolean processing = false;
    private LinkedBlockingDeque<T> events = null;
    
    public ProcessorThread()
    {
        events = new LinkedBlockingDeque<T>();
    }
    
    public ProcessorThread(ProcessorThread<T> p)
    {
        events = p.getEvents();
    }
    
    public synchronized void addEvent(T event) throws InterruptedException
    {
    	events.put(event);
    }

    public abstract void processEvent(final T event);

    @Override
    public void run() {
        try
        {
            setProcessing(true);
            LinkedBlockingDeque<T> events = getEvents();
            while(isProcessing())
            {
                //System.out.println("Events size:"+events.size());
                T event = events.poll();
                if(event!=null)
                    processEvent(event);
                Thread.sleep(POLLING_TIME_MS);
            }
        } catch(InterruptedException e)
        {
        }
    }

    public synchronized void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public synchronized boolean isProcessing() {
        return processing;
    }
    
    public synchronized LinkedBlockingDeque<T> getEvents() {
        return events;
    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
