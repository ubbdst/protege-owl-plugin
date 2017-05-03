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

package edu.stanford.smi.protegex.owl.ui.properties.domain;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protege.util.ViewAction;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

/**
 * A Widget for the domain of an RDFProperty.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDomainWidget extends AbstractPropertyWidget {

    private AddClassToDomainAction addAction;

    private AllowableAction removeAction;

    private OWLDomainTable table;

    private OWLDomainTableModel tableModel;
    
    private FrameListener frameListener = new FrameAdapter(){
    	@Override
    	public void frameReplaced(FrameEvent event) {
    		Frame newFrame = event.getNewFrame();    		
    		setInstance((RDFResource)newFrame);
    	}
    };

    public void initialize() {

        tableModel = new OWLDomainTableModel(null);
        table = new OWLDomainTable(tableModel);
        table.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateActions();
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        LabeledComponent lc = new OWLLabeledComponent("Domain  " + DefaultOWLUnionClass.OPERATOR, scrollPane);
        WidgetUtilities.addViewButton(lc, new ViewAction("View class", table) {
            @Override
			public void onView(Object o) {
                getProject().show((Instance) o);
            }
        });
        addAction = new AddClassToDomainAction(this);
                        
        lc.addHeaderButton(addAction);
        removeAction = new AllowableAction("Remove from Domain",
                                           OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS), table) {
            public void actionPerformed(ActionEvent e) {
                RDFProperty property = (RDFProperty) getEditedResource();
                Collection sel = table.getSelection();
                for (Iterator it = sel.iterator(); it.hasNext();) {
                    RDFSClass cls = (RDFSClass) it.next();
                    removeFromDomain(cls, property);
                }
            }


            @Override
			public void onSelectionChange() {
                Collection sel = table.getSelection();
                
                if (sel.isEmpty()) {
                	setAllowed(false);
                	return;
                }
                
                RDFProperty prop = (RDFProperty) getEditedResource();
                
                if (!prop.isEditable()) {
                	setAllowed(false);
                	return;
                }
                
                
                boolean allowed = false;
                for (Iterator it = sel.iterator(); it.hasNext();) {
                    RDFSClass cls = (RDFSClass) it.next();
                    Collection unionDomain = prop.getUnionDomain();
                    if (    (!cls.equals(cls.getOWLModel().getOWLThingClass()) &&  unionDomain.contains(cls)) ||
                    		( cls.equals(cls.getOWLModel().getOWLThingClass()) &&  unionDomain.size() > 1) ) {
                        allowed = true;
                    }
                }
                
                setAllowed(allowed);
            }
        };
        updateActions();        
        lc.addHeaderButton(removeAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
               slot.getName().equals(Model.Slot.DIRECT_DOMAIN);
    }

    //FIXME TT: Check this method.     
    private void removeFromDomain(RDFSClass cls, RDFProperty property) {
        try {
            beginTransaction("Remove " + cls.getBrowserText() + " from the domain of " + property.getBrowserText(), property.getName());
            if (property.isDomainDefined()) {
				property.removeUnionDomainClass(cls);
			} else {
				Collection classes = new ArrayList();

				classes.addAll(property.getUnionDomain());
				/* TT: This was the previous implementation, but it is not clear that it is
				 * the desired behavior 
				 */
				// classes.addAll(property.getUnionDomain(true));

				if (classes.remove(cls)) {
					if (classes.size() == 1) {
						property.setDomain((RDFSClass) classes.iterator()
								.next());
					} else {
						RDFSClass domain = cls.getOWLModel().createOWLUnionClass(classes);
						property.setDomain(domain);
					}
				}
			}
            commitTransaction();
        }
        catch (Exception ex) {
        	rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }
    }


    @Override
	public void setEditable(boolean b) {        
        //table.setEnabled(b);
        setEnabled(b);
        //addAction.setEnabled(b);
        //removeAction.setAllowed(b);
    }


    @SuppressWarnings("deprecation")
	@Override
	public void setInstance(Instance newInstance) {
    	if (getInstance() != null) {
    		getInstance().removeFrameListener(frameListener);
    	}
        super.setInstance(newInstance);
        if (getInstance() != null) {
        	getInstance().addFrameListener(frameListener);
        }
        if (newInstance instanceof RDFProperty) {
            final RDFProperty newProperty = (RDFProperty) newInstance;
            tableModel.setSlot(newProperty);
        }
        else {
            tableModel.setSlot(null);
        }
        updateActions();
    }


    @Override
	public void setValues(Collection values) {
        super.setValues(values);
        updateActions();
    }


    private void updateActions() {
        RDFProperty property = tableModel.getSlot();
        boolean enabled = property != null;
        setEnabled(enabled);       
    }
    
    @Override
	public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OwlMetaProjectConstants.OPERATION_PROPERTY_TAB_WRITE);
    	
    	RDFProperty property = tableModel.getSlot();
    	
    	if (property != null) {
    		addAction.setEnabled(enabled);
    		enabled = enabled && property.isEditable();    	
    	}    	    	
    	
    	if (property != null) {
    		if (!property.isEditable() || !property.isDomainDefined()) {    			
    			removeAction.setEnabled(false);
    		} else {
    			removeAction.setAllowed(enabled);
    		}
    	}
    	//table.setEnabled(enabled);
    	
    };
    
    @Override
    public void dispose() {
    	tableModel.dispose();
    	super.dispose();
    }
}
