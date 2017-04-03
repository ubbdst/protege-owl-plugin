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

package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A Swing Action wrapping an OWLModelAction.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLModelActionAction extends AbstractAction implements Disposable {

    private OWLModel owlModel;

    private OWLModelAction owlModelAction;

    private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            setEnabled(owlModelAction.isSuitable(owlModel));
        }
    };


    public OWLModelActionAction(OWLModelAction owlModelAction, OWLModel owlModel) {
        super(owlModelAction.getName(), getIcon(owlModelAction));
        this.owlModelAction = owlModelAction;
        this.owlModel = owlModel;
        setEnabled(owlModelAction.isSuitable(owlModel));
        owlModelAction.addPropertyChangeListener(propertyChangeListener);
    }


    public void actionPerformed(ActionEvent e) {
        owlModelAction.run(owlModel);
    }


    public void dispose() {
        owlModelAction.removePropertyChangeListener(propertyChangeListener);
    }


    public static Icon getIcon(OWLModelAction owlModelAction) {
        Icon icon = Icons.getBlankIcon();
        String fileName = owlModelAction.getIconFileName();
        if (fileName != null) {
            Class c = owlModelAction.getIconResourceClass();
            if (c == null) {
                c = OWLIcons.class;
            }
            if (!fileName.endsWith(".gif") && !fileName.endsWith(".png")) {
                fileName += ".gif";
            }
            icon = OWLIcons.getImageIcon(fileName, c);
        }
        return icon;
    }
}
