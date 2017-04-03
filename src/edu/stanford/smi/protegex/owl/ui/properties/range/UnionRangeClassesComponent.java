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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.PropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

/**
 * A component that can be used to edit arbitrary class expressions as rdfs:range values.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class UnionRangeClassesComponent extends JComponent {

    private OWLModel owlModel;

    private UnionRangeClassesTable table;

    private UnionRangeClassesTableModel tableModel;


    private ResourceSelectionAction addAction = new ResourceSelectionAction("Specialise Range",
                                                                            OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS), true) {

        public void resourceSelected(RDFResource resource) {
            final RDFSClass classToAdd = (RDFSClass) resource;
            if (classToAdd.equals(resource.getOWLModel().getOWLThingClass())) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                                                         "The class " + classToAdd.getBrowserText() +
                                                                         " cannot be added to the range.\n" +
                                                                         "Please set the range to empty to get this behavior.",
                                                                         "Invalid Class");
                return;
            }
            RDFProperty property = tableModel.getEditedProperty();

            if (!(property instanceof OWLProperty)) {
                OWLModel owlModel = property.getOWLModel();
                if (!ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Union_Classes)) {
                    Collection dd = property.getUnionRangeClasses();
                    if (dd.size() > 0 && property.getRange(false) != null) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                                                                 "In pure RDF, rdf:Properties can only have one class in their\n" +
                                                                                 "(union) range.  You need to select a different language profile.");
                        return;
                    }
                }
            }

            if (property.getSuperpropertyCount() > 0) {  // Only subclasses of range allowed
                Collection clses = getUnionRangeClassesOfSuperproperties();
                if (clses.size() > 0) {
                    boolean one = false;
                    for (Iterator it = clses.iterator(); it.hasNext();) {
                        RDFSClass cls = (RDFSClass) it.next();
                        if (cls.equals(resource) || classToAdd.getSuperclasses(true).contains(cls)) {
                            one = true;
                        }
                    }
                    if (!one) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                                                                 "The class " + classToAdd.getBrowserText() +
                                                                                 " cannot be added to the range\n" +
                                                                                 "of " + property.getBrowserText() + " because it does not narrow the classes\n" +
                                                                                 "from the range of its super-properties.");
                        return;
                    }
                }
            }
            tableModel.addRow(classToAdd, table.getSelectedRow());
        }


        @Override
		public Collection getSelectableResources() {
            RDFProperty property = tableModel.getEditedProperty();
            Collection<Cls> clses = new HashSet();
            final Collection allowedClses = property.getUnionRangeClasses();
            final Cls rootCls = property.getOWLModel().getOWLThingClass();
            if (property.getSuperpropertyCount() > 0 &&
                !allowedClses.isEmpty() && !allowedClses.contains(rootCls)) {
                // Only subclasses of inherited range are allowed
                for (Iterator it = allowedClses.iterator(); it.hasNext();) {
                    Cls cls = (Cls) it.next();
                    clses.add(cls);
                    clses.addAll(cls.getSubclasses());
                }
                final Slot valueTypeSlot = ((KnowledgeBase) property.getOWLModel()).getSlot(Model.Slot.VALUE_TYPE);
                clses.removeAll(((Slot) property).getDirectOwnSlotValues(valueTypeSlot));
            }
            else {
                clses = OWLUtil.getSelectableNamedClses(owlModel);
                Cls editedCls = tableModel.getEditedCls();
                clses.remove(editedCls); // Can never add itself
                clses.removeAll(tableModel.getValues());
            }
            clses.remove(rootCls);
            Cls[] cs = clses.toArray(new Cls[0]);
            Arrays.sort(cs, new FrameComparator());
            return Arrays.asList(cs);
        }


        @Override
		public Collection pickResources() {
            RDFSNamedClass rdfsClass = owlModel.getRDFSNamedClassClass();
            boolean rdfsClassWasVisible = rdfsClass.isVisible();
            RDFSNamedClass owlClass = owlModel.getOWLNamedClassClass();
            boolean owlClassWasVisible = owlClass.isVisible();
            if (!rdfsClassWasVisible && ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.OWL_Full)) {
                rdfsClass.setVisible(true);
                owlClass.setVisible(true);
            }

            RDFProperty property = tableModel.getEditedProperty();
            Collection rootClasses = new HashSet();
            rootClasses.add(owlModel.getOWLThingClass());

            if (property.getSuperpropertyCount() > 0) {
                for (Iterator it = property.getSuperproperties(true).iterator(); it.hasNext();) {
                    RDFProperty superProp = (RDFProperty) it.next();
                    rootClasses.addAll(superProp.getRanges(false));
                }
                if (rootClasses.size() > 1){
                    rootClasses.remove(owlModel.getOWLThingClass());
                }
            }

            Collection results = ProtegeUI.getSelectionDialogFactory().selectClasses(table, owlModel,
                                                                                     rootClasses,
                                                                                     "Select named class(es) to add");
            rdfsClass.setVisible(rdfsClassWasVisible);
            owlClass.setVisible(owlClassWasVisible);
            return results;
        }
    };


    private Action createAction = new AbstractAction("Specialise Range using OWL expression",
                                                     OWLIcons.getCreateIcon(OWLIcons.ANONYMOUS_OWL_CLASS)) {

        public void actionPerformed(ActionEvent e) {
            table.createAndEditRow();
        }
    };


    private OWLTableAction deleteAction =
            new AbstractOWLTableAction("Remove from Range", OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {

                public void actionPerformed(ActionEvent e) {
                    int selIndex = table.getSelectedRow();
                    if (selIndex >= 0) {
                        OWLTableModel tableModel = (OWLTableModel) table.getModel();
                        if (tableModel.isCellEditable(selIndex, tableModel.getSymbolColumnIndex())) {
                            tableModel.deleteRow(selIndex);
                            table.getSelectionModel().setSelectionInterval(selIndex, selIndex);
                        }
                    }
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    //return cls != null && tableModel.isDeleteEnabledFor(cls) && isEnabled();
                	RDFProperty property = tableModel.getEditedProperty();
                	
                	return cls != null && tableModel.isDeleteEnabledFor(cls) && (property != null && property.isEditable());
                }
            };

    private OWLTableAction viewAction = new AbstractOWLTableAction("View class",
                                                                   OWLIcons.getViewIcon()) {

        public void actionPerformed(ActionEvent e) {
            Cls cls = table.getSelectedCls();
            table.getOWLModel().getProject().show(cls);
        }


        public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
            return cls instanceof RDFSNamedClass;
        }
    };


    public UnionRangeClassesComponent(OWLModel owlModel, PropertyWidget propertyWidget) {

        this.owlModel = owlModel;

        tableModel = new UnionRangeClassesTableModel(propertyWidget);
        table = new UnionRangeClassesTable(tableModel, owlModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());

        table.registerAction(viewAction);
        table.registerAction(deleteAction);
        LabeledComponent lc = new OWLLabeledComponent("Range  " + DefaultOWLUnionClass.OPERATOR, scrollPane, true, false);
        WidgetUtilities.addViewButton(lc, viewAction);
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.OWL_Lite)) {
            lc.addHeaderButton(createAction);
        }
        lc.addHeaderButton(addAction);
        lc.addHeaderButton(deleteAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    private Collection getUnionRangeClassesOfSuperproperties() {
        Collection result = new HashSet();
        for (Iterator it = tableModel.getEditedProperty().getSuperproperties(true).iterator(); it.hasNext();) {
            RDFProperty superproperty = (RDFProperty) it.next();
            result.addAll(superproperty.getUnionRangeClasses());
        }
        return result;
    }


    public void dispose() {
        ((OWLTableModel) table.getModel()).dispose();
    }


    public JComponent getComponent() {
        return this;
    }

/*
    public void setEditable(boolean b) {
        super.setEnabled(b);
        if (getComponentCount() > 0) {
            LabeledComponent lc = (LabeledComponent) getComponent(0);
            for (Iterator it = lc.getHeaderButtonActions().iterator(); it.hasNext();) {
                Action action = (Action) it.next();
                action.setEnabled(b);
            }
            table.setEnabled(b);
            table.enableActions();
        }
    }
*/

    public void refill() {
        table.hideSymbolPanel();
        tableModel.refill();
    }
    
    @Override
	public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(owlModel, OwlMetaProjectConstants.OPERATION_PROPERTY_TAB_WRITE);
    	RDFProperty property = tableModel.getEditedProperty();
    	
    	if (property != null) {
    		addAction.setEnabled(enabled);
    		createAction.setEnabled(enabled);
    		enabled = enabled && property.isEditable(); 
    	}    	
    	//table.setEnabled(enabled);    	
    	deleteAction.setEnabled(enabled);    	
    	super.setEnabled(enabled);
    };
}
