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

import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.testing.AbstractOWLTest;
import edu.stanford.smi.protegex.owl.testing.DefaultOWLTestResult;
import edu.stanford.smi.protegex.owl.testing.OWLTestResult;
import edu.stanford.smi.protegex.owl.testing.RDFSClassTest;

import java.util.Collections;
import java.util.List;

/**
 * Normalisation rule - classes should only have a single asserted named superclass, i.e., be a pure tree
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester, 06-Feb-2006
 * 
 */
public class SingleAssertedSuperclassTest extends AbstractOWLTest implements RDFSClassTest {

    public String getDocumentation() {
        return "Checks that named classes only have one asserted parent";
    }

    public String getGroup() {
        return "Style";
    }

    public String getName() {
        return "Normalisation: Single Asserted Superclass";
    }

	public List test(RDFSClass aClass) {
		if (aClass.getNamedSuperclasses().size() > 1) {
			return Collections.singletonList(new DefaultOWLTestResult(
					"This class has multiple asserted parents", aClass,
					OWLTestResult.TYPE_WARNING, this));
		}
		return Collections.EMPTY_LIST;
	}
}
