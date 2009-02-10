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

// Imports {{{

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// }}}

@SuppressWarnings("serial")
public class ClientsPanel
	extends JPanel
	implements ActionListener
{

	private JButton create_new_client = null;
	private JList clients_list = null;
	private JScrollPane clients_pane = null;
	
	protected void initPanel()
	{
		create_new_client = new JButton("Create new client");
		create_new_client.addActionListener(this);
		clients_list = new JList();
		clients_pane = new JScrollPane(clients_list);
		add(create_new_client);
		add(clients_pane);
	}

	public ClientsPanel() {
		super();
		initPanel();
	}

	public ClientsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initPanel();
	}

	public ClientsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initPanel();
	}

	public ClientsPanel(LayoutManager layout) {
		super(layout);
		initPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
	}	
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
