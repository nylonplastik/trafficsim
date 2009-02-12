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

/* Imports {{{ */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* }}} */

/**
 * Main frame
 *
 * @class TrafficSimFrame
 *
 */
@SuppressWarnings("serial")
public class MainFrame
	extends JFrame
    implements ActionListener, ComponentListener
{
	private JMenuItem quit_menu_item = null;
	private JMenuItem map_editor_menu_item = null;
	
	/**
	 * Initializes frame
	 */
	protected void initFrame()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menu_bar = new JMenuBar();
        JMenu file_menu = new JMenu("File");
        file_menu.setMnemonic(KeyEvent.VK_F);
        menu_bar.add(file_menu);
        map_editor_menu_item = new JMenuItem("Map Editor...", KeyEvent.VK_M);
        map_editor_menu_item.setAccelerator(
        		KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.CTRL_MASK));
        map_editor_menu_item.addActionListener(this);
        map_editor_menu_item.setToolTipText("Map editor not implemented yet");
        map_editor_menu_item.setEnabled(true);
        file_menu.add(map_editor_menu_item);
        file_menu.addSeparator();
        quit_menu_item = new JMenuItem("Quit", KeyEvent.VK_Q);
        quit_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
        		ActionEvent.CTRL_MASK));
        quit_menu_item.addActionListener(this);
        file_menu.add(quit_menu_item);
        setJMenuBar(menu_bar);
        setEnabled(true);
        setResizable(true);
        setUndecorated(true);
        // TODO: is this really needed ? ... 
        // TODO: must test this under more conformant wm ... 
        // addComponentListener(this);
        setContentPane(new MainPanel());
    }
	
    public MainFrame() throws HeadlessException {
		super();
		initFrame();
	}

	public MainFrame(GraphicsConfiguration gc) {
		super(gc);
		initFrame();
	}

	public MainFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		initFrame();
	}

	public MainFrame(String title) throws HeadlessException {
		super(title);
		initFrame();
	}

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == quit_menu_item)
        {
            this.dispose();
        } else
        if (e.getSource() == map_editor_menu_item)
        {
        	EditorFrame editor = new EditorFrame("Map editor");
        	editor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        	editor.pack();
        	editor.setVisible(true);
        }

    }

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		doLayout();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
