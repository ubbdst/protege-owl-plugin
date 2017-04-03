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

package edu.stanford.smi.protegex.owl.ui.editors;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JFormattedTextField;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TimeValueEditor implements PropertyValueEditor {

    public boolean canEdit(RDFResource instance, RDFProperty property, Object value) {
        return DateValueEditor.canEdit(instance.getOWLModel().getXSDtime(), instance, property, value);
    }


    public Object createDefaultValue(RDFResource instance, RDFProperty property) {
        OWLModel owlModel = property.getOWLModel();
        String str = XMLSchemaDatatypes.getTimeString(new Date());
        return createRDFSLiteral(owlModel, str);
    }


    private RDFSLiteral createRDFSLiteral(OWLModel owlModel, String str) {
        return owlModel.createRDFSLiteral(str, owlModel.getRDFSDatatypeByURI(XSDDatatype.XSDtime.getURI()));
    }


    public Object editValue(Component parent, RDFResource instance, RDFProperty property, Object value) {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final JFormattedTextField field = new JFormattedTextField(format);
        try {
            final Date date = format.parse(value.toString());
            field.setValue(date);
            String name = property.getBrowserText();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            LabeledComponent lc = new LabeledComponent(name, field);
            int r = ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(property.getProject()), lc,
                    "Edit " + property.getBrowserText(), ModalDialogFactory.MODE_OK_CANCEL, new ModalDialogFactory.CloseCallback() {
                public boolean canClose(int result) {
                    return field.isEditValid();
                }
            });
            if (r == ModalDialogFactory.OPTION_OK) {
                Date newDate = (Date) field.getValue();
                String str = XMLSchemaDatatypes.getTimeString(newDate);
                return createRDFSLiteral(property.getOWLModel(), str);
            }
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }

        return null;
    }


    public boolean mustEdit(RDFResource subject, RDFProperty predicate, Object value) {
        return canEdit(subject, predicate, value);
    }
}
