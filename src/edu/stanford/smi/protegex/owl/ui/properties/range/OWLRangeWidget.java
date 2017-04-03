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

package edu.stanford.smi.protegex.owl.ui.properties.range;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.menu.preferences.ProtegeSettingsPanel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

/**
 * A property widget to edit the range of an RDFProperty (or subclasses).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLRangeWidget extends AbstractPropertyWidget {

    private final static String ANY = "Any";

    private UnionRangeClassesComponent classesComponent;

    private JComboBox comboBox;

    private OWLDataRangeComponent dataRangeComponent;

    private FacetsPanel facetsPanel;

    private LabeledComponent lc;

    private JPanel mainPanel;

    private JPanel northPanel;

    private final static String OBJECTS = "Objects";

    /**
     * Needed to avoid infinite recursion: If the range changes, the form may
     * have been re-created as a side-effect!
     */
    private boolean ignore = false;


    public static void addDatatypes(OWLModel owlModel, Vector values) {
        values.add(owlModel.getXSDboolean());
        values.add(owlModel.getXSDfloat());
        values.add(owlModel.getXSDint());
        values.add(owlModel.getXSDstring());
        java.util.List ds = new ArrayList(owlModel.getRDFSDatatypes());
        ds.removeAll(values);
        ds = OWLUtil.removeInvisibleResources(ds.iterator());
        Collections.sort(ds, new FrameComparator());
        values.add(" ");
        values.addAll(ds);
    }


    private FacetsPanel createFacetsPanel(RDFSDatatype datatype) {
        if (datatype != null) {
            RDFSDatatype baseDatatype = datatype.getBaseDatatype();
            if (baseDatatype != null) {
                return createFacetsPanel(baseDatatype);
            }
            else {
                if (datatype.equals(getOWLModel().getXSDstring())) {
                    return new StringFacetsPanel(this);
                }
                else if (datatype.isNumericDatatype()) {
                    return new NumericFacetsPanel(this);
                }
            }
        }
        return null;
    }


    public void initialize() {

        classesComponent = new UnionRangeClassesComponent(getOWLModel(), this);

        comboBox = new JComboBox();
        comboBox.setRenderer(new FrameRenderer());
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleComboBoxChanged();
            }
        });
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(BorderLayout.NORTH, comboBox);

        dataRangeComponent = new OWLDataRangeComponent(this);

        mainPanel = new JPanel(new BorderLayout(0, 4));
        mainPanel.add(BorderLayout.NORTH, northPanel);
        mainPanel.add(BorderLayout.CENTER, dataRangeComponent);

        lc = new LabeledComponent("Range", mainPanel);
        lc.setVerticallyStretchable(true);
        add(BorderLayout.CENTER, lc);
    }


    RDFProperty getEditedProperty() {
        return (RDFProperty) getEditedResource();
    }


    private void handleComboBoxChanged() {
        Object newValue = comboBox.getSelectedItem();
        if (" ".equals(newValue)) {
            comboBox.setSelectedItem(ANY);
            newValue = ANY;
        }
        if (ANY.equals(newValue)) {
            setRange(null);
            dataRangeComponent.setDatatype(null);            
        }
        else if (OBJECTS.equals(newValue)) {
            if (getEditedProperty().getRange() != null) {
                setRange(null);
            }
            mainPanel.remove(dataRangeComponent);
            mainPanel.add(BorderLayout.CENTER, classesComponent);
            mainPanel.revalidate();
        }
        else if (newValue instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) newValue;
            if (!datatype.equals(getEditedProperty().getRange())) {
                setRange(datatype);
            }
            dataRangeComponent.setDatatype(datatype);
            if (dataRangeComponent.getParent() != mainPanel) {
                mainPanel.remove(classesComponent);
                mainPanel.add(BorderLayout.CENTER, dataRangeComponent);
                mainPanel.revalidate();
            }
        }
        updateFacetsPanel();
    }


    public static boolean isSuitable(Cls cls, edu.stanford.smi.protege.model.Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(RDFSNames.Slot.RANGE);
    }


    private void refillAll() {
        refillComboBox();
        dataRangeComponent.refill();
        classesComponent.refill();
        updateFacetsPanel();
    }


    private void refillComboBox() {
        if (getEditedResource() instanceof OWLObjectProperty) {
            removeAll();
            add(BorderLayout.CENTER, classesComponent);
        }
        else if (getEditedResource() instanceof RDFProperty) {
            add(BorderLayout.CENTER, lc);
            Vector values = new Vector();
            OWLModel owlModel = getOWLModel();
            values.add(ANY);
            if (!(getEditedResource() instanceof OWLDatatypeProperty)) {
                values.add(OBJECTS);
            }
            addDatatypes(owlModel, values);
            comboBox.setModel(new DefaultComboBoxModel(values));
            selectComboBoxValue();
            dataRangeComponent.setEditable(!ANY.equals(comboBox.getSelectedItem()) && getEditedProperty().isEditable());
        }
    }


    private void selectComboBoxValue() {
        RDFProperty property = (RDFProperty) getEditedResource();
        if (property != null) {
            ignore = true;
            selectComboBoxValue(property);
            ignore = false;
        }
    }


    private void selectComboBoxValue(RDFProperty property) {
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) range;
            if (datatype.getBaseDatatype() != null) {
                datatype = datatype.getBaseDatatype();
            }
            comboBox.setSelectedItem(datatype);
        }
        else if (range instanceof OWLDataRange) {
            RDFSDatatype datatype = ((OWLDataRange) range).getRDFDatatype();
            if (datatype != null && !datatype.equals(comboBox.getSelectedItem())) {
                comboBox.setSelectedItem(datatype);
            }
        }
        else if (range instanceof RDFSClass) {
            comboBox.setSelectedItem(OBJECTS);
        }
        else if (range == null && property.getSuperpropertyCount() > 0) {
            RDFProperty firstSuperproperty = property.getFirstSuperproperty();
            selectComboBoxValue(firstSuperproperty);
        }
    }


    @Override
	public void setEditable(boolean b) {
        dataRangeComponent.setEditable(b);
        //classesComponent.setEditable(b);
        comboBox.setEnabled(b);
        if (facetsPanel != null) {
            facetsPanel.setEditable(b);
        }
    }


    @Override
	public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        refillAll();
    }


    void setRange(RDFSDatatype datatype) {
        if (!ignore) {
            RDFResource range = getEditedProperty().getRange();
            if (range == null || !range.equals(datatype)) {
                if (range instanceof RDFSDatatype && range.isAnonymous()) {
                    range.delete();
                }
                getEditedProperty().setRange(datatype);
            }
        }
    }


    @Override
	public void setValues(Collection collection) {
        refillAll();
    }


    private void updateFacetsPanel() {
        if (ProtegeSettingsPanel.isUserDefinedDatatypesSupported(getOWLModel())) {
            RDFResource range = getEditedProperty().getRange();
            if (facetsPanel != null) {
                northPanel.remove(facetsPanel);
                mainPanel.revalidate();
            }
            if (range instanceof RDFSDatatype) {
                RDFSDatatype datatype = (RDFSDatatype) range;
                facetsPanel = createFacetsPanel(datatype);
                if (facetsPanel != null) {
                    northPanel.add(BorderLayout.SOUTH, facetsPanel);
                    mainPanel.revalidate();
                    facetsPanel.update(datatype);
                }
            }
        }
    }
    
    @Override
	public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OwlMetaProjectConstants.OPERATION_PROPERTY_TAB_WRITE);
    	classesComponent.setEnabled(enabled);
    	dataRangeComponent.setEnabled(enabled);
    	super.setEnabled(enabled);
    };
}
