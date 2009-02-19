/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Adam Rutkowski
 */
@SuppressWarnings("serial")
public class changePlannedRouteData implements Serializable {
    
    public changePlannedRouteData(LinkedList<Integer> route, int id)
    {
        this.plannedRoute = route;
        this.carId        = id;
    }
    
    public LinkedList<Integer> plannedRoute;
    public int carId;
}
