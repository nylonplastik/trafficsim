/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim.data;

import java.util.LinkedList;

/**
 *
 * @author Adam Rutkowski
 */
public class changePlannedRouteData {
    
    public changePlannedRouteData(LinkedList<Integer> route, int id)
    {
        this.plannedRoute = route;
        this.carId        = id;
    }
    
    LinkedList<Integer> plannedRoute;
    public int carId;
}
