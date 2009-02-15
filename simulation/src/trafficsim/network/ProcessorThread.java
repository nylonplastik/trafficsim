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

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessorThread implements PacketsProcessor, Runnable
{

	private static final int POLLING_TIME = 100;

	private Hashtable<Integer, PacketsProcessor> processors = null;
	private LinkedBlockingQueue<Packet> packets = new LinkedBlockingQueue<Packet>();

	private boolean processing = false;
	
	@Override
	public void processPacket(Packet packet) {
		if ((processors!=null)&&(processors.containsKey(packet.getTypeId())))
		{
			PacketsProcessor processor = processors.get(packet.getTypeId());
			if (processor!=null)
				processor.processPacket(packet);
			else
				processors.remove(packet.getTypeId());
		}
	}
	
	public void registerProcessor(
			final Integer packetTypeId,
			final PacketsProcessor processor)
	{
		if (processors==null)
			processors = new Hashtable<Integer, PacketsProcessor>();
		processors.put(packetTypeId, processor);
	}
	
	public void unregisterProcessor(final Integer packetTypeId)
	{
		if (processors!=null)
		{
			if (processors.containsKey(packetTypeId))
				processors.remove(packetTypeId);
			if (processors.isEmpty())
				processors = null;
		}
	}

	public void addPacket(final Packet packet)
	{
		packets.add(packet);
	}

	@Override
	public void run() {
		try
		{
			processing = true;
			while(processing)
			{
				Packet p = packets.poll();
				while(p!=null)
				{
					processPacket(p);
					p = packets.poll();
				}
				Thread.sleep(POLLING_TIME);
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
