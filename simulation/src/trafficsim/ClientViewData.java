/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;

import java.util.LinkedList;
import java.util.Hashtable;

/**
 *
 * @author Adam Rutkowski
 */
public class ClientViewData { //{{{
    
    // Variables {{{    
   
    public LinkedList<Car>                    observedCars;
    
    /**
     * List of all lanes close enough to controlled cars.
     */
    public Hashtable<Integer, LanesCross>      crosses;
    
    /***
     * List of all lanes close enough to controlled cars.
     */
    public LinkedList<Lane>                    lanes;
    
    public LinkedList<Car>                     cars;     

    /**
     * List of lane crosses which are on the border of this client view.
     * (their nejghbour lanes crosses' lists are empty not becouse there
     *   are no adjecent lanes crosses, but becouse the client view is not
     *   wide enough to contain them).
     */
    public LinkedList<LanesCross>             borderCrosses;
    
    //}}}
    
    public ClientViewData() //{{{
    {
        observedCars  = new LinkedList<Car>();
        borderCrosses = new LinkedList<LanesCross>();
        cars          = new LinkedList<Car>();
        lanes         = new LinkedList<Lane>();
        crosses       = new Hashtable<Integer, LanesCross>();
    } //}}}
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */

