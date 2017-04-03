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

package edu.stanford.smi.protegex.owl.ui.properties.types;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.util.Collection;

/**
 * A AbstractPropertyTypesWidget for OWLObjectProperties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLObjectPropertyTypesWidget extends AbstractPropertyTypesWidget {


    private void alignRangeAndDomain() {
        OWLObjectProperty property = (OWLObjectProperty) getEditedResource();
        if (property.isSymmetric()) {
            Collection domain = property.getUnionDomain();
            Collection range;
            range = property.getUnionRangeClasses();
            if (!domain.containsAll(range) || !range.containsAll(domain)) {
                //JOptionPane.showMessageDialog(this, "Symmetric properties should have identical\ndomains and ranges.");
            }
        }
    }


    public void initialize() {
        OWLModel owlModel = getOWLModel();
        initialize(new RDFSNamedClass[]{
                owlModel.getRDFSNamedClass(OWLNames.Cls.FUNCTIONAL_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY)
        });
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if (cls instanceof RDFSNamedClass && slot.getName().equals(Model.Slot.DIRECT_TYPES)) {
            RDFSNamedClass t = (RDFSNamedClass) cls.getKnowledgeBase().getCls(OWLNames.Cls.OBJECT_PROPERTY);
            return cls.equals(t) || cls.hasSuperclass(t);
        }
        return false;
    }


    protected void postProcessChange(RDFSNamedClass type) {
        super.postProcessChange(type);
        if (type.equals(getOWLModel().getRDFResource(OWLNames.Cls.SYMMETRIC_PROPERTY))) {
            alignRangeAndDomain();
            updateInverseProperty();
        }
    }


    private void updateInverseProperty() {
        OWLObjectProperty property = (OWLObjectProperty) getEditedResource();
        if (property.isSymmetric()) {
            property.setInverseProperty(property);
        }
        else {
            property.setInverseProperty(null);
        }
    }
}
