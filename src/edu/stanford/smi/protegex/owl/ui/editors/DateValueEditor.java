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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.toedter.calendar.JCalendar;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.widget.OWLDateWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetUtil;

import java.awt.*;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DateValueEditor implements PropertyValueEditor {

    public boolean canEdit(RDFResource instance, RDFProperty property, Object value) {
        final RDFSDatatype date = instance.getOWLModel().getXSDdate();
        return canEdit(date, instance, property, value);
    }


    static boolean canEdit(final RDFSDatatype dataType, RDFResource instance, RDFProperty property, Object value) {
        if (value instanceof RDFSLiteral) {
            RDFSLiteral literal = (RDFSLiteral) value;
            if (dataType.equals(literal.getDatatype())) {
                return true;
            }
        }
        for (Iterator it = instance.getRDFTypes().iterator(); it.hasNext();) {
            RDFSClass type = (RDFSClass) it.next();
            if (type instanceof RDFSNamedClass) {
                if (OWLWidgetUtil.isDatatypeProperty(dataType, (RDFSNamedClass) type, property)) {
                    return true;
                }
            }
        }
        return false;
    }


    public Object createDefaultValue(RDFResource instance, RDFProperty property) {
        String str = XMLSchemaDatatypes.getDefaultDateValue();
        return createRDFSLiteral(property.getOWLModel(), str);
    }


    private RDFSLiteral createRDFSLiteral(OWLModel owlModel, String str) {
        return owlModel.createRDFSLiteral(str, owlModel.getRDFSDatatypeByURI(XSDDatatype.XSDdate.getURI()));
    }


    public Object editValue(Component parent, RDFResource instance, RDFProperty property, Object value) {
        if (canEdit(instance, property, value)) {
            JCalendar calendar = new JCalendar(OWLDateWidget.getDate(value.toString()));
            String name = property.getBrowserText();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            LabeledComponent lc = new LabeledComponent(name, calendar);
            int r = ProtegeUI.getModalDialogFactory().showDialog(parent, lc, "Edit " + property.getBrowserText(), ModalDialogFactory.MODE_OK_CANCEL);
            if (r == ModalDialogFactory.OPTION_OK) {
                Date date = calendar.getDate();
                String newValue = XMLSchemaDatatypes.getDateString(date);
                return createRDFSLiteral(property.getOWLModel(), newValue);
            }
        }
        return null;
    }


    public boolean mustEdit(RDFResource subject, RDFProperty predicate, Object value) {
        return canEdit(subject, predicate, value);
    }
}
