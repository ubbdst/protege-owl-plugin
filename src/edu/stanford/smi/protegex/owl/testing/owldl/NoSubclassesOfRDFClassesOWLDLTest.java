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

import com.hp.hpl.jena.vocabulary.RDF;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class NoSubclassesOfRDFClassesOWLDLTest extends AbstractOWLTest implements OWLDLTest, RDFSClassTest {

    public final static Set ILLEGAL_SYSTEM_CLASSES = new HashSet();


    static {
        ILLEGAL_SYSTEM_CLASSES.add(RDF.List.getURI());
    }


    public NoSubclassesOfRDFClassesOWLDLTest() {
        super(GROUP, null);
    }


    public List test(RDFSClass aClass) {
        List results = new ArrayList();
        if (aClass instanceof RDFSNamedClass) {
            for (Iterator it = aClass.getSuperclasses(false).iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSNamedClass) {
                    RDFSNamedClass rdfsClass = (RDFSNamedClass) superCls;
                    if (ILLEGAL_SYSTEM_CLASSES.contains(rdfsClass.getURI()) || !(superCls instanceof OWLNamedClass)) {
                        results.add(new DefaultOWLTestResult("OWL DL does not support subclasses of RDF(S) classes: The class " +
                                aClass.getBrowserText() + " is a subclass of " + superCls.getBrowserText() + ".",
                                aClass,
                                OWLTestResult.TYPE_OWL_FULL,
                                this));
                    }
                }
            }
        }
        return results;
    }
}
