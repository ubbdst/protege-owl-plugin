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

package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestLibrary;
import edu.stanford.smi.protegex.owl.testing.OWLTestManager;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestSettingsTableModel extends AbstractTableModel {

    public final static int COL_ACTIVATED = 0;

    public final static int COL_NAME = 1;

    public final static int COL_COUNT = 2;

    /**
     * A list of either Class objects (for the TestClass) or Strings
     * (to represent group headers).
     */
    private List testClasses = new ArrayList();

    private OWLTestManager testManager;


    public OWLTestSettingsTableModel(OWLTestManager testManager) {
        this.testManager = testManager;
        Class[] classes = OWLTestLibrary.getOWLTestClasses();
        Arrays.sort(classes, new Comparator() {
            public int compare(Object o1, Object o2) {
                Class class1 = (Class) o1;
                OWLTest test1 = OWLTestLibrary.getOWLTest(class1);
                Class class2 = (Class) o2;
                OWLTest test2 = OWLTestLibrary.getOWLTest(class2);
                String group1 = test1.getGroup();
                if (group1 == null) {
                    group1 = "";
                }
                String group2 = test2.getGroup();
                if (group2 == null) {
                    group2 = "";
                }
                int groupCompare = group1.compareTo(group2);
                if (groupCompare == 0) {
                    return test1.getName().compareTo(test2.getName());
                }
                else {
                    return groupCompare;
                }
            }
        });

        // Fill list grouped by group separators
        String previousGroup = null;
        for (int i = 0; i < classes.length; i++) {
            Class c = classes[i];
            OWLTest test = OWLTestLibrary.getOWLTest(c);
            String group = test.getGroup();
            if (group != null && !group.equals(previousGroup)) {
                testClasses.add(group);
                previousGroup = group;
            }
            testClasses.add(c);
        }
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_ACTIVATED) {
            return Boolean.class;
        }
        else if (columnIndex == COL_NAME) {
            return String.class;
        }
        return null;
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        if (column == COL_ACTIVATED) {
            return "Activated";
        }
        else if (column == COL_NAME) {
            return "Test Class Name";
        }
        return null;
    }


    public OWLTest getOWLTest(Class clazz) {
        OWLTest[] tests = testManager.getOWLTests();
        for (int i = 0; i < tests.length; i++) {
            OWLTest test = tests[i];
            if (test.getClass().equals(clazz)) {
                return test;
            }
        }
        return null;
    }


    public Class getOWLTestClass(int rowIndex) {
        return (Class) testClasses.get(rowIndex);
    }


    public String getOWLTestClassName(int rowIndex) {
        OWLTest test = getOWLTest(getOWLTestClass(rowIndex));
        return test.getName();
    }


    public int getRowCount() {
        return testClasses.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (isSeparator(rowIndex)) {
            if (columnIndex == COL_ACTIVATED) {
                String groupName = (String) testClasses.get(rowIndex);
                return Boolean.valueOf(testManager.isOWLTestGroupEnabled(groupName));
            }
            else if (columnIndex == COL_NAME) {
                return (String) testClasses.get(rowIndex);
            }
        }
        else {
            if (columnIndex == COL_ACTIVATED) {
                return new Boolean(isTestActivated(rowIndex));
            }
            else if (columnIndex == COL_NAME) {
                Class c = getOWLTestClass(rowIndex);
                OWLTest test = OWLTestLibrary.getOWLTest(c);
                String prefix = test.getGroup() == null ? "" : "      ";
                return prefix + test.getName();
            }
        }
        return null;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COL_ACTIVATED;
    }


    public boolean isSeparator(int rowIndex) {
        return testClasses.get(rowIndex) instanceof String;
    }


    private boolean isTestActivated(int rowIndex) {
        return getOWLTest(getOWLTestClass(rowIndex)) != null;
    }


    private void setTestActivated(int rowIndex, boolean value) {
        Class clazz = getOWLTestClass(rowIndex);
        if (value) {
            OWLTest test = OWLTestLibrary.getOWLTest(clazz);
            if (test != null) {
                testManager.addOWLTest(test);
            }
        }
        else {
            OWLTest test = getOWLTest(clazz);
            if (test != null) {
                testManager.removeOWLTest(test);
            }
        }
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COL_ACTIVATED) {
            boolean value = ((Boolean) aValue).booleanValue();
            if (isSeparator(rowIndex)) {
                String groupName = getGroupName(rowIndex);
                testManager.setOWLTestGroupEnabled(groupName, value);
                fireTableDataChanged();
            }
            else {
                setTestActivated(rowIndex, value);
            }
        }
    }


    private String getGroupName(int rowIndex) {
        return (String) testClasses.get(rowIndex);
    }
}
