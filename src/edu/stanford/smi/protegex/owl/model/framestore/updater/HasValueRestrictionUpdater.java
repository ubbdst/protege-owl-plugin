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

package edu.stanford.smi.protegex.owl.model.framestore.updater;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class HasValueRestrictionUpdater extends AbstractRestrictionUpdater {
    private static final transient Logger log = Log.getLogger(HasValueRestrictionUpdater.class);

    private Facet valuesFacet;


    public HasValueRestrictionUpdater(OWLModel owlModel) {
        super(owlModel);
        valuesFacet = owlModel.getSystemFrames().getValuesFacet();
    }


    // Implements RestrictionUpdater
    public void copyFacetValuesIntoNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
        Slot slot = restriction.getOnProperty();
        if (slot != null) {
            updateValuesFacet(cls, slot);
        }
    }


    // Implements RestrictionUpdater
    public void updateRestrictions(OWLNamedClass cls, RDFProperty property, Facet facet) {
        removeRestrictions(cls, property, owlModel.getCls(OWLNames.Cls.HAS_VALUE_RESTRICTION));
        if (((Cls) cls).hasDirectlyOverriddenTemplateFacet(property, valuesFacet)) {
            Collection values = ((Cls) cls).getTemplateSlotValues(property);
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object value = it.next();
                OWLHasValue restriction = owlModel.createOWLHasValue(property, value);
                cls.addSuperclass(restriction);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("+ OWLHasValue " + restriction.getBrowserText() + " to " + cls.getName() + "." + property.getName());
                }
            }
        }
    }


    void updateValuesFacet(RDFSNamedClass cls, Slot slot) {
        ((Cls) cls).setTemplateFacetValues(slot, valuesFacet, Collections.EMPTY_LIST); // was: null
        if (log.isLoggable(Level.FINE)) {
            log.fine("- :VALUES override from " + cls.getName() + "." + slot.getName());
        }
        for (OWLHasValue r : getDirectRestrictions(cls, slot, OWLHasValue.class)) {
            if (r != null) {
                Object value = r.getHasValue();
                if (value != null) {
                    if (value instanceof RDFSLiteral) {
                        value = value.toString();
                    }
                    ((Cls) cls).addTemplateFacetValue(slot, valuesFacet, value);
                }
            }
        }
    }
}
