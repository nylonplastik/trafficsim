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

package gui;

/* Imports {{{ */

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
public class TrafficSimFrame extends JFrame
    implements ActionListener
{
    JMenuItem quit_menu_item = null;

    public TrafficSimFrame(String title)
    {
        super(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menu_bar = new JMenuBar();
        JMenu file_menu = new JMenu("File");
        file_menu.setMnemonic(KeyEvent.VK_F);
        menu_bar.add(file_menu);
        quit_menu_item = new JMenuItem("Quit", KeyEvent.VK_Q);
        quit_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quit_menu_item.addActionListener(this);
        file_menu.add(quit_menu_item);
        setJMenuBar(menu_bar);
        setEnabled(true);
        setContentPane(new TrafficSimMainPanel());
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == quit_menu_item)
        {
            this.dispose();
        }

    }

}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
