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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;

import javax.swing.*;

import trafficsim.Model;
import trafficsim.gui.SimulationComponent;

public class TrafficSimEditor implements Runnable
{

	private JFrame main_frame = null;
	private Model model = null;
	private SimulationComponent sim_component = null;
	private JPanel main_panel = null;
	private JMenuBar menu_bar = null;
	private JMenu file_menu = null;
	private JMenuItem new_model_menu_item = null;
	private JMenuItem quit_menu_item = null;
	
	public static void main(String []args) throws java.lang.reflect.InvocationTargetException
	{
		try {
			SwingUtilities.invokeAndWait(new TrafficSimEditor());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			model = Model.loadModel("model.xml");
		} catch (FileNotFoundException e1) {
			model = new Model();
		}
		main_frame = new JFrame("TrafficSim Map Editor");
		
		/* Menu bar */
		menu_bar = new JMenuBar();
		
		/* File menu */
		file_menu = new JMenu("File");
		file_menu.setMnemonic(KeyEvent.VK_F);

		new_model_menu_item = new JMenuItem("New model",KeyEvent.VK_N);
		new_model_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		
		new_model_menu_item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				model = new Model();
				sim_component.setModel(model);
			}
			
		});
		file_menu.add(new_model_menu_item);
		
		/* Quit menu item */
		quit_menu_item = new JMenuItem("Quit",KeyEvent.VK_Q);
		quit_menu_item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));

		quit_menu_item.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				main_frame.dispose();
			}
			
		});
		
		file_menu.addSeparator();
		file_menu.add(quit_menu_item);
		
		menu_bar.add(file_menu);
		main_frame.setJMenuBar(menu_bar);

		/* Main panel */
		main_panel = new JPanel(true);
		
		/* Simulation component */
		sim_component = new SimulationComponent(model);
		main_panel.add(sim_component);
		
		main_frame.add(main_panel);

		/* Main frame listeners */
		main_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		main_frame.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				try {
					model.saveModel("model.xml");
				} catch (FileNotFoundException e1) {
				}
				System.exit(0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}
			
		});
		main_frame.pack();
		main_frame.setVisible(true);
	}
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
