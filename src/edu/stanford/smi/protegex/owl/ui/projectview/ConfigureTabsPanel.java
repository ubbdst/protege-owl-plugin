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

package edu.stanford.smi.protegex.owl.ui.projectview;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.CheckBoxRenderer;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protege.widget.TabWidget;
import edu.stanford.smi.protege.widget.WidgetDescriptorRenderer;
import edu.stanford.smi.protege.widget.WidgetUtilities;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Configure the tab in the application.  This allows the tabs to be enabled and ordered.
 * It also allows for tab specific configuration.
 *
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */

public class ConfigureTabsPanel extends AbstractValidatableComponent {

    private boolean dirty;

    private Project project;

    private ProjectView projectView;

    private JTable table;


    private class MoveTabUp extends AbstractAction {

        MoveTabUp() {
            super("Move selected tab up", Icons.getUpIcon());
        }


        public void actionPerformed(ActionEvent event) {
            int index = table.getSelectedRow();
            if (canMoveUp(index)) {
                getTabModel().moveRow(index, index, index - 1);
                int n = index - 1;
                table.getSelectionModel().setSelectionInterval(n, n);
                dirty = true;
            }
        }
    }

    private class MoveTabDown extends AbstractAction {

        MoveTabDown() {
            super("Move selected tab down", Icons.getDownIcon());
        }


        public void actionPerformed(ActionEvent event) {
            int index = table.getSelectedRow();
            if (canMoveDown(index)) {
                getTabModel().moveRow(index, index, index + 1);
                int n = index + 1;
                table.getSelectionModel().setSelectionInterval(n, n);
                dirty = true;
            }
        }
    }


    public ConfigureTabsPanel(ProjectView projectView) {
        setLayout(new BorderLayout());
        this.projectView = projectView;
        this.project = projectView.getProject();
        table = ComponentFactory.createTable(getConfigureAction());
        table.setModel(createTableModel());
        ComponentUtilities.addColumn(table, new WidgetDescriptorEnableRenderer());
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        ComponentUtilities.addColumn(table, new WidgetDescriptorRenderer(project));
        table.addMouseListener(new ClickListener());
        JScrollPane pane = ComponentFactory.createScrollPane(table);
        pane.setColumnHeaderView(table.getTableHeader());
        pane.setBackground(table.getBackground());
        LabeledComponent c = new LabeledComponent("Tabs", pane);
        c.addHeaderButton(new MoveTabUp());
        c.addHeaderButton(new MoveTabDown());
        add(c);
    }


    private boolean canMoveUp(int index) {
        return index > 0 && isEnabled(index);
    }


    private boolean canMoveDown(int index) {
        boolean canMoveDown = 0 <= index && index < table.getRowCount() - 1;
        if (canMoveDown) {
            canMoveDown = isEnabled(index) && canEnable(index + 1);
        }
        return canMoveDown;
    }


    public boolean getRequiresReloadUI() {
        return dirty;
    }


    private boolean isEnabled(int row) {
        Boolean b = (Boolean) getTabModel().getValueAt(row, 0);
        return b.booleanValue();
    }


    private void setEnabled(int row, boolean enabled) {
        getTabModel().setValueAt(Boolean.valueOf(enabled), row, 0);
    }


    private class ClickListener extends MouseAdapter {

        public void mousePressed(MouseEvent event) {
            Point p = event.getPoint();
            int col = table.columnAtPoint(p);
            if (col == 0) {
                int row = table.rowAtPoint(p);
                if (isEditable(row)) {
                    boolean b = isEnabled(row);
                    setEnabled(row, !b);
                    dirty = true;
                }
            }
        }
    }


    private boolean isEditable(int row) {
        WidgetDescriptor d = getDescriptor(row);
        Collection strings = new ArrayList();
        return WidgetUtilities.isSuitableTab(d.getWidgetClassName(), project, strings);
    }


    private TableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Visible");
        model.addColumn("Tab Widget");
        boolean all = true; // _filterComboBox.getSelectedItem().equals(ALL);

        Collection tabDescriptors = new ArrayList(project.getTabWidgetDescriptors());
        tabDescriptors = sort(tabDescriptors);
        Iterator i = tabDescriptors.iterator();
        while (i.hasNext()) {
            WidgetDescriptor d = (WidgetDescriptor) i.next();
            if (all || canEnable(d)) {
                model.addRow(new Object[]{Boolean.valueOf(d.isVisible()), d});
            }
        }
        return model;
    }


    private Collection sort(Collection descriptors) {
        List sortedDescriptors = new ArrayList(descriptors);
        int i;
        for (i = 0; i < sortedDescriptors.size(); ++i) {
            WidgetDescriptor d = (WidgetDescriptor) sortedDescriptors.get(i);
            if (!d.isVisible()) {
                break;
            }
        }
        Collections.sort(sortedDescriptors, new WidgetDescriptorComparator());
        return sortedDescriptors;
    }


    private Action getConfigureAction() {
        return new AbstractAction("Configure") {
            public void actionPerformed(ActionEvent event) {
                int row = table.getSelectedRow();
                WidgetDescriptor d = getDescriptor(row);
                if (d.isVisible()) {
                    TabWidget widget = ProjectManager.getProjectManager().getCurrentProjectView().getTabByClassName(
                            d.getWidgetClassName());
                    widget.configure();
                }
            }
        };
    }


    private DefaultTableModel getTabModel() {
        return (DefaultTableModel) table.getModel();
    }


    public void saveContents() {
        if (dirty) {
            Collection tabWidgetDescriptors = new ArrayList();
            for (int row = 0; row < getTabModel().getRowCount(); ++row) {
                boolean isEnabled = isEnabled(row);
                WidgetDescriptor descriptor = getDescriptor(row);
                descriptor.setVisible(isEnabled);
                tabWidgetDescriptors.add(descriptor);
            }
            project.setTabWidgetDescriptorOrder(tabWidgetDescriptors);
        }
    }


    public boolean validateContents() {
        return true;
    }


    private boolean canEnable(String className) {
        return WidgetUtilities.isSuitableTab(className, project, new ArrayList()) &&
                !OWLUI.isUnsuitableTab(className);
    }


    private WidgetDescriptor getDescriptor(int row) {
        return (WidgetDescriptor) getTabModel().getValueAt(row, 1);
    }


    private boolean canEnable(int row) {
        WidgetDescriptor d = getDescriptor(row);
        return canEnable(d);
    }


    private boolean canEnable(WidgetDescriptor d) {
        return canEnable(d.getWidgetClassName());
    }


    class WidgetDescriptorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            WidgetDescriptor wd1 = (WidgetDescriptor) o1;
            WidgetDescriptor wd2 = (WidgetDescriptor) o2;
            boolean isEnabled1 = wd1.isVisible();
            boolean isEnabled2 = wd2.isVisible();
            int compare;
            if (isEnabled1) {
                compare = isEnabled2 ? 0 : -1;
            }
            else {
                compare = isEnabled2 ? +1 : 0;
            }
            if (!isEnabled1 && !isEnabled2) {
                String n1 = wd1.getWidgetClassName();
                String n2 = wd2.getWidgetClassName();
                boolean canEnable1 = canEnable(n1);
                boolean canEnable2 = canEnable(n2);
                if (canEnable1) {
                    compare = canEnable2 ? 0 : -1;
                }
                else {
                    compare = canEnable2 ? +1 : 0;
                }
                if (compare == 0) {
                    String sn1 = StringUtilities.getShortClassName(n1);
                    String sn2 = StringUtilities.getShortClassName(n2);
                    compare = sn1.compareToIgnoreCase(sn2);
                }
            }
            return compare;
        }

    }

    class WidgetDescriptorEnableRenderer extends CheckBoxRenderer {

        private final Component EMPTY;


        {
            EMPTY = new JPanel() {
                public boolean isOpaque() {
                    return false;
                }
            };
        }


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean b,
                                                       int row, int col) {
            Component c;
            if (canEnable(row)) {
                c = super.getTableCellRendererComponent(table, value, isSelected, b, row, col);
            }
            else {
                c = EMPTY;
            }
            return c;
        }
    }
}
