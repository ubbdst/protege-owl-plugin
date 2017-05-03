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

package edu.stanford.smi.protegex.owl.ui.components.triples;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.metadata.AnnotationsWidgetPlugin;

/**
 * A TableModel to display property values for a given subject.
 * Each row in this table represents a single triple.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TriplesTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 4888649374752349462L;
    
    /*
     * This is a hacky mechanism to allow a readOnly property value to be edited while a property value is being created. 
     */
    private boolean allowReadOnlyEdit = false;
    

    /**
     * The list of Properties currently displayed
     */
    private ArrayList<RDFProperty> properties = new ArrayList<RDFProperty>();

    /**
     * The resource being annotated
     */
    private RDFResource subject;

    private JTable table;

    /**
     * A FrameListener that detects any changes in own slot values of annotation
     * properties and then updates the table model accordingly.
     */
    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        @Override
		@SuppressWarnings("unchecked")
        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            if (isRelevantProperty(property)) {
                updateValues();
            }
        }
    };


    /**
     * The values currently displayed in each row (either plain values or RDFObjects)
     */
    private ArrayList values = new ArrayList();

    public final static int COL_PROPERTY = 0;

    public final static int COL_VALUE = 1;


    public TriplesTableModel() {
    }


    public TriplesTableModel(RDFResource subject) {
        this.subject = subject;
        if (subject != null) {
            subject.addPropertyValueListener(valueListener);
        }
        refill();
    }


    public int addRow(RDFProperty property) {
        Object value = createDefaultValue(property);
        if (value != null) {
            return addRow(property, value);
        }
        else {
            return -1;
        }
    }


    public int addRow(RDFProperty property, Object value) {
        if (!subject.hasPropertyValue(property, value)) {
            subject.addPropertyValue(property, value);
        }
        return getPropertyValueRow(property, value);
    }


    @SuppressWarnings("unchecked")
    private Object createDefaultValue(RDFProperty property) {
        for (Iterator<AnnotationsWidgetPlugin> it = TriplesComponent.plugins(); it.hasNext();) {
            AnnotationsWidgetPlugin plugin = it.next();
            if (plugin.canEdit(subject, property, null)) {
                Object defaultValue = plugin.createDefaultValue(subject, property);
                if (defaultValue != null) {
                    return defaultValue;
                }
            }
        }
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            return ((RDFSDatatype) range).getDefaultValue();
        }
        else if (range instanceof OWLDataRange) {
            OWLDataRange dataRange = (OWLDataRange) range;
            RDFList oneOf = dataRange.getOneOf();
            Object firstValue = null;
            if (oneOf != null) {
                Collection values = oneOf.getValues();
                if (!values.isEmpty()) {
                    firstValue = values.iterator().next();
                }
            }
            return firstValue;
        }
        else if (property instanceof OWLObjectProperty) {
            return null;
        }
        else {
            return "";
        }
    }


    private Object createNewValue(RDFProperty property, String text, String language) {
        Object newValue = null;
        if (language == null || language.trim().length() == 0) {
            newValue = text;
        }
        else {
            newValue = property.getOWLModel().createRDFSLiteralOrString(text, language);
        }
        return newValue;
    }


    public void deleteRow(int rowIndex) {
        RDFProperty property = getPredicate(rowIndex);
        Object value = getValue(rowIndex);
        subject.removePropertyValue(property, value);
    }


    public void dispose() {
        if (subject != null) {
            subject.removePropertyValueListener(valueListener);
        }
    }


    @Override
	@SuppressWarnings("unchecked")
    public Class getColumnClass(int column) {
        if (column == COL_PROPERTY) {
            return RDFProperty.class;
        }
        else if (column == COL_VALUE) {
            return Object.class;
        }
        else if (hasTypeColumn() && column == COL_VALUE + 1) {
            return RDFResource.class;
        }
        else {
            return String.class;
        }
    }


    public int getColumnCount() {
        return hasTypeColumn() ? 4 : 3;
    }


    @Override
	public String getColumnName(int column) {
        if (column == COL_PROPERTY) {
            return "Property";
        }
        else if (column == COL_VALUE) {
            return "Value";
        }
        else if (isTypeColumn(column)) {
            return "Type";
        }
        else {
            return "Lang";
        }
    }


    public Object getDisplayValue(int rowIndex) {
        Object value = values.get(rowIndex);
        if (value instanceof RDFSLiteral) {
            return ((RDFSLiteral) value).getString();
        }
        return value;
    }


    public String getLanguage(int row) {
        Object value = getValue(row);
        if (value instanceof RDFSLiteral) {
            return ((RDFSLiteral) value).getLanguage();
        }
        return null;
    }


    public OWLModel getOWLModel() {
        return subject.getOWLModel();
    }


    public RDFProperty getPredicate(int rowIndex) {
        return properties.get(rowIndex);
    }


    public int getPropertyValueRow(RDFProperty property, Object value) {
        for (int i = 0; i < properties.size(); i++) {
            RDFProperty s = getPredicate(i);
            if (s.equals(property)) {
                if (value == null && getValue(i) == null) {
                    return i;
                }
                else if (value != null && value.equals(getValue(i))) {
                    return i;
                }
            }
        }
        return -1;
    }


    protected Collection<RDFProperty> getRelevantProperties() {    	
        OWLModel owlModel = subject.getOWLModel();
        return owlModel.getRDFProperties(); //show everything!
    }


    public int getRowCount() {
        return properties.size();
    }


    public RDFResource getSubject() {
        return subject;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_PROPERTY) {
            return getPredicate(rowIndex);
        }
        else if (columnIndex == COL_VALUE) {
            return getDisplayValue(rowIndex);
        }
        else if (isTypeColumn(columnIndex)) {
            Object value = getValue(rowIndex);
            if (value instanceof RDFResource) {
                return ((RDFResource) value).getRDFType();
            }
            else if (value instanceof RDFSLiteral) {
                return ((RDFSLiteral) value).getDatatype();
            }
            else {
                RDFSLiteral literal = DefaultRDFSLiteral.create(getOWLModel(), value);
                return literal.getDatatype();
            }
        }
        else {
            return getLanguage(rowIndex);
        }
    }


    public Object getValue(int rowIndex) {
        return values.get(rowIndex);
    }


    protected boolean hasTypeColumn() {
        return true;
    }

    public void setAllowReadOnlyEdit(boolean allowReadOnlyEdit) {
        this.allowReadOnlyEdit = allowReadOnlyEdit;
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
        RDFProperty property = getPredicate(rowIndex);
        if (!allowReadOnlyEdit && property.isReadOnly()) {
            return false;
        }
        if (columnIndex != COL_PROPERTY) {
            if (columnIndex == COL_VALUE) {
                Object value = getValue(rowIndex);
                for (Iterator<AnnotationsWidgetPlugin> it = TriplesComponent.plugins(); it.hasNext();) {
                    AnnotationsWidgetPlugin plugin = it.next();
                    if (plugin.canEdit(subject, property, value)) {
                        return false;
                    }
                }
                OWLModel owlModel = property.getOWLModel();
                if (!owlModel.getTripleStoreModel().isActiveTriple(subject, property, value)) {
                    return false;
                }
                else if (getDefaultProperties().contains(property) && value == null) {
                    return true;
                }
                else {
                    return property instanceof OWLDatatypeProperty || 
                                (property.isAnnotationProperty() && !(value instanceof Instance));
                }
            }
            else if (isTypeColumn(columnIndex)) {
                return getValueAt(rowIndex, columnIndex) instanceof RDFSDatatype;
            }
            else {
                return property.getOWLModel().getXSDstring().equals(property.getRange()) &&
                        !property.isReadOnly();
            }
        }
        else {
            return false;
        }
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    public boolean isDeleteEnabled(int row) {
        RDFProperty predicate = getPredicate(row);
        Object object = getValue(row);
        return !predicate.isReadOnly() 
                && edu.stanford.smi.protegex.owl.ui.actions.triple.DeleteTripleAction.isSuitable(subject, predicate, object);
    }


    public static boolean isInvalidXMLLiteral(RDFProperty property, Object value) {
        if (XMLSchemaDatatypes.isXMLLiteralSlot(property) && value instanceof String) {
            if (!XMLLiteralType.theXMLLiteralType.isValid((String) value)) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(property.getOWLModel(),
                        "This value is not a valid XML literal:\n" + value);
                return true;
            }
        }
        return false;
    }


    protected boolean isRelevantProperty(RDFProperty property) {
        return true;
    }


    private boolean isTypeColumn(int column) {
        return hasTypeColumn() && column == COL_VALUE + 1;
    }


    @SuppressWarnings("unchecked")
    private void refill() {
        if (subject != null) {
            Collection<RDFProperty> properties = getRelevantProperties();
            RDFProperty[] ss = properties.toArray(new RDFProperty[0]);
            Arrays.sort(ss, new FrameComparator());
            for (RDFProperty property : ss) {
                Collection values = subject.getPropertyValues(property);
		        for (Iterator it = values.iterator(); it.hasNext();) {
		            Object value = it.next();
		            this.properties.add(property);
		            this.values.add(value);
		        }
            }
	        for (RDFProperty curProp : getDefaultProperties()) {
		        if(this.properties.contains(curProp) == false) {
			        this.properties.add(0, curProp);
			        this.values.add(0, null);
		        }
	        }
        }
    }

    private void setDatatype(int row, RDFSDatatype datatype) {
        Object oldValue = getValue(row);
        final String lexicalValue = oldValue.toString();
        RDFSLiteral newValue = datatype.getOWLModel().createRDFSLiteral(lexicalValue, datatype);
        RDFProperty predicate = getPredicate(row);
        subject.removePropertyValue(predicate, oldValue);
        subject.addPropertyValue(predicate, newValue);
    }


    private Object setLanguage(int row, String newLanguage) {
        RDFProperty property = getPredicate(row);
        String text = (String) getDisplayValue(row);
        Object newValue = createNewValue(property, text, newLanguage);
        if (!subject.getPropertyValues(property).contains(newValue)) {
            Object oldValue = getValue(row);
            subject.removePropertyValue(property, oldValue);
            subject.addPropertyValue(property, newValue);
        }
        return newValue;
    }


    public void setSubject(RDFResource instance) {
        RDFResource oldSubject = this.subject;
        if (oldSubject != null) {
            oldSubject.removePropertyValueListener(valueListener);
        }
        this.subject = instance;
        properties.clear();
        values.clear();
        if (instance != null) {
            instance.addPropertyValueListener(valueListener);
            refill();
        }
        fireTableDataChanged();
    }


    public Object setValue(Object aValue, int row) {
        RDFProperty property = getPredicate(row);
        Object oldValue = getValue(row);
        if (oldValue == null || !oldValue.equals(aValue)) {
            try {
                Object newValue;
                String str = aValue.toString();
                OWLModel owlModel = property.getOWLModel();
                if (owlModel.getOWLOntologyProperties().contains(property)) {
                    String message = str + " is not a valid URI.";
                    try {
                        new URI(str);
                    }
                    catch (Exception ex) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, message);
                        return oldValue;
                    }
                    if (!str.startsWith("http://") && !str.startsWith("file:")) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, message);
                        return oldValue;
                    }
                }
                String lang = getLanguage(row);
                if(aValue instanceof RDFSLiteral && ((RDFSLiteral)aValue).getLanguage() != null) {
                    newValue = aValue;
                }
                else if (lang != null) {
                    newValue = createNewValue(property, str, lang);
                }
                else if (oldValue instanceof RDFSLiteral) {
                    RDFSLiteral oldLiteral = (RDFSLiteral) oldValue;
                    newValue = getOWLModel().createRDFSLiteral(str, oldLiteral.getDatatype());
                }
                else if (oldValue instanceof Boolean) {
                    newValue = Boolean.valueOf(str.equals("true"));
                }
                else if (oldValue instanceof Float) {
                    newValue = Float.valueOf(str);
                }
                else if (oldValue instanceof Integer) {
                    newValue = Integer.valueOf(str);
                }
                else {
                    newValue = str;
                }
                if (!subject.getPropertyValues(property).contains(newValue)) {
                	if (oldValue != null) {
                		subject.removePropertyValue(property, oldValue);
                	}
                    subject.addPropertyValue(property, newValue);
                }
                return newValue;
            }
            catch (NumberFormatException ex) {
                // Ignore illegal number format
            }
        }
        return oldValue;
    }


    @Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        setValueAndGetIt(aValue, rowIndex, columnIndex);
    }


    public Object setValueAndGetIt(Object value, int row, int col) {
        if (col == COL_PROPERTY) {
            return null;
        }
        if (col == COL_VALUE) {
            return setValue(value, row);
        }
        else if (isTypeColumn(col)) {
            if (value instanceof RDFSDatatype) {
                RDFSDatatype datatype = (RDFSDatatype) value;
                setDatatype(row, datatype);
                return datatype;
            }
            else {
                return null;
            }
        }
        else {
            return setLanguage(row, (String) value);
        }
    }


    /**
     * Removes all rows containing a given property and then re-adds them with the
     * most recent values.
     */
    void updateValues() {
        int index = -1;
        if (table != null) {
            index = table.getSelectedRow();
        }
        properties.clear();
        values.clear();
        refill();
        fireTableDataChanged();
        if (table != null && index >= 0 && index < getRowCount()) {
            table.getSelectionModel().setSelectionInterval(index, index);
        }
    }


    public void setTable(TriplesTable table) {
        this.table = table;
    }

	public Collection<RDFProperty> getDefaultProperties() {
		return Collections.emptyList();
	}
}
