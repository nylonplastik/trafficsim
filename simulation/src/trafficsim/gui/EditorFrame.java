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
    private SimulationCanvas sim_canvas = null;
    private JPanel editor_panel = null;
    
    private LanesCross moving_cross = null;
    
    protected void initFrame()
    {
        model = new Model();
        int cross1 = model.addCross(10,10);
        int cross2 = model.addCross(20,20);
        model.addLane(cross1, cross2, 50, 200);
        model.addLane(cross2, cross1, 50, 200);
        model.getCars().add(model.addParking(model.getLanes().get(0), model.getLanes().get(1)).newCar());
        setLayout(new BorderLayout());
        sim_canvas = new SimulationCanvas(model);
        sim_canvas.addMouseListener(this);
        sim_canvas.addMouseMotionListener(this);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        sim_canvas.getModel().addCross(e.getX(), e.getY());
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
        for(LanesCross lc : sim_canvas.getModel().getLanesCrosses().values())
        {
            if ((-5<=lc.getX()-e.getX())&&(lc.getX()-e.getX()<=5)&&
                (-5<=lc.getY()-e.getY())&&(lc.getY()-e.getY()<=5))
            {
                System.out.println("Found cross");
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
