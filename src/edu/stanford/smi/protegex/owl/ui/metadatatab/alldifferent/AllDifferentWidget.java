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

package edu.stanford.smi.protegex.owl.ui.metadatatab.alldifferent;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import java.awt.*;

/**
 * A property widget to edit the AllDifferent's in a KnowledgeBase.
 * This is not actually assigned to any Slot, as the AllDifferentInstances are
 * independent, free-floating objects in the KnowledgeBase.
 * This widget is used in the OWLMetadataTab, where it can be assigned to the
 * Ontology-URI slot.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AllDifferentWidget extends AbstractPropertyWidget {

    public void initialize() {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        add(BorderLayout.CENTER, new AllDifferentPanel(owlModel));
    }


    public static boolean isSuitable(Cls cls, edu.stanford.smi.protege.model.Slot slot, Facet facet) {
        return slot.getName().equals(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
    }
}
