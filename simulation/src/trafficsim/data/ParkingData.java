/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;

import trafficsim.*;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class ParkingData implements Serializable {
    public LinkedList<Integer>  carsOnParking;
    public LinkedList<Integer>  carsLeavingParking;
    public int              id;

    public ParkingData(Parking p) {
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
