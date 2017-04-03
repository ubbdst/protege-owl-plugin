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

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.toedter.calendar.JCalendar;

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
public class DateTimeValueEditor implements PropertyValueEditor {

    public boolean canEdit(RDFResource instance, RDFProperty property, Object value) {
        return DateValueEditor.canEdit(instance.getOWLModel().getXSDdateTime(), instance, property, value);
    }


    public Object createDefaultValue(RDFResource instance, RDFProperty property) {
        String str = XMLSchemaDatatypes.getDefaultDateTimeValue();
        return createRDFSLiteral(property.getOWLModel(), str);
    }


    private RDFSLiteral createRDFSLiteral(OWLModel owlModel, String str) {
        return owlModel.createRDFSLiteral(str, owlModel.getRDFSDatatypeByURI(XSDDatatype.XSDdateTime.getURI()));
    }


    public Object editValue(Component parent, RDFResource instance, RDFProperty property, Object value) {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final JFormattedTextField field = new JFormattedTextField(format);
        try {
            final Date date = XMLSchemaDatatypes.getDate(value.toString());
            field.setValue(date);
            JCalendar calendar = new JCalendar(date);
            String name = property.getBrowserText();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(BorderLayout.CENTER, new LabeledComponent("Date", calendar));
            panel.add(BorderLayout.SOUTH, new LabeledComponent("Time", field));
            int r = ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(property.getProject()), panel,
                    "Edit " + property.getBrowserText(), ModalDialogFactory.MODE_OK_CANCEL);
            if (r == ModalDialogFactory.OPTION_OK) {
                Date newDate = calendar.getDate();
                Calendar dateCal = new GregorianCalendar();
                dateCal.setTime(newDate);
                int year = dateCal.get(Calendar.YEAR);
                int month = dateCal.get(Calendar.MONTH);
                int day = dateCal.get(Calendar.DAY_OF_MONTH);

                Calendar timeCal = new GregorianCalendar();
                timeCal.setTime((Date) field.getValue());
                int hour = timeCal.get(Calendar.HOUR_OF_DAY);
                int minute = timeCal.get(Calendar.MINUTE);
                int second = timeCal.get(Calendar.SECOND);

                Calendar newCal = new GregorianCalendar(year, month, day, hour, minute, second);
                String str = XMLSchemaDatatypes.getDateTimeString(newCal.getTime());
                return createRDFSLiteral(property.getOWLModel(), str);
            }
            else {
                return null;
            }
        }
        catch (Exception ex) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            return null;
        }
    }


    public boolean mustEdit(RDFResource subject, RDFProperty predicate, Object value) {
        return canEdit(subject, predicate, value);
    }
}
