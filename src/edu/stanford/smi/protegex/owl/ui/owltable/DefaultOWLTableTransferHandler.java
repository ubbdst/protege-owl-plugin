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

package edu.stanford.smi.protegex.owl.ui.owltable;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;

/**
 * A default OWLTableTransferHandler that assumes that the class maintain their
 * rows after changing something.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLTableTransferHandler extends OWLTableTransferHandler {

    private int addCount = 0;  // Number of items added.

    private int addIndex = -1; // Location where items were added


    public DefaultOWLTableTransferHandler(OWLModel owlModel) {
        super(owlModel);
    }


    protected void cleanup(JComponent c, boolean remove) {
        JTable source = (JTable) c;
        if (remove && rows != null) {
            OWLTableModel model = (OWLTableModel) source.getModel();

            // If we are moving items around in the same table, we
            // need to adjust the rows accordingly, since those
            // after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] > addIndex) {
                        rows[i] += addCount;
                    }
                }
            }
            for (int i = rows.length - 1; i >= 0; i--) {
                model.deleteRow(rows[i]);
            }
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }


    protected int importOWLClses(JComponent c, String clsesText) {
        addIndex = super.importOWLClses(c, clsesText);
        String[] values = clsesText.split("\n");
        addCount = values.length;
        return addIndex;
    }
}
