/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;

import trafficsim.*;
import java.util.LinkedList;
import trafficsim.Car;
import trafficsim.Parking;
import trafficsim.network.Packet;
import trafficsim.network.PacketTypes;

/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class ParkingData extends Packet {
    public LinkedList<Integer>  carsOnParking;
    public LinkedList<Integer>  carsLeavingParking;
    public int              id;

    public ParkingData(Parking p) {
        super(PacketTypes.PARKING_DATA_TYPEID);
        carsOnParking      = new LinkedList<Integer>();
        carsLeavingParking = new LinkedList<Integer>();
                
        for (Car c : p.getCarsOnParking())
        {
           carsOnParking.add(c.getId());
        }
        
        for (Car c: p.getCarsLeavingParking())
        {
            carsLeavingParking.add(c.getId());
        }
            
    }
}
