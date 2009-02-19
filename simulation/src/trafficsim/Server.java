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
        this(port,0);
    }

    public Server(int port, int nAdditionalProcessors)
    {
        model=new Model();
        
        int x1 = 20, x2 = 160, x3=164, x4=480, x5=484, x6=560;
        int y1 = 20, y2=130, y3=134, y4=320, y5=324, y6=450;
        
        int cross1 = model.addCross(x2,y1);
        int cross2 = model.addCross(x3,y1);
        int cross3 = model.addCross(x4, y1);
        int cross4 = model.addCross(x1, y2);    
        int cross5 = model.addCross(x2,y2);
        int cross6 = model.addCross(x3,y2);
        int cross7 = model.addCross(x4, y2);
        int cross8 = model.addCross(x5, y2);  
        int cross9 = model.addCross(x6,y2);
        int cross10 = model.addCross(x1,y3);
        int cross11 = model.addCross(x2, y3);
        int cross12 = model.addCross(x3, y3);  
        int cross13 = model.addCross(x4, y3);
        int cross14 = model.addCross(x5,y3);
        int cross15 = model.addCross(x6, y3);
        int cross16 = model.addCross(x1, y4);  
        int cross17 = model.addCross(x2,y4);
        int cross18 = model.addCross(x3,y4);
        int cross19 = model.addCross(x4, y4);
        int cross20 = model.addCross(x5, y4);  
        int cross21 = model.addCross(x6,y4);
        int cross22 = model.addCross(x1,y5);
        int cross23 = model.addCross(x2, y5);
        int cross24 = model.addCross(x3, y5);  
        int cross25 = model.addCross(x4,y5);
        int cross26 = model.addCross(x5,y5);
        int cross27 = model.addCross(x6, y5);
        int cross28 = model.addCross(x3, y6);  
        int cross29 = model.addCross(x4,y6);
        int cross30 = model.addCross(x5,y6);      
        
        int speedLimit = 20;
        
        Lane lane1 = model.addLane(cross3, cross2, speedLimit);
        Lane lane2 = model.addLane(cross5, cross4, speedLimit);
        Lane lane3 = model.addLane(cross6, cross5, speedLimit);
        Lane lane4 = model.addLane(cross7, cross6, speedLimit);        
        Lane lane5 = model.addLane(cross8, cross7, speedLimit);
        Lane lane6 = model.addLane(cross9, cross8, speedLimit);   
        Lane lane7 = model.addLane(cross10, cross11, speedLimit);
        Lane lane8 = model.addLane(cross11, cross12, speedLimit);
        Lane lane9 = model.addLane(cross12, cross13, speedLimit);
        Lane lane10 = model.addLane(cross13, cross14, speedLimit);        
        Lane lane11 = model.addLane(cross14, cross15, speedLimit);        
        Lane lane12 = model.addLane(cross17, cross16, speedLimit);        
        Lane lane13 = model.addLane(cross18, cross17, speedLimit);
        Lane lane14 = model.addLane(cross19, cross18, speedLimit);
        Lane lane15 = model.addLane(cross20, cross19, speedLimit);
        Lane lane16 = model.addLane(cross21, cross20, speedLimit);      
        Lane lane17 = model.addLane(cross22, cross23, speedLimit);
        Lane lane18 = model.addLane(cross23, cross24, speedLimit);
        Lane lane19 = model.addLane(cross24, cross25, speedLimit);
        Lane lane20 = model.addLane(cross25, cross26, speedLimit);        
        Lane lane21 = model.addLane(cross26, cross27, speedLimit);
        Lane lane22 = model.addLane(cross29, cross28, speedLimit);
        Lane lane23 = model.addLane(cross4, cross10, speedLimit);
        Lane lane24 = model.addLane(cross16, cross22, speedLimit);
        Lane lane25 = model.addLane(cross1, cross5, speedLimit);
        Lane lane26 = model.addLane(cross5, cross11, speedLimit);
        Lane lane27 = model.addLane(cross11, cross17, speedLimit);
        Lane lane28 = model.addLane(cross17, cross23, speedLimit);           
        Lane lane29 = model.addLane(cross6, cross2, speedLimit);
        Lane lane30 = model.addLane(cross12, cross6, speedLimit);
        Lane lane31 = model.addLane(cross18, cross12, speedLimit);
        Lane lane32 = model.addLane(cross24, cross18, speedLimit);
        Lane lane33 = model.addLane(cross28, cross24, speedLimit);        
        Lane lane34 = model.addLane(cross3, cross7, speedLimit);
        Lane lane35 = model.addLane(cross7, cross13, speedLimit);
        Lane lane36 = model.addLane(cross13, cross19, speedLimit);        
        Lane lane37 = model.addLane(cross19, cross25, speedLimit);
        Lane lane38 = model.addLane(cross25, cross29, speedLimit);        
        Lane lane39 = model.addLane(cross14, cross8, speedLimit);
        Lane lane40 = model.addLane(cross20, cross14, speedLimit);
        Lane lane41 = model.addLane(cross26, cross20, speedLimit);
        Lane lane42 = model.addLane(cross30, cross26, speedLimit);        
        Lane lane43 = model.addLane(cross15, cross9, speedLimit);
        Lane lane44 = model.addLane(cross27, cross21, speedLimit);
        
    
        
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
        for(int i=0;i<nAdditionalProcessors;++i)
            new Thread(new ClientsProcessor(cp)).start();
        new Thread(st).start();   
        this.start();
    }

    public synchronized void addObservedCar(int clientId, int newCarId) {
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

    public synchronized ClientViewData getViewData(int clientId) {
        return clientsViews.get(clientId).getData();
    }

    public synchronized Integer newClient() {
        int id = newClientId();
        ClientViewServerSide newView = new ClientViewServerSide(model);
        clientsViews.put(id, newView);
        return id;
    }
    
    public synchronized void start()
    {
        // Create time controller
        TimeController tc = TimeController.getTimeController(model);
        timeControlThread = new Thread(tc);
        timeControlThread.start();
    }
    
    private synchronized static int newClientId() {
        return clientsCount++;
    }

    public synchronized boolean wasViewUpdated(int clientId) {
        if (clientsViews.containsKey(clientId))
            return clientsViews.get(clientId).hasChanged();
        else return false;
    }
    
    public static void main(String []args)
    {
        new Server(5);
    }
    
}
