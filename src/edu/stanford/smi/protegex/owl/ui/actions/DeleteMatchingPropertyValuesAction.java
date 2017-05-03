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

package edu.stanford.smi.protegex.owl.ui.actions;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteMatchingPropertyValuesAction extends AbstractAction {

    private RDFProperty property;

    private Object value;

    private Collection targetFrames;

    private String type;


    public DeleteMatchingPropertyValuesAction(String type, RDFProperty property, Object value, Collection targetResources) {
        this(type, property, value, targetResources, "annotation");
    }


    public DeleteMatchingPropertyValuesAction(String type, RDFProperty property, Object value, Collection targetResources,
                                              String partialName) {
        super("Delete selected " + partialName + " value in " + type + "...", OWLIcons.getDeleteIcon(OWLIcons.SUB_CLASS));
        this.property = property;
        this.value = value;
        this.targetFrames = targetResources;
        this.type = type;
    }


    public void actionPerformed(ActionEvent e) {
        OWLModel owlModel = property.getOWLModel();
        try {
            owlModel.beginTransaction("Delete annotation " + value +
                    " from property " + property.getBrowserText() + " at multiple " + type, (property == null ? null : property.getName()));
            for (Iterator it = targetFrames.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                if (resource.getPropertyValues(property).contains(value)) {
                    resource.removePropertyValue(property, value);
                }
            }
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }
}
