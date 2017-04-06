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

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protege.widget.ReadOnlyWidgetConfigurationPanel;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;

/**
 * A container for PropertyValuesComponents, so that they can be placed on a
 * Protege FormWidget.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyValuesWidget extends AbstractSlotWidget {

    private PropertyValuesComponent component;
    private boolean isEditable;


    protected abstract PropertyValuesComponent createComponent(RDFProperty predicate);


    public void initialize() {
        component = createComponent((RDFProperty) getSlot());
        add((Component) component);
        
        if (component instanceof AbstractPropertyValuesComponent) {
    		((AbstractPropertyValuesComponent)component).setEditable(!isReadOnlyConfiguredWidget());
    	}
    }


    public static boolean isInvalid(RDFResource subject, RDFProperty predicate, Collection values) {
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object value = it.next();
            if (!subject.isValidPropertyValue(predicate, value)) {
                return true;
            }
        }
        RDFResource type = subject.getRDFType();
        if (type instanceof OWLNamedClass) {
            OWLNamedClass namedClass = (OWLNamedClass) type;
            int min = namedClass.getMinCardinality(predicate);
            if (min >= 0 && values.size() < min) {
                return true;
            }
            int max = namedClass.getMaxCardinality(predicate);
            if (max >= 0 && values.size() > max) {
                return true;
            }
            if(values.size() == 0 && namedClass.getSomeValuesFrom(predicate) != null) {
                return true;
            }
        }
        return false;
    }


    public void setInstance(Instance newInstance) {
        RDFResource subject = null;
        if (newInstance instanceof RDFResource) {
            subject = (RDFResource) newInstance;
        }
       
        component.setSubject(subject);        
        super.setInstance(newInstance);
    }


    public void setValues(Collection values) {
        super.setValues(values);
        component.valuesChanged();
    }


    @Override
    protected void updateBorder(Collection values) {
        if (OWLUI.isConstraintChecking((OWLModel) getKnowledgeBase())) {
            RDFResource subject = (RDFResource) getInstance();
            RDFProperty predicate = (RDFProperty) getSlot();
            if (subject != null && predicate != null) {
                boolean invalid = isInvalid(subject, predicate, values);
                if (invalid) {
                    
                    setInvalidValueBorder();
                }
                else {
                    setNormalBorder();
                }
                repaint();
            }
        }
    }
    
    @Override
    public void setEditable(boolean b) {
    	b = b && !isReadOnlyConfiguredWidget();    	
    	
    	if (component instanceof AbstractPropertyValuesComponent) {
    		((AbstractPropertyValuesComponent)component).setEditable(b);
    	}
    	
    	isEditable = b;
    }
    
    public boolean isEditable() {
		return isEditable;
	}
    
    //maybe move up
    @Override
    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
    	WidgetConfigurationPanel confPanel = super.createWidgetConfigurationPanel();
    	
    	confPanel.addTab("Options", new ReadOnlyWidgetConfigurationPanel(this));
    	
    	return confPanel;
    }
    
    @Override
    public void dispose() {
    	component.dispose();
    	super.dispose();
    }
    
}
