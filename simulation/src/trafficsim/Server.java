/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trafficsim;

import trafficsim.data.ClientViewData;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import trafficsim.network.*;
import trafficsim.network.server.ClientsProcessor;


/**
 *
 * @author Adam Rutkowski
 */
public class Server {
    
    private static Logger s_log = Logger.getLogger(Server.class.toString());


    private ServerThread st = null;
    private trafficsim.network.server.ClientsProcessor cp = null;
    Model model = null;
    ConcurrentHashMap<Integer, ClientViewServerSide> clientsViews = 
                        new ConcurrentHashMap<Integer, ClientViewServerSide>();
    Thread timeControlThread;
    private static int clientsCount = 0;
    
    @SuppressWarnings("unused")
    private ClientViewServerSide serverSideView = null;
    
    public Server() 
    {
        this(23456);
    }
    
    public Server(int port)
    {
        model=new Model();
        
        int cross1 = model.addCross(250,50);
        int cross2 = model.addCross(375,300);
        int cross3 = model.addCross(125, 300);
        Lane lane1 = model.addLane(cross1, cross2, 20);
        Lane lane2 = model.addLane(cross2, cross3, 20);
        Lane lane3 = model.addLane(cross3, cross1, 20);
        if (lane1 != null)
            lane1.setDefaultNextLane(lane2);
        if (lane2 != null)
            lane2.setDefaultNextLane(lane3);
        if (lane3 != null)
            lane1.setDefaultNextLane(lane1);

        model.addParking(lane1, lane2);  
        serverSideView = new ClientViewServerSide(model);
        
        // Server
        try {
            cp = new ClientsProcessor(model, this);
            st = new ServerThread(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),port));
            st.setConnectionProcessor(cp);
        } catch (UnknownHostException e2) {
            s_log.log(Level.SEVERE,"wtf?",e2);
            System.exit(1);
        }
        
        new Thread(cp).start();
        new Thread(st).start();   
        this.start();
    }

    public void addObservedCar(int clientId, int newCarId) {
        clientsViews.get(clientId).addObservedCar(newCarId);
    }

    public void close() {
        st.setRunning(false);
        cp.setProcessing(false);
        if (timeControlThread!=null)
            timeControlThread.interrupt();
        try {
            model.saveModel("model.xml");
        } catch (FileNotFoundException e1) {
        }
    }

    public synchronized Model getModel() {
        return this.model;
    }

    public ClientViewData getViewData(int clientId) {
        return clientsViews.get(clientId).getData();
    }

    public Integer newClient() {
        int id = newClientId();
        ClientViewServerSide newView = new ClientViewServerSide(model);
        clientsViews.put(id, newView);
        return id;
    }
    
    public void start()
    {
        // Create time controller
        TimeController tc = TimeController.getTimeController(model);
        timeControlThread = new Thread(tc);
        timeControlThread.start();
    }
    
    private static int newClientId() {
        return clientsCount++;
    }

    public boolean wasViewUpdated(int clientId) {
        return clientsViews.get(clientId).hasChanged();
    }
    
}
