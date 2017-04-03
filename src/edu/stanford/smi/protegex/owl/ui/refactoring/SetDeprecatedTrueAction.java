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

package edu.stanford.smi.protegex.owl.ui.refactoring;

import edu.stanford.smi.protegex.owl.model.Deprecatable;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A ResourceAction to set a class or property to deprecated.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SetDeprecatedTrueAction extends RefactorResourceAction {

    public SetDeprecatedTrueAction() {
        super("Set deprecation flag", OWLIcons.getAddIcon("Deprecated"));
    }


    public void actionPerformed(ActionEvent e) {
        try {
            getOWLModel().beginTransaction("" + getValue(Action.NAME) + " from " + getResource().getBrowserText(), getResource().getName());
            ((Deprecatable) getResource()).setDeprecated(true);
            getOWLModel().commitTransaction();
        }
        catch (Exception ex) {
        	getOWLModel().rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return //frame.isEditable() &&
                resource instanceof Deprecatable &&
                        !((Deprecatable) resource).isDeprecated();
    }
}
