/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;
import trafficsim.network.Packet;

/**
 *
 * @author Adam Rutkowski
 */
public class startMovingData extends Packet{
    
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
