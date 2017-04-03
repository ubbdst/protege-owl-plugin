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

package edu.stanford.smi.protegex.owl.testing.todo;

import edu.stanford.smi.protegex.owl.model.Deprecatable;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeprecationOWLTest extends AbstractOWLTest implements RDFSClassTest, RDFPropertyTest {

    public String getDocumentation() {
        return "<HTML>Finds all classes and properties that are marked as deprecated " +
                "using owl:DeprecatedClass or owl:DeprecatedProperty.</HTML>";
    }


    public String getGroup() {
        return TodoAnnotationOWLTest.GROUP;
    }


    public String getName() {
        return "List deprecated classes and properties";
    }


    public List test(RDFProperty property) {
        if (property instanceof Deprecatable && ((Deprecatable) property).isDeprecated()) {
            return Collections.singletonList(new DefaultOWLTestResult("Property " + property.getBrowserText() +
                    " has been deprecated.",
                    property,
                    OWLTestResult.TYPE_WARNING,
                    this));
        }
        return Collections.EMPTY_LIST;
    }


    public List test(RDFSClass aClass) {
        if (aClass instanceof Deprecatable && ((Deprecatable) aClass).isDeprecated()) {
            return Collections.singletonList(new DefaultOWLTestResult("Class " + aClass.getBrowserText() +
                    " has been deprecated.",
                    aClass,
                    OWLTestResult.TYPE_WARNING,
                    this));
        }
        return Collections.EMPTY_LIST;
    }
}
