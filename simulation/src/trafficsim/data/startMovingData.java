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
    
    public startMovingData(float acc, int id)
    {
        this.acceleration = acc;
        this.carId        = id;
    }
    
    public float acceleration;
    public int carId;
}
