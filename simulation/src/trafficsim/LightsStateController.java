/*
    TrafficSim is simulation of road traffic
    Copyright (C) 2009  Mariusz Ceier, Adam Rutkowski

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package trafficsim;

/**
 * Lights state controller - updates lights state
 *
 * @author Mariusz Ceier
 */
 
public class LightsStateController //{{{
    implements IController
{
    // Variables {{{
    private Model p_model = null;
    //}}}

    public LightsStateController(Model model)//{{{
    {
        setModel(model);
    }//}}}

    // FIXME: ???
    public void viewChanged() //{{{
    {
    }//}}}

    public void setModel(Model p_model) {
        this.p_model = p_model;
    }

    public Model getModel() {
        return p_model;
    }

}//}}}

/* vim: set ts=4 sts=4 sw=4 et foldmethod=marker: */
