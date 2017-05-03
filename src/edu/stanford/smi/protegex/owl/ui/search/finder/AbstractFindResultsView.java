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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-Oct-2005
 */
abstract class AbstractFindResultsView extends JComponent {

    private HostResourceDisplay hostResourceDisplay;

    protected AbstractFindResultsView(HostResourceDisplay hostResourceDisplay) {
        this.hostResourceDisplay = hostResourceDisplay;
    }

    abstract RDFResource getSelectedResource();

//    abstract void setResults(Map results);

    public abstract void addMouseListener(MouseListener l);

    public abstract void addKeyListener(KeyListener l);

    public abstract void requestFocus();

    /**
     * Make sure the resource is selected in the main Protege window
     */
    public void selectResource() {
        RDFResource resource = getSelectedResource();
        requestDispose();
        if (resource != null) {
            OWLUI.selectResource(resource, hostResourceDisplay);
        }
    }

    protected void requestDispose() {
        try {
            JDialog dialog = (JDialog) getTopLevelAncestor();
            if (dialog != null) {
                dialog.dispose();
            }
        }
        catch (Exception e) {
        }
    }
}
