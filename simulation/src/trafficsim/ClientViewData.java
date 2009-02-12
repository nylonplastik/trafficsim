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
   
    private LinkedList<Car>                    observedCars;
    
    /**
     * List of all lanes close enough to controlled cars.
     */
    private Hashtable<Integer, LanesCross>      crosses;
    
    /***
     * List of all lanes close enough to controlled cars.
     */
    private LinkedList<Lane>                    lanes;
    
    private LinkedList<Car>                     cars;     

    /**
     * List of lane crosses which are on the border of this client view.
     * (their nejghbour lanes crosses' lists are empty not becouse there
     *   are no adjecent lanes crosses, but becouse the client view is not
     *   wide enough to contain them).
     */
    private LinkedList<LanesCross>             borderCrosses;
    
    //}}}
    
    public ClientViewData() //{{{
    {
        setObservedCars(new LinkedList<Car>());
        setBorderCrosses(new LinkedList<LanesCross>());
        setCars(new LinkedList<Car>());
        setLanes(new LinkedList<Lane>());
        setCrosses(new Hashtable<Integer, LanesCross>());
    } //}}}

	public void setObservedCars(LinkedList<Car> observedCars) {
		this.observedCars = observedCars;
	}

	public LinkedList<Car> getObservedCars() {
		return observedCars;
	}

	public void setCrosses(Hashtable<Integer, LanesCross> crosses) {
		this.crosses = crosses;
	}

	public Hashtable<Integer, LanesCross> getCrosses() {
		return crosses;
	}

	public void setLanes(LinkedList<Lane> lanes) {
		this.lanes = lanes;
	}

	public LinkedList<Lane> getLanes() {
		return lanes;
	}

	public void setCars(LinkedList<Car> cars) {
		this.cars = cars;
	}

	public LinkedList<Car> getCars() {
		return cars;
	}

	public void setBorderCrosses(LinkedList<LanesCross> borderCrosses) {
		this.borderCrosses = borderCrosses;
	}

	public LinkedList<LanesCross> getBorderCrosses() {
		return borderCrosses;
	}
} //}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */

