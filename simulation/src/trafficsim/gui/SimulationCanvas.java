package trafficsim.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.util.Collection;

import trafficsim.Car;
import trafficsim.Lane;
import trafficsim.LanesCross;
import trafficsim.Model;
import trafficsim.Position;

@SuppressWarnings("serial")
public class SimulationCanvas extends Canvas {

	private Model model = null;
	
	protected void initCanvas(Model m)
	{
		model = m;
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
		if (model == null) return;
		Collection<Lane> lanes = model.getLanes();
		for(Lane l : lanes)
		{
			g.drawLine(l.getLaneSource().X_coordinate,
					   l.getLaneSource().Y_coordinate, 
					   l.getLaneDestination().X_coordinate,
					   l.getLaneDestination().Y_coordinate);
		}
		Collection<LanesCross> crosses = model.getLanesCrosses().values();
		g.setColor(Color.RED);
		for(LanesCross l : crosses)
		{
			g.fillOval(l.X_coordinate-5, l.Y_coordinate-5, 10, 10);
		}
		
		g.setColor(Color.GREEN);
		for(Car c : model.getCars())
		{
			Position p = c.getPosition();
			int x = p.lane.getLaneSource().X_coordinate;
			int y = p.lane.getLaneSource().Y_coordinate;
			x += c.getPosition().coord*(p.lane.getLaneDestination().X_coordinate - p.lane.getLaneSource().X_coordinate);
			y += c.getPosition().coord*(p.lane.getLaneDestination().Y_coordinate - p.lane.getLaneSource().Y_coordinate);
			g.fillRect(x-5,y-5,10,10);
		}
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
