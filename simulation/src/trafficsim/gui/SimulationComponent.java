/*
    TrafficSim is simulation of road traffic
    Copyright (C) 2009  Mariusz Ceier, Adam Rutkowski

    This file is part of TrafficSim

    TrafficSim is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TrafficSim is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TrafficSim.  If not, see <http://www.gnu.org/licenses/>.
*/

package trafficsim.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import trafficsim.Car;
import trafficsim.Lane;
import trafficsim.LanesCross;
import trafficsim.Model;
import trafficsim.Position;

@SuppressWarnings("serial")
public class SimulationComponent extends JComponent implements Observer {

    private Model model = null;
    
    protected void initCanvas(Model m)
    {
        model = m;
        model.addObserver(this);
        setBackground(Color.WHITE);
        setOpaque(false);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(640,480));
    }

    public SimulationComponent(Model m) {
        initCanvas(m);
    }

    /*
    @Override
    public void paint(Graphics g)
    {
        doUpdate(g);
    }
    */
    
    public void doUpdate(Graphics g)
    {
        Model m = model;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        //g.clearRect(0, 0, this.getWidth(), this.getHeight());
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
        this.invalidate();
        this.repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
    	this.invalidate();
        this.repaint();
    }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		doUpdate(g);
	}
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
