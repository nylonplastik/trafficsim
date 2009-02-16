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
import javax.swing.*;

// }}}

@SuppressWarnings("serial")
public class CreateServerDialog
    extends JDialog
{

    private JButton ok_button = null;
    private JButton cancel_button = null;
    
    protected void initDialog()
    {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(3,2));
        this.add(new JLabel("Server name:"));
        this.add(new JEditorPane());
        this.add(new JLabel("Server port:"));
        this.add(new JEditorPane());
        
        ok_button = new JButton("OK");
        this.add(ok_button);
        cancel_button = new JButton("Cancel");
        this.add(cancel_button);
        this.setResizable(true);
    }

    public CreateServerDialog() {
        super();
        initDialog();
    }

    public CreateServerDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        initDialog();
    }

    public CreateServerDialog(Dialog owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public CreateServerDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public CreateServerDialog(Dialog owner, String title) {
        super(owner, title);
        initDialog();
    }

    public CreateServerDialog(Dialog owner) {
        super(owner);
        initDialog();
    }

    public CreateServerDialog(Frame owner, boolean modal) {
        super(owner, modal);
        initDialog();
    }

    public CreateServerDialog(Frame owner, String title, boolean modal,
            GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initDialog();
    }

    public CreateServerDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initDialog();
    }

    public CreateServerDialog(Frame owner, String title) {
        super(owner, title);
        initDialog();
    }

    public CreateServerDialog(Frame owner) {
        super(owner);
        initDialog();
    }

    public CreateServerDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        initDialog();
    }

    public CreateServerDialog(Window owner, String title,
            ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initDialog();
    }

    public CreateServerDialog(Window owner, String title,
            ModalityType modalityType) {
        super(owner, title, modalityType);
        initDialog();
    }

    public CreateServerDialog(Window owner, String title) {
        super(owner, title);
        initDialog();
    }

    public CreateServerDialog(Window owner) {
        super(owner);
        initDialog();
    }
    
}

/* vim: set ts=4 sts=4 sw=4 expandtab foldmethod=marker : */
