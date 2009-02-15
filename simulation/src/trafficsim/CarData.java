/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;
import java.util.LinkedList;
import trafficsim.Car;
import trafficsim.Lane;
import trafficsim.Position;

/**
 *
 * @author Adam Rutkowski
 */
public class CarData {

    public int          id;
    public int          positionLane;
    public Position.e_info positionInfo;
    public int          positionCoord;
    public float        speed;
    public float        acceleration; 
    public float        maxSpeed;
    public float        maxAcceleration;
    public boolean      collided;
    public int          parkingId;
    public LinkedList<Integer> plannedRoute;
    
    public CarData(Car car)
    {
        this.id              = car.getId();
        this.collided        = car.isCollided();
        this.acceleration    = car.getAcceleration();
        this.maxAcceleration = car.getMaxAcceleration();
        this.maxSpeed        = car.getMaxSpeed();
        if (car.getCurrentParking() == null)
            this.parkingId = -1;
        else this.parkingId = car.getCurrentParking().getId();
        if (car.getPosition().getCoord() == null)
        {
            this.positionCoord = -1;
        }
        else
        {
            this.positionCoord   = car.getPosition().getCoord();
        }
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
