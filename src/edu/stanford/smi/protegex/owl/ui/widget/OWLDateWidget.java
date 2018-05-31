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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.toedter.calendar.JDateChooser;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ReadOnlyWidgetConfigurationPanel;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDateWidget extends AbstractPropertyWidget {

    private JDateChooser dateChooser;
    private LabeledComponent lc;
    private boolean enableAddAction = true;

    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                updateValues();
            }
        }
    };

   protected Action deleteAction = new AbstractAction(getDeleteActionText(), getDeleteActionIcon()) {
        public void actionPerformed(ActionEvent e) {
            deleteValue();
        }
    };

    public String getDeleteActionText() {
        return "Delete value";
    }

    public Icon getDeleteActionIcon() {
        return  OWLIcons.getDeleteIcon();
    }

    private final Action setAction = new AbstractAction("Set value", OWLIcons.getAddIcon()) {
        public void actionPerformed(ActionEvent e) {
            setPropertyValue(new Date());
        }
    };

    public OWLDateWidget() {
        setPreferredColumns(2);
        setPreferredRows(1);
    }

    public OWLDateWidget(boolean enableAddAction) {
        this();
        this.enableAddAction = enableAddAction;
    }

    protected RDFSLiteral createPropertyValue(Date date) {
        String value = XMLSchemaDatatypes.getDateString(date);
        RDFSDatatype datatype = getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDdate.getURI());
        return getOWLModel().createRDFSLiteral(value, datatype);
    }


    protected void deleteValue() {
        getEditedResource().setPropertyValue(getRDFProperty(), null);
    }


    protected Component getCenterComponent() {
        return dateChooser;
    }


    protected Date getDate() {
        return dateChooser.getDate();
    }


    public static Date getDate(String s) {
        if (s == null) { return null; }
        Date date = new Date();
        int index = s.indexOf("T");
        if (index >= 0) {
            s = s.substring(0, index);
        }
        //TODO: Does not consider the timezone!
        int zindex = s.indexOf("Z");
        if (zindex >= 0) {
            s = s.substring(0, zindex);
        }

        String[] ss = s.split("-");
        if (ss.length >= 3) {
            try {
                int year = Integer.parseInt(ss[0]);
                int month = Integer.parseInt(ss[1]) - 1;
                int day = Integer.parseInt(ss[2]);
                date = new Date(new GregorianCalendar(year, month, day).getTimeInMillis());
            }
            catch (Exception ex) {
                Log.getLogger().warning("Could not parse value " + s + ": " + ex.getMessage());
            }
        }
        return date;
    }

    public void initialize() {
        setLayout(new BorderLayout());
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        lc = new LabeledComponent(getRDFProperty().getBrowserText(), getCenterComponent());
        if(enableAddAction) {
            lc.addHeaderButton(setAction);
        }
        lc.addHeaderButton(deleteAction);
        add(BorderLayout.CENTER, lc);
        enabledCompListeners();
    }


    public void setDateChooserValue() {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        Object value = resource.getPropertyValue(property);
        System.out.println("Set date chooser value: " + value);
        setValue(value == null ? null : value.toString());
    }


    protected void setValue(String s) {
    	disableCompListeners();
        Date date = getDate(s);
        dateChooser.setDate(date);
        enabledCompListeners();
    }


    public void setPropertyValue(Date date) {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null) {
            if (date == null) {
                resource.setPropertyValue(property, null);
            } else {
                Object value = createPropertyValue(date);
                resource.setPropertyValue(property, value);
            }
        }
    }

    @Override
	public void setEnabled(boolean enabled) {
        super.setEnabled(!isReadOnlyConfiguredWidget() && enabled);
        updateComponents();
    }


    @Override
	public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        disableCompListeners();
        if (newInstance != null) {
            setDateChooserValue();
        }
        updateComponents();
        enabledCompListeners();
    }


    @Override
	public void setValues(Collection values) {
        super.setValues(values);
        updateComponents();
        ignoreUpdate = true;
        setDateChooserValue();
        ignoreUpdate = false;
    }


    protected void updateComponents() {
    	boolean isEditable = !isReadOnlyConfiguredWidget();

        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null && resource.isEditable()) {
            boolean value = resource.getPropertyValue(property) != null;
            setAction.setEnabled(isEditable && !value);
            deleteAction.setEnabled(isEditable && value);
            //dateChooser.setEnabled(isEditable && value);
            enableDateChooser(isEditable && value);
            lc.revalidate();
        }
        else {
            setAction.setEnabled(false);
            deleteAction.setEnabled(false);
            //dateChooser.setEnabled(false);
            enableDateChooser(false);
        }


    }

    private void enableDateChooser(boolean enable) {
    	 for (Component comp : dateChooser.getComponents()) {
    		 comp.setEnabled(enable);
    	 }
    }


    private boolean ignoreUpdate = false;


    protected void updateValues() {
        if (!ignoreUpdate) {
            Date date = getDate();
            setPropertyValue(date);
        }
    }


    @Override
    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
    	WidgetConfigurationPanel confPanel = super.createWidgetConfigurationPanel();

    	confPanel.addTab("Options", new ReadOnlyWidgetConfigurationPanel(this));

    	return confPanel;
    }

    protected void enabledCompListeners() {
    	dateChooser.addPropertyChangeListener(propertyChangeListener);
    }

    protected void disableCompListeners() {
    	dateChooser.removePropertyChangeListener(propertyChangeListener);
    }


    @Deprecated
	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(OWLDateWidget.class, cls, slot);
    }
}