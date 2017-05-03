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

package edu.stanford.smi.protegex.owl.ui.classform.form;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

/**
 * A ResourceSelectionAction allowing users to add a property component to a ClassForm.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddPropertyFormComponentAction extends ResourceSelectionAction {

    private ClassForm classForm;

    public static final Icon ICON = OWLIcons.getImageIcon(OWLIcons.ADD_PROPERTY_TO_CLASS_FORM); //AddIcon(OWLIcons.OWL_SOME_VALUES_FROM);


    public AddPropertyFormComponentAction(ClassForm classForm) {
        super("Add restrictions on property...", ICON);
        this.classForm = classForm;
    }


    public Collection getSelectableResources() {
        return Collections.EMPTY_LIST;
    }


    public void resourceSelected(RDFResource resource) {
        // TODO
    }
}
