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

package edu.stanford.smi.protegex.owl.testing.owldl;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TransitivePropertiesCannotBeFunctionalOWLDLTest extends AbstractOWLTest
        implements OWLDLTest, RDFPropertyTest, RepairableOWLTest {

    public TransitivePropertiesCannotBeFunctionalOWLDLTest() {
        super(GROUP, null);
    }


    public static boolean fails(RDFProperty property) {
        if (property instanceof OWLObjectProperty && ((OWLObjectProperty) property).isTransitive()) {
            return isFunctional(property, new HashSet());
        }
        return false;
    }


    private static boolean isFunctional(Slot slot, Set reached) {
        if (!reached.contains(slot)) {
            reached.add(slot);
            if (slot instanceof OWLObjectProperty) {
                OWLObjectProperty objectSlot = (OWLObjectProperty) slot;
                if (objectSlot.isFunctional() || objectSlot.isInverseFunctional()) {
                    return true;
                }
                for (Iterator it = slot.getDirectSuperslots().iterator(); it.hasNext();) {
                    Slot superSlot = (Slot) it.next();
                    if (isFunctional(superSlot, reached)) {
                        return true;
                    }
                }
                Slot inverseSlot = slot.getInverseSlot();
                if (inverseSlot != null && isFunctional(inverseSlot, reached)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean repair(OWLTestResult testResult) {
        OWLProperty property = (OWLProperty) testResult.getHost();
        property.setFunctional(false);
        property.setInverseFunctional(false);
        return !fails(property);
    }


    public List test(RDFProperty property) {
        if (fails(property)) {
            return Collections.singletonList(new DefaultOWLTestResult("Transitive properties (or inverse or super properties of them) cannot be functional (or inverse functional) in OWL DL.",
                                                                      property,
                                                                      OWLTestResult.TYPE_OWL_FULL,
                                                                      this));
        }
        return Collections.EMPTY_LIST;
    }
}
