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

package edu.stanford.smi.protegex.owl.testing.sanity;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InversePropertyMustHaveMatchingRangeAndDomainTest extends AbstractOWLTest
        implements RepairableOWLTest, RDFPropertyTest {

    public InversePropertyMustHaveMatchingRangeAndDomainTest() {
        super(SANITY_GROUP, null);
    }


    private static boolean equalCollections(Collection a, Collection b) {
        return a.containsAll(b) && b.containsAll(a);
    }


    public static boolean fails(RDFProperty property) {
        if (property instanceof OWLObjectProperty && ((OWLObjectProperty) property).getInverseProperty() instanceof OWLObjectProperty) {
            OWLObjectProperty inverseSlot = (OWLObjectProperty) property.getInverseProperty();
            if (!property.isDomainDefined() || !inverseSlot.isDomainDefined()) {
                return false;
            }
            else {
                Collection domain = property.getUnionDomain();
                Collection range = property.getUnionRangeClasses();
                Collection inverseDomain = inverseSlot.getUnionDomain();
                Collection inverseRange = inverseSlot.getUnionRangeClasses();
                return !equalCollections(domain, inverseRange) ||
                        !equalCollections(range, inverseDomain);
            }
        }
        else {
            return false;
        }
    }


    public boolean repair(OWLTestResult testResult) {
        OWLProperty property = (OWLProperty) testResult.getHost();
        if (fails(property) && property instanceof OWLObjectProperty &&
                ((OWLObjectProperty) property).getInverseProperty() instanceof OWLObjectProperty) {
            OWLObjectProperty inverseSlot = (OWLObjectProperty) property.getInverseProperty();
            if (property.isDomainDefined() && inverseSlot.isDomainDefined()) {
                Collection inverseDomain = inverseSlot.getUnionDomain();
                Collection inverseRange = inverseSlot.getUnionRangeClasses();
                property.setUnionRangeClasses(inverseDomain);
                Collection oldDomain = new ArrayList(property.getUnionDomain());
                for (Iterator it = oldDomain.iterator(); it.hasNext();) {
                    OWLNamedClass namedCls = (OWLNamedClass) it.next();
                    property.removeUnionDomainClass(namedCls);
                }
                for (Iterator it = inverseRange.iterator(); it.hasNext();) {
                    RDFSClass cls = (RDFSClass) it.next();
                    property.addUnionDomainClass(cls);
                }
            }
        }
        return !fails(property);
    }


    public List test(RDFProperty property) {
        if (fails(property)) {
            return Collections.singletonList(new DefaultOWLTestResult("Inverse properties must have inverse domains and ranges.",
                    property,
                    OWLTestResult.TYPE_ERROR,
                    this));
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }
}
