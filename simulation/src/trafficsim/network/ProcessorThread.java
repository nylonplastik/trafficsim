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

import java.util.concurrent.LinkedBlockingQueue;

public abstract class ProcessorThread<T> implements Runnable
{

	private static final int POLLING_TIME_MS = 100;

	private boolean processing = false;
	private LinkedBlockingQueue<T> events = new LinkedBlockingQueue<T>();
		
	public void addEvent(final T event)
	{
		events.add(event);
	}

	public abstract void processEvent(final T event);

	@Override
	public void run() {
		try
		{
			processing = true;
			while(processing)
			{
				T event = events.poll();
				while(event!=null)
				{
					processEvent(event);
					event = events.poll();
				}
				Thread.sleep(POLLING_TIME_MS);
			}
		} catch(InterruptedException e)
		{	
		}
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public boolean isProcessing() {
		return processing;
	}
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
