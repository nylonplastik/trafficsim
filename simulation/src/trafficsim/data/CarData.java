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
public class CarData implements Serializable {

    public int          id;
    public int          positionLane;
    public Position.e_info positionInfo;
    public float        positionCoord;
    public float        speed;
    public float        acceleration; 
    public float        maxSpeed;
    public float        maxAcceleration;
    public boolean      collided;
    public int          parkingId;
    public LinkedList<Integer> plannedRoute;
    
    public CarData(Car car)
    {
        synchronized(car)
        {
            this.id              = car.getId();
            this.collided        = car.isCollided();
            this.acceleration    = car.getAcceleration();
            this.maxAcceleration = car.getMaxAcceleration();
            this.maxSpeed        = car.getMaxSpeed();
            if (car.getCurrentParking() == null)
                this.parkingId = -1;
            else this.parkingId = car.getCurrentParking().getId();
            this.positionCoord   = car.getPosition().getCoord();

            this.positionInfo    = car.getPosition().getInfo();

            if (car.getPosition().getLane() == null)
            {
                this.positionLane    = -1;
            }
            else
            {
                this.positionLane    = car.getPosition().getLane().getId();
            }        

            this.speed           = car.getSpeed();
            plannedRoute         = new LinkedList<Integer>();
            for(Lane l : car.getPlannedRoute())
            {
                plannedRoute.add(l.getId());
            }
        }
    }
}
