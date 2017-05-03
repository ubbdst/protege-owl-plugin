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

package edu.stanford.smi.protegex.owl.testing.style;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.*;

/**
 * Normalisation rule - all primitive siblings should be disjoint.
 *
 * This test reports at the superclass level (ie where prim  subs aren't disjoint)
 * to keep the number of results down.
 *
 * Fixing this uses <code>OWLUtil.ensureSubclassesDisjoint()</code> to add
 * required disjoints in.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Feb-2006
 */
public class PrimitiveSubclassesDisjointTest extends AbstractOWLTest
        implements RDFSClassTest, AutoRepairableOWLTest {

    public String getDocumentation() {
        return "Checks that all primitive subclasses are disjoint";
    }


    public String getGroup() {
        return "Style";
    }


    public String getName() {
        return "Normalisation: Primitive Subclasses Disjoint";
    }


    public List test(RDFSClass aClass) {
        Collection primSubs = getPrimitiveSubs(aClass);

        if (primSubs.size() > 1){

            Collection idealDisjoints = new ArrayList(primSubs);
            Collection brokenDisjoints = new ArrayList();

            for (Iterator i = primSubs.iterator(); i.hasNext();){
                OWLNamedClass sub = (OWLNamedClass)i.next();
                idealDisjoints.remove(sub);
                if (!sub.getDisjointClasses().containsAll(idealDisjoints)){
                    brokenDisjoints.add(sub);
                }
                idealDisjoints.add(sub);
            }

            if (!brokenDisjoints.isEmpty()){

                String brokenDisjointsText = "Missing disjoints on primitive subclasses:";

                for (Iterator i=brokenDisjoints.iterator(); i.hasNext();){
                    brokenDisjointsText += " " + ((OWLNamedClass)i.next()).getBrowserText();
                }

                return Collections.singletonList(
                        new DefaultOWLTestResult (brokenDisjointsText,
                                                  aClass,
                                                  OWLTestResult.TYPE_WARNING,
                                                  this)
                );
            }
        }
        return Collections.EMPTY_LIST;
    }

    private Collection getPrimitiveSubs(RDFSClass aClass) {

        Collection primNamedSubs = aClass.getNamedSubclasses();

        for (Iterator j = primNamedSubs.iterator(); j.hasNext();){
            RDFSClass namedSub = (RDFSClass) j.next();
            if ((namedSub instanceof OWLNamedClass) &&
                (namedSub.isVisible())){
                if (((OWLNamedClass)namedSub).isDefinedClass()){
                    j.remove();
                }
            }
            else{
                j.remove();
            }
        }

        return primNamedSubs;
    }

    public boolean repair(OWLTestResult testResult) {
        OWLUtil.ensureSubclassesDisjoint((OWLNamedClass)testResult.getHost());
        return true;
    }
}
