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

package edu.stanford.smi.protegex.owl.ui.matrix;

import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Comparator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AnnotationPropertyMatrixColumn implements EditableMatrixColumn, SortableMatrixColumn {

    private RDFProperty property;


    public AnnotationPropertyMatrixColumn(RDFProperty property) {
        this.property = property;
    }


    public RDFProperty getAnnotationProperty() {
        return property;
    }


    public TableCellRenderer getCellRenderer() {
        return new FrameRenderer() {
            public void load(Object value) {
                if (value instanceof RDFResource) {
                    RDFResource instance = (RDFResource) value;
                    value = instance.getPropertyValue(property);
                }
                if (value == null) {
                    clear();
                }
                else {
                    super.load(value);
                }
            }
        };
    }


    public String getName() {
        return property.getName();
    }


    public int getWidth() {
        return 250;
    }


    public Comparator getSortComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                final RDFResource a = (RDFResource) o1;
                final RDFResource b = (RDFResource) o2;
                final Object valueA = a.getPropertyValue(property);
                final Object valueB = b.getPropertyValue(property);
                if (valueA instanceof Comparable) {
                    if (valueB == null) {
                        return -1;
                    }
                    else {
                        int c = ((Comparable) valueA).compareTo(valueB);
                        if (c != 0) {
                            return c;
                        }
                    }
                }
                else if (valueB != null) {
                    return 1;
                }
                return a.getName().compareTo(b.getName());
            }
        };
    }


    public TableCellEditor getTableCellEditor() {
        JTextField textField = new JTextField();
        return new DefaultCellEditor(textField) {
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                RDFResource instance = (RDFResource) value;
                value = instance.getPropertyValue(property);
                if (value == null) {
                    value = "";
                }
                return super.getTableCellEditorComponent(table, instance.getLocalName(), isSelected, row, column);
            }
        };
    }


    public boolean isCellEditable(RDFResource instance) {
        return instance.getPropertyValues(property).size() < 2;
    }


    public void setValueAt(RDFResource instance, Object value) {
        if (((String) value).length() == 0) {
            value = null;
        }
        instance.setPropertyValue(property, value);
    }
}
