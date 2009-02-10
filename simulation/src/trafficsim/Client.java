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

package trafficsim;

/**
 *
 * @author Adam Rutkowski
 */
public class Client //{{{
{
    // Variables {{{
    private ClientViewServerSide p_serverSideView;
    private Model                p_model;
    
    // TODO: this field should be removed, references to it replaced by 
    // communication with client process.
    private ClientViewClientSide p_clientSideView;
    //}}}

    public Client(
            Model model, 
            ClientViewClientSide clientSideView, 
            ClientViewServerSide serverSideView
            )//{{{
    {
        p_serverSideView = serverSideView;
        p_model          = model;
        p_clientSideView = clientSideView;
    }//}}}
}//}}}

/* vim: set ts=4 sw=4 sts=4 et foldmethod=marker: */
