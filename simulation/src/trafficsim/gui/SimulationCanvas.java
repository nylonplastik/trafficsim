package trafficsim.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import trafficsim.Car;
import trafficsim.Lane;
import trafficsim.LanesCross;
import trafficsim.Model;
import trafficsim.Position;

@SuppressWarnings("serial")
public class SimulationCanvas extends Canvas implements Observer {

    private Model model = null;
    
    protected void initCanvas(Model m)
    {
        model = m;
        model.addObserver(this);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(640,480));
    }

    public SimulationCanvas(Model m) {
        initCanvas(m);
    }

    public SimulationCanvas(Model m, GraphicsConfiguration config) {
        super(config);
        initCanvas(m);
    }

    @Override
    public void paint(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        Model m = model;
        if (m == null) return;
        Collection<Lane> lanes = m.getLanes();
        g.setColor(Color.BLACK);
        for(Lane l : lanes)
        {
            g.drawLine(l.getLaneSource().getX(),
                       l.getLaneSource().getY(), 
                       l.getLaneDestination().getX(),
                       l.getLaneDestination().getY());
        }
        /*
        for(Parking p : model.getParkings())
        {
            g.drawLine(p.getLaneSource().X_coordinate,
                       p.getLaneSource().Y_coordinate, 
                       p.getLaneDestination().X_coordinate,
                       p.getLaneDestination().Y_coordinate);
        }
        */
        Collection<LanesCross> crosses = m.getLanesCrosses().values();
        g.setColor(Color.RED);
        for(LanesCross l : crosses)
        {
            g.fillOval(l.getX()-5, l.getY()-5, 10, 10);
        }
        
        g.setColor(Color.GREEN);
        for(Car c : m.getCars())
        {
            Position p = c.getPosition();
            int x = p.getLane().getLaneSource().getX();
            int y = p.getLane().getLaneSource().getY();
            x += c.getPosition().getCoord()*(p.getLane().getLaneDestination().getX() - p.getLane().getLaneSource().getX())/c.getPosition().getLane().getLength();
            y += c.getPosition().getCoord()*(p.getLane().getLaneDestination().getY() - p.getLane().getLaneSource().getY())/c.getPosition().getLane().getLength();
            g.fillRect(x-5,y-5,10,10);
        }
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        Model m = this.model;
        if (m!=null)
            m.deleteObserver(this);
        this.model = model;
        if (this.model!=null)
            this.model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
