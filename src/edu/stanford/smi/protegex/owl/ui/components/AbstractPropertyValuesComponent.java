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

package edu.stanford.smi.protegex.owl.ui.components;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JComponent;

import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditorManager;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyValuesComponent extends JComponent implements PropertyValuesComponent {

    private final RDFProperty predicate;

    private RDFResource subject;

    private String label;

    private boolean editable = true;

    private boolean isReadOnly = false;


    protected AbstractPropertyValuesComponent(RDFProperty predicate) {
    	this(predicate, null);
    }

    protected AbstractPropertyValuesComponent(RDFProperty predicate, String label) {
    	this(predicate, label, false);
    }

    protected AbstractPropertyValuesComponent(RDFProperty predicate, String label, boolean isReadOnly) {
        this.predicate = predicate;
        setLayout(new BorderLayout());
        this.label = label;
        this.isReadOnly = isReadOnly;
    }


    protected PropertyValueEditor getEditor(Object value) {
        final RDFResource subject = getSubject();
        final RDFProperty predicate = getPredicate();
        return PropertyValueEditorManager.getEditor(subject, predicate, value);
    }


    protected String getLabel() {
    	if (label == null) {
	        RDFProperty property = getPredicate();
	        String text = property.getBrowserText();
	        if (getOWLModel().getProject().getPrettyPrintSlotWidgetLabels()) {
	            text = StringUtilities.symbolToLabel(text);
	        }
	        return text;
	    }

    	return label;
    }


    protected Object getObject() {
        Collection objects = getObjects();
        if (objects.isEmpty()) {
            return null;
        }
        else {
            return objects.iterator().next();
        }
    }


    public Collection getObjects() {
        return getObjects(false);
    }


    public Collection getObjects(boolean includingSubproperties) {
        RDFResource subject = getSubject();
        if (subject != null) {
            return subject.getPropertyValues(getPredicate(), includingSubproperties);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    protected OWLModel getOWLModel() {
        return predicate.getOWLModel();
    }


    public RDFProperty getPredicate() {
        return predicate;
    }


    public RDFResource getSubject() {
        return subject;
    }


    /**
     * Gets the first rdf:type of the current subject.
     *
     * @return the type of the subject
     */
    public RDFSClass getSubjectType() {
        RDFResource subject = getSubject();
        if (subject != null) {
            return subject.getRDFType();
        }
        else {
            return null;
        }
    }


    protected boolean hasHasValueRestriction() {
        if (getSubject() != null && getPredicate() != null) {
            return !getSubject().getHasValuesOnTypes(getPredicate()).isEmpty();
        }
        else {
            return false;
        }
    }


    protected boolean hasOnlyEditableValues() {
    	//TT - this is a strange condition. It doesn't seem to make sense in OWL
    	/*
        Collection values = getObjects();
        TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object value = it.next();
            if (!tsm.isEditableTriple(getSubject(), getPredicate(), value)) {
                return false;
            }
        }*/
        return true;
    }


    /**
     * Checks if the property has an owl:oneOf class as its range at the given type.
     *
     * @return true  if true, false otherwise :)
     */
    protected boolean isEnumerationProperty() {
        if (subject != null) {
            for (Iterator it = subject.getRDFTypes().iterator(); it.hasNext();) {
                RDFSClass type = (RDFSClass) it.next();
                if (type instanceof OWLNamedClass) {
                    OWLNamedClass namedClass = (OWLNamedClass) type;
                    RDFResource all = namedClass.getAllValuesFrom(predicate);
                    if (all instanceof OWLNamedClass) {
                        OWLNamedClass typeClass = (OWLNamedClass) all;
                        Iterator sit = typeClass.getSuperclasses(false).iterator();
                        while (sit.hasNext()) {
                            if (sit.next() instanceof OWLEnumeratedClass) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public void setSubject(RDFResource subject) {
        this.subject = subject;
        repaint();
    }


    protected void showResource(RDFResource resource) {
        getOWLModel().getProject().show(resource);
    }

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public void dispose() {
		//TO BE implemented in sublcasses
	}
}
