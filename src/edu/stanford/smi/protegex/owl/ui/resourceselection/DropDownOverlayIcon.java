/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.ui.resourceselection;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DropDownOverlayIcon implements Icon {

    private Icon baseIcon;

    private JButton button;


    public DropDownOverlayIcon(Icon baseIcon, JButton button) {
        this.baseIcon = baseIcon;
        this.button = button;
    }


    public int getIconHeight() {
        return baseIcon.getIconHeight();
    }


    public int getIconWidth() {
        return baseIcon.getIconWidth();
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        baseIcon.paintIcon(c, g, x, y);
        int bx = button.getWidth() - 19; //8;
        int by = button.getHeight() - 6;
        g.drawLine(bx, by, bx + 4, by);
        g.drawLine(bx + 1, by + 1, bx + 3, by + 1);
        g.drawLine(bx + 2, by + 2, bx + 2, by + 2);
    }
}
