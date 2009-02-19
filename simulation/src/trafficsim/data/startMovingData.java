/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;

import java.io.Serializable;

/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class startMovingData implements Serializable {
    
    private static int count = 0;

    private synchronized static int getCount() {
        return count++;
    }
    
    public startMovingData(float acc, int id)
    {
        this.acceleration = acc;
        this.carId        = id;
        this.id = getCount();
    }
    
    public float acceleration;
    public int carId;
    public int id;
}
