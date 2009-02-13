/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;

/**
 *
 * @author Adam Rutkowski
 */
public class ClientViewClientSide { //{{{
    // Variables {{{
    
    public ClientViewData       data;
    
    private IController         p_controller;
    
    // TODO: this field should be removed and it's methods invocation replaced
    // by interprocess communication
    private ClientViewServerSide p_serverSideView;
    
    //}}}    
    
    // TODO: when communication implemented, this should be removed as well.
    public void setServerSideView(ClientViewServerSide serverSideView)
    {
        p_serverSideView = serverSideView;
    }
    
    public void setController(IController controller)
    {
        p_controller = controller;
    }    

    public void addObservedCar(Car car)
    {
        
    }
    
    // TODO: eventually this class should be made private when interprocess
    // communication is implemented. It should be invoked by communication 
    // routine upon receiving data from server.
    public void viewChanged(ClientViewData data)
    { //{{{
        this.data = data;
        p_controller.viewChanged();
    } //}}}
} //}}}
