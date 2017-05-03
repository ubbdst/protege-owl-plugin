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

package edu.stanford.smi.protegex.owl.ui.components.annotations;

import javax.swing.*;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 14, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationsTableCellHolder extends JPanel {

    public static final Color LINE_COLOR = Color.LIGHT_GRAY;

    public static final Color TABLE_SELECTION_FOREGROUND = UIManager.getDefaults().getColor("Table.selectionForeground");

    public static final Color TABLE_FOREGROUND = UIManager.getDefaults().getColor("Table.foreground");

    public static final Color TABLE_SELECTION_BACKGROUND = UIManager.getDefaults().getColor("Table.selectionBackground");

    public static final Color TABLE_BACKGROUND = UIManager.getDefaults().getColor("Table.background");

    private JComponent component;


    public AnnotationsTableCellHolder(JComponent component, String location) {
        this.component = component;
        setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        setOpaque(true);
        setLayout(new BorderLayout());
        if (component != null) {
            add(component, location);
        }
        setRequestFocusEnabled(true);
    }


    public void setColors(boolean selected, boolean focused) {
        if (selected) {
            setBackground(TABLE_SELECTION_BACKGROUND);
            setForeground(TABLE_SELECTION_FOREGROUND);
        }
        else {
            setBackground(TABLE_BACKGROUND);
            setForeground(TABLE_FOREGROUND);
        }
    }
}
