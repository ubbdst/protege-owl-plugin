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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.widget.StringListWidget;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * An StringListWidget that only allows to edit valid resource names.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @deprecated no longer really needed
 */
public class ResourceListWidget extends StringListWidget {

    protected void handleCreateAction() {
        String uri = ProtegeUI.getModalDialogFactory().showInputDialog(this,
                "Please enter a resource URI", "http://");
        if (uri != null) {
            if (getInstance().getDirectOwnSlotValues(getSlot()).contains(uri)) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this,
                        uri + " is already one of the values.",
                        "Duplicate value");
            }
            else {
                try {
                    new URI(uri);
                    getInstance().addOwnSlotValue(getSlot(), uri);
                }
                catch (URISyntaxException ex) {
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this,
                            uri + " is not a valid URI.",
                            "Invalid URI");
                }
            }
        }
    }


    protected void handleViewAction(String str) {
        String uri = JOptionPane.showInputDialog(ProtegeUI.getTopLevelContainer(getProject()),
                "Please enter a resource URI", str);
        if (uri != null && !uri.equals(str)) {
            try {
                new URI(uri);
                getInstance().removeOwnSlotValue(getSlot(), str);
                getInstance().addOwnSlotValue(getSlot(), uri);
            }
            catch (URISyntaxException ex) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this,
                        uri + " is not a valid URI.",
                        "Invalid URI");
            }
        }
    }
}
