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

/*
 * Created on Aug 13, 2003
 */
package edu.stanford.smi.protegex.owl.ui.metadatatab.alldifferent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;

import javax.swing.*;
import java.awt.*;

/**
 * A component to edit owl:AllDifferent blocks in the OWL ontology.
 * <p/>
 * The AllDifferentPanel contains two sub-components called
 * 'AllDifferentSelectionPanel' and 'AllDifferentEditorPanel'.
 * The first one represents all AllDifferent tags in the .owl file.
 * The second Panel shows all members which belong to an AllDifferent
 * tag selected in the 'AllDifferentSelectionPanel'.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class AllDifferentPanel extends JPanel {


    public AllDifferentPanel(OWLModel owlModel) {

        AllDifferentEditorPanel editorPanel = new AllDifferentEditorPanel(owlModel);
        AllDifferentSelectionPanel selectionPanel = new AllDifferentSelectionPanel(owlModel, editorPanel);
        editorPanel.setAllDifferentMemberChangedListener(selectionPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectionPanel, editorPanel);

        setLayout(new BorderLayout());
        OWLLabeledComponent lc = new OWLLabeledComponent("owl:AllDifferent", splitPane, true, false);
        add(BorderLayout.CENTER, lc);
        splitPane.setDividerLocation(200);

        setPreferredSize(new Dimension(400, 500));
    }
}
