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

package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.SubclassPane;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SearchNamedClassAction extends ResourceAction {

    public static final String GROUP = "Search and View/";


    public SearchNamedClassAction() {
        super("Search subclass by property value...", Icons.getFindIcon(), GROUP);
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof SubclassPane && resource instanceof RDFSNamedClass;
    }


    public void actionPerformed(ActionEvent e) {
        RDFSNamedClass rootClass = (RDFSNamedClass) getResource();
        SubclassPane subclassPane = (SubclassPane) getComponent();
        List result = SearchNamedClassPanel.showDialog(getComponent(), rootClass);
        if (result != null) {
            if (result.size() == 0) {
                ProtegeUI.getModalDialogFactory().showMessageDialog(rootClass.getOWLModel(),
                        "There were no matching classes.");
            }
            else {
                OWLNamedClass selectedCls = (OWLNamedClass) ProtegeUI.getSelectionDialogFactory().
                        selectResourceFromCollection(getComponent(),
                                rootClass.getOWLModel(), result, "Select a matching class");
                if (selectedCls != null) {
                    subclassPane.setSelectedCls(selectedCls);
                }
            }
        }
    }
}
