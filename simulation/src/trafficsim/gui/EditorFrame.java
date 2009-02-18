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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import trafficsim.LanesCross;
import trafficsim.Model;

@SuppressWarnings("serial")
public class EditorFrame extends JFrame
    implements MouseListener, MouseMotionListener
{

    private Model model = null;
    private SimulationComponent sim_component = null;
    private JPanel editor_panel = null;
    
    private LanesCross selected_cross = null;
    private LanesCross moving_cross = null;
    
    protected void initFrame()
    {
        setLayout(new BorderLayout());
        sim_component = new SimulationComponent(model);
        sim_component.addMouseListener(this);
        sim_component.addMouseMotionListener(this);
        editor_panel = new JPanel(true);
        editor_panel.add(sim_component,BorderLayout.CENTER);
        add(editor_panel,BorderLayout.CENTER);
        setPreferredSize(new Dimension(800,600));
        setResizable(true);
    }

    public EditorFrame() throws HeadlessException {
        super();
        initFrame();
    }

    public EditorFrame(GraphicsConfiguration gc) {
        super(gc);
        initFrame();
    }

    public EditorFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        initFrame();
    }

    public EditorFrame(String title) throws HeadlessException {
        super(title);
        initFrame();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
        sim_component.setModel(model);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        LanesCross last_selected_cross = selected_cross;
        selected_cross = null;
        for(LanesCross lc : sim_component.getModel().getLanesCrosses().values())
        {
            if ((-5<=lc.getX()-e.getX())&&(lc.getX()-e.getX()<=5)&&
                (-5<=lc.getY()-e.getY())&&(lc.getY()-e.getY()<=5))
            {
                selected_cross = lc;
                break;
            }        
        }
        if (selected_cross==null)
            sim_component.getModel().addCross(e.getX(), e.getY());
        else
        {
            if ((last_selected_cross != null)&&
                (last_selected_cross != selected_cross))
            {
                sim_component.getModel().addLane(last_selected_cross.getId(), selected_cross.getId(), 70, 700);
            }
        }
        e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        moving_cross = null;
        for(LanesCross lc : sim_component.getModel().getLanesCrosses().values())
        {
            if ((-5<=lc.getX()-e.getX())&&(lc.getX()-e.getX()<=5)&&
                (-5<=lc.getY()-e.getY())&&(lc.getY()-e.getY()<=5))
            {
                moving_cross = lc;
                break;
            }        
        }
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        moving_cross = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub        
        LanesCross lc = moving_cross;
        if (lc!=null)
        {
            lc.setX(e.getX());
            lc.setY(e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
