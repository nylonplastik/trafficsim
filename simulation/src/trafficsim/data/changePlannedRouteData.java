/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;
import java.util.LinkedList;
import trafficsim.network.Packet;

/**
 *
 * @author Adam Rutkowski
 */
public class changePlannedRouteData extends Packet {
    
    private static int count = 0;

    private synchronized static int getCount() {
        return count++;
    }        
    
    public changePlannedRouteData(LinkedList<Integer> route, int id)
    {
        this.plannedRoute = route;
        this.carId        = id;
        this.id = getCount();
    }
    
    public LinkedList<Integer> plannedRoute;
    public int carId;
    public int id;
}
