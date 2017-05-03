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

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.ui.metadatatab.prefixes.PrefixesPanel;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel that allows users to view and edit the default namespace, the prefixes
 * and the imports of an OWLOntology.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLNamespacesPanel extends JPanel implements Disposable {

    private PrefixesPanel prefixesPanel;


    public OWLNamespacesPanel(OWLOntology ontology) {
        prefixesPanel = new PrefixesPanel(ontology);
        setLayout(new BorderLayout(0, 10));
        add(BorderLayout.NORTH, new LabeledComponent("Default Namespace",
                                                     prefixesPanel.getDefaultNamespaceField()));
        add(BorderLayout.CENTER, prefixesPanel);
    }


    public void dispose() {
        prefixesPanel.dispose();
    }


    public void setEnabled(boolean enabled) {        
        prefixesPanel.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
