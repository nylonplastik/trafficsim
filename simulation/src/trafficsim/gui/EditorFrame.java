package trafficsim.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import trafficsim.Model;

@SuppressWarnings("serial")
public class EditorFrame extends JFrame {

	private Model model = null;
	private SimulationCanvas sim_canvas = null;
	private JPanel editor_panel = null;
	
	protected void initFrame()
	{
		model = new Model();
        model.addCross(0,10,10);
        model.addCross(1,20,20);
        model.addLane(0, 1, 50, 200);
        model.addLane(1, 0, 50, 200);
        model.getCars().add(model.addParking(model.getLanes().get(0), model.getLanes().get(1)).newCar());
        setLayout(new BorderLayout());
        sim_canvas = new SimulationCanvas(model);
        editor_panel = new JPanel(true);
        editor_panel.add(sim_canvas,BorderLayout.CENTER);
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
		sim_canvas.setModel(model);
	}

}
