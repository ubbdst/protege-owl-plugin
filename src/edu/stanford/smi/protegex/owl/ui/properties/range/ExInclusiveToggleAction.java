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

package edu.stanford.smi.protegex.owl.ui.properties.range;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExInclusiveToggleAction extends AbstractAction {

    private Callback callback;

    private Icon exclusiveIcon;

    private Icon inclusiveIcon;


    public ExInclusiveToggleAction(String text, String exclusiveIconName, String inclusiveIconName, Callback callback) {
        super(text, OWLIcons.getImageIcon(inclusiveIconName));
        this.callback = callback;
        exclusiveIcon = OWLIcons.getImageIcon(exclusiveIconName);
        inclusiveIcon = OWLIcons.getImageIcon(inclusiveIconName);
    }


    public void actionPerformed(ActionEvent e) {
        setExclusive(!isExclusive());
        callback.assignInterval();
    }


    public boolean isExclusive() {
        return getValue(Action.SMALL_ICON).equals(exclusiveIcon);
    }


    public void setExclusive(boolean exclusive) {
        if (exclusive) {
            putValue(Action.SMALL_ICON, exclusiveIcon);
        }
        else {
            putValue(Action.SMALL_ICON, inclusiveIcon);
        }
    }


    public static interface Callback {

        void assignInterval();
    }
}
