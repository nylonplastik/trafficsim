package trafficsim.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

import trafficsim.Model;

@SuppressWarnings("serial")
public class EditorFrame extends JFrame {

	private Model model = null;
	
	protected void initFrame()
	{
		model = new Model();
        model.addCross(0,10,10);
        model.addCross(1,20,20);
        model.addLane(0, 1, 50, 200);
        model.addLane(1, 0, 50, 200);
        model.addParking(model.getLanes().get(0), model.getLanes().get(1));
        setLayout(new BorderLayout());
		add(new SimulationCanvas(model),BorderLayout.CENTER);
		setPreferredSize(new Dimension(800,600));
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
	}

}
