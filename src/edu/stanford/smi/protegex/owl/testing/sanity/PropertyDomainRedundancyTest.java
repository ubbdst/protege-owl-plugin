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

import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.*;

/**
 * @author Holger Knublauch
 */
public class PropertyDomainRedundancyTest extends AbstractOWLTest
        implements RDFPropertyTest, RepairableOWLTest {

    public PropertyDomainRedundancyTest() {
        super(SANITY_GROUP, "Domain of a property should not contain redundant classes");
    }


    public static Collection fails(OWLObjectProperty slot) {
        Collection results = new HashSet();
        Collection domain = new HashSet(slot.getUnionDomain());
        if (domain.size() >= 2) {
            for (Iterator it = domain.iterator(); it.hasNext();) {
                RDFSClass subCls = (RDFSClass) it.next();
                for (Iterator oit = domain.iterator(); oit.hasNext();) {
                    RDFSClass superCls = (RDFSClass) oit.next();
                    if (!superCls.equals(subCls) && subCls.getSuperclasses(true).contains(superCls)) {
                        results.add(subCls);
                    }
                }
            }
        }
        return results;
    }


    public boolean repair(OWLTestResult testResult) {
        RDFResource host = testResult.getHost();
        if (host instanceof OWLObjectProperty) {
            OWLObjectProperty slot = (OWLObjectProperty) host;
            Collection clses = fails(slot);
            for (Iterator it = clses.iterator(); it.hasNext();) {
                RDFSClass cls = (RDFSClass) it.next();
                slot.removeUnionDomainClass(cls);
            }
            return fails(slot).isEmpty();
        }
        return false;
    }


    public List test(RDFProperty property) {
        if (property instanceof OWLObjectProperty) {
            Collection failClses = fails((OWLObjectProperty) property);
            if (!failClses.isEmpty()) {
                String str = failClses.size() > 1 ? "es " : " ";
                for (Iterator it = failClses.iterator(); it.hasNext();) {
                    RDFSClass cls = (RDFSClass) it.next();
                    str += cls.getBrowserText();
                    if (it.hasNext()) {
                        str += ", ";
                    }
                }
                return Collections.singletonList(new DefaultOWLTestResult("The domain of " +
                        property.getBrowserText() + " contains the redundant class" +
                        str + ".",
                        property,
                        OWLTestResult.TYPE_WARNING,
                        this));
            }
        }
        return Collections.EMPTY_LIST;
    }
}
