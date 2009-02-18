/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;
/**
 *
 * @author Adam Rutkowski
 */
public interface ICarController extends IController{ 
    public void registered();
    public void newCarCallback(int newCarId);
}
