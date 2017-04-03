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

package edu.stanford.smi.protegex.owl.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Mar-2006
 */
public class TooltippedSelectableList extends SelectableList {

    public TooltippedSelectableList() {
        super();
        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        addMouseListener(new MouseAdapter() {
            @Override
			public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });
        setToolTipText(""); // Dummy to initialize the mechanism
    }


    @Override
	public String getToolTipText(MouseEvent event) {
        int row = locationToIndex(event.getPoint());
        if (row < 0) { return "";}
        Object o = getModel().getElementAt(row);
        if (o != null && o instanceof RDFResource) {
            ToolTipManager.sharedInstance().setDismissDelay(OWLTable.INFINITE_TIME);
            return OWLUI.getOWLToolTipText((RDFResource) o);
        }
        return null;
    }
}