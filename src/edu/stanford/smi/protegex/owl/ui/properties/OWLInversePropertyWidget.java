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

package edu.stanford.smi.protegex.owl.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.Action;
import javax.swing.JList;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.CreateAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.RemoveAction;
import edu.stanford.smi.protege.util.ViewAction;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An adjusted version of the InverseSlotWidget that honors the OWL look and feel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLInversePropertyWidget extends AbstractPropertyWidget {

    private ResourceSelectionAction addAction = new ResourceSelectionAction("Set inverse property...",
            OWLIcons.getAddIcon(OWLIcons.OWL_OBJECT_PROPERTY), false) {

        public void resourceSelected(RDFResource resource) {
        	try {
                beginTransaction("Set inverse property of " + getEditedResource().getBrowserText() + 
                		" to " + resource.getBrowserText(), getEditedResource().getName());
                final OWLProperty inverseProperty = (OWLProperty) resource;
                setInverseProperty(inverseProperty);
                commitTransaction();				
			} catch (Exception e) {
				rollbackTransaction();
				// TODO: handle exception
			}
        }


        public RDFResource pickResource() {
            Collection possibleInverses = getPossibleInverses();
            if (possibleInverses.isEmpty()) {
                String text = "There are no existing properties which can be used as an inverse.";
                ProtegeUI.getModalDialogFactory().showMessageDialog(getOWLModel(), text);
                return null;
            }
            else {
                return (OWLObjectProperty) ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(OWLInversePropertyWidget.this, getOWLModel(),
                        possibleInverses, "Select inverse property");
            }
        }


        public Collection getSelectableResources() {
            return getPossibleInverses();
        }
    };

    private AllowableAction createAction;

    private FrameListener frameListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            updateWidget();
        }
    };

    private JList list;

    private AllowableAction removeAction;

    private AllowableAction viewAction;

    private RDFProperty createInverseProperty() {
        OWLProperty inverseProperty = null;
        try {
            OWLProperty forwardProperty = (OWLProperty) getEditedResource();
            //TT: Maybe this should be on the inverse property, not on the forward one
            beginTransaction("Create inverse property for " + forwardProperty.getName(), forwardProperty.getName());
            String forwardName = forwardProperty.getName();
            String forwardNamespace = NamespaceUtil.getNameSpace(forwardName);
            String forwardLocalName = NamespaceUtil.getLocalName(forwardName);
            String propertyName = forwardNamespace + "inverse_of_" + forwardLocalName;
            if (getKnowledgeBase().getFrame(propertyName) != null) {
                int i = 0;
                while (getKnowledgeBase().getFrame(propertyName + "_"  + i) != null) {
                    i++;
                }
                propertyName  = propertyName + "_"  + i;
            }
            Collection inverseSuperproperties = getSuperpropertyInverses(forwardProperty);
            RDFSNamedClass type = (RDFSNamedClass) forwardProperty.getRDFType();
            inverseProperty = (OWLProperty) getKnowledgeBase().createSlot(propertyName, type, inverseSuperproperties, true);
            inverseProperty.setFunctional(false);
            setInverseProperty(inverseProperty);
            if (forwardProperty.isAnnotationProperty() && !inverseProperty.isAnnotationProperty()) {
                inverseProperty.addProtegeType(forwardProperty.getOWLModel().getOWLAnnotationPropertyClass());
            }
            commitTransaction();
        }
        catch (Exception ex) {
        	rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
        return inverseProperty;
    }


    private Collection getSuperpropertyInverses(RDFProperty forwardProperty) {
        Collection superpropertyInverses = new LinkedHashSet();
        Iterator i = forwardProperty.getSuperproperties(true).iterator();
        while (i.hasNext()) {
            RDFProperty superproperty = (RDFProperty) i.next();
            RDFProperty superpropertyInverse = superproperty.getInverseProperty();
            if (superpropertyInverse != null) {
                superpropertyInverses.add(superpropertyInverse);
            }
        }
        return superpropertyInverses;
    }


    private JList createList() {
        JList list = ComponentFactory.createSingleItemList(getViewAction());
        list.setCellRenderer(FrameRenderer.createInstance());
        return list;
    }


    private Action getCreateAction() {
        if (createAction == null) {
            createAction = new CreateAction("Create new inverse property") {
                public void onCreate() {
                    RDFProperty property = createInverseProperty();
                    getProject().show(property);
                }
            };
        }
        return createAction;
    }


    protected Collection getPossibleInverses() {
        Collection possibleInverses = new ArrayList();
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        Iterator it = owlModel.getVisibleUserDefinedOWLProperties().iterator();
        while (it.hasNext()) {
            RDFProperty property = (RDFProperty) it.next();
            if (property instanceof OWLObjectProperty) {
                RDFProperty inverseProperty = property.getInverseProperty();
                if (inverseProperty == null) {
                    possibleInverses.add(property);
                }
            }
        }
        return possibleInverses;
    }


    private Action getRemoveAction() {
        if (removeAction == null) {
            removeAction = new RemoveAction("Remove inverse property", this) {
                public void onRemove(Object o) {
                    setInverseProperty(null);
                }
            };
        }
        return removeAction;
    }


    public Collection getSelection() {
        return getValues();
    }


    public Collection getValues() {
        return ComponentUtilities.getListValues(list);
    }


    private Action getViewAction() {
        if (viewAction == null) {
            viewAction = new ViewAction("View inverse property", this) {
                public void onView(Object o) {
                    RDFProperty property = (RDFProperty) o;
                    getProject().show(property);
                }
            };
        }
        return viewAction;
    }


    public void initialize() {

        list = createList();
        LabeledComponent c = new LabeledComponent(getLabel(), list);
        c.addHeaderButton(getViewAction());
        c.addHeaderButton(getCreateAction());
        addAction.activateComboBox(c.addHeaderButton(addAction));
        c.addHeaderButton(getRemoveAction());
        add(c);
        setPreferredColumns(2);
        setPreferredRows(1);

        LabeledComponent oldLabeledComponent = (LabeledComponent) getComponent(0);
        List actions = new ArrayList(oldLabeledComponent.getHeaderButtonActions());
        createAction.putValue(Action.SMALL_ICON, OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_OBJECT_PROPERTY));
        createAction.putValue(Action.SHORT_DESCRIPTION, "Create new inverse property");
        Action addAction = (Action) actions.get(2);
        addAction.putValue(Action.SMALL_ICON, OWLIcons.getAddIcon(OWLIcons.OWL_OBJECT_PROPERTY));
        addAction.putValue(Action.SHORT_DESCRIPTION, "Assign existing property");
        Action removeAction = (Action) actions.get(3);
        removeAction.putValue(Action.SMALL_ICON, OWLIcons.getRemoveIcon(OWLIcons.OWL_OBJECT_PROPERTY));
        removeAction.putValue(Action.SHORT_DESCRIPTION, "Unassign current inverse property");
        OWLLabeledComponent lc = new OWLLabeledComponent("Inverse",
                oldLabeledComponent.getCenterComponent());
        lc.addHeaderButton(createAction);
        lc.addHeaderButton(addAction);
        lc.addHeaderButton(removeAction);
        remove(oldLabeledComponent);
        add(lc);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(OWLNames.Slot.INVERSE_OF);
    }


    private void setDomain(RDFProperty property, Collection domain) {
        Iterator i = domain.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            if (cls instanceof RDFSNamedClass) {
                property.addUnionDomainClass((RDFSClass) cls);
            }
        }
    }


    public void setEditable(boolean b) {
        updateWidget();
    }


    public void setInstance(Instance newInstance) {
        Instance oldInstance = getEditedResource();
        if (oldInstance != null && !isSlotAtCls()) {
            oldInstance.removeFrameListener(frameListener);
        }
        super.setInstance(newInstance);
        if (newInstance != null && !isSlotAtCls()) {        	
            newInstance.addFrameListener(frameListener);
        }
    }


    private void setInverseProperty(RDFProperty property) {
        if (property == null) {
            ComponentUtilities.setListValues(list, Collections.EMPTY_LIST);
        }
        else {
            ComponentUtilities.setListValues(list, Collections.singleton(property));
        }
        valueChanged();
        if (getEditedResource() instanceof OWLObjectProperty) {
            OWLObjectProperty objectSlot = (OWLObjectProperty) getEditedResource();
            objectSlot.setSymmetric(objectSlot.equals(property));
        }
    }


    public void setValues(Collection c) {
        ComponentUtilities.setListValues(list, c);
    }


    private void updateWidget() {
        RDFResource resource = getEditedResource();
        boolean editable = !isSlotAtCls() && resource.isEditable();
        if (editable && resource instanceof Slot) {
            ValueType type = ((Slot) resource).getValueType();
            editable = equals(type, ValueType.INSTANCE) || equals(type, ValueType.CLS);
        }
        setEnabled(editable);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OwlMetaProjectConstants.OPERATION_PROPERTY_TAB_WRITE);
        createAction.setAllowed(enabled);
        addAction.setEnabled(enabled);
        removeAction.setAllowed(enabled);    	
    	super.setEnabled(enabled);
    }
    
    @Override
    public void dispose() {
    	Instance resource = getEditedResource();
    	 if (resource != null && !isSlotAtCls()) {         	
         	resource.removeFrameListener(frameListener);
         }
    	    	
    	super.dispose();
    }
}
