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

package edu.stanford.smi.protegex.owl.ui.restrictions;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLRestriction;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextArea;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * A OWLTextArea to edit the filler in a RestrictionsTable.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FillerTextArea extends OWLTextArea {

    private RDFProperty onProperty;

    private RDFProperty restrictionProperty;


    public FillerTextArea(OWLModel owlModel, SymbolErrorDisplay errorDisplay) {
        super(owlModel, errorDisplay);
    }


    public void checkExpression(String text) throws Throwable {
        AbstractOWLRestriction.checkExpression(text, onProperty, restrictionProperty);
    }


    public void setOnProperty(RDFProperty onProperty) {
        this.onProperty = onProperty;
    }


    public void setRestrictionProperty(RDFProperty restrictionProperty) {
        this.restrictionProperty = restrictionProperty;
    }


    protected void stopEditing() {
        ProtegeUI.getModalDialogFactory().attemptDialogClose(ModalDialogFactory.OPTION_OK);
    }
}
