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

package edu.stanford.smi.protegex.owl.ui.conditions;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * A DefaultTableCellRenderer (derived from JLabel) used to display separators
 * in the ConditionsTable.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SeparatorCellRenderer extends DefaultTableCellRenderer {

    private boolean grayed;


    public SeparatorCellRenderer(boolean grayed) {
        this.grayed = grayed;
        setHorizontalAlignment(JLabel.RIGHT);
        setVerticalAlignment(JLabel.BOTTOM);
        setVerticalTextPosition(JLabel.BOTTOM);
    }


    public void paint(Graphics g) {
        setForeground(grayed ? Color.gray : Color.black);
        setFont(getFont().deriveFont(Font.PLAIN, 9.0f));
        super.paint(g);
        int y = getHeight() / 2;
        g.setColor(grayed ? Color.lightGray : Color.black);
        int strWidth = getFontMetrics(getFont()).stringWidth(getText());
        g.drawLine(4, y, getWidth() - 6 - strWidth, y);
    }
}
