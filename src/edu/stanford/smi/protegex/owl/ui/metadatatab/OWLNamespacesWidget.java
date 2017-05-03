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

package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLNamespacesWidget extends AbstractPropertyWidget {

    private OWLNamespacesPanel panel;


    public void dispose() {
        super.dispose();
        if (panel != null) {
            panel.dispose();
        }
    }


    public void initialize() {
        panel = new OWLNamespacesPanel(getOWLModel().getDefaultOWLOntology());
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, panel);
    }


    public static boolean isSuitable(Cls cls, edu.stanford.smi.protege.model.Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(OWLNames.Slot.IMPORTS);
    }


    public void setInstance(Instance newInstance) {
        remove(panel);
        panel.dispose();
        panel = null;
        super.setInstance(newInstance);
        if (newInstance instanceof OWLOntology) {
            panel = new OWLNamespacesPanel((OWLOntology) newInstance);
            add(BorderLayout.CENTER, panel);
            revalidate();
        }
        TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
        panel.setEnabled(tsm.getActiveTripleStore() == tsm.getTopTripleStore() && isEnabled());
    }
    
    public void setEnabled(boolean enabled) {
    	panel.setEnabled(enabled);
    	super.setEnabled(enabled);
    };
}
