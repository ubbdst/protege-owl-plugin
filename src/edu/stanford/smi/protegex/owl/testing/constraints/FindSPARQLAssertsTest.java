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

package edu.stanford.smi.protegex.owl.testing.constraints;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.testing.AbstractOWLTest;
import edu.stanford.smi.protegex.owl.testing.DefaultOWLTestResult;
import edu.stanford.smi.protegex.owl.testing.OWLTestResult;
import edu.stanford.smi.protegex.owl.testing.RDFResourceTest;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindSPARQLAssertsTest extends AbstractOWLTest implements RDFResourceTest {


    public FindSPARQLAssertsTest() {
        super(null, null);
    }


    private void addItems(List emptyItems, String iconName, List results, RDFResource instance) {
        for (Iterator it = emptyItems.iterator(); it.hasNext();) {
            final String str = (String) it.next();
            results.add(new DefaultOWLTestResult(str,
                    instance,
                    OWLTestResult.TYPE_WARNING,
                    this,
                    OWLIcons.getImageIcon(iconName)) {
                public String toString() {
                    return "SPARQL Assertion (at " + getHost().getBrowserText() + "): " + str;
                }
            });
        }
    }


    public String getName() {
        return "Find SPARQL assert items";
    }


    public static List getSPARQLAsserts(RDFResource instance, String uri) {
        List results = new ArrayList();
        OWLModel owlModel = instance.getOWLModel();
        String propertyName = owlModel.getResourceNameForURI(uri);
        if (propertyName != null) {
            RDFProperty property = owlModel.getRDFProperty(propertyName);
            if (property != null) {
                for (Iterator it = instance.getPropertyValues(property).iterator(); it.hasNext();) {
                    Object value = it.next();
                    String str = null;
                    if (value instanceof RDFSLiteral) {
                        str = ((RDFSLiteral) value).getString();
                        value = str;
                    }
                    else {
                        str = value.toString();
                    }
                    results.add(value);
                }
            }
        }
        return results;
    }


    public List test(RDFResource instance) {
        List emptyItems = getSPARQLAsserts(instance, SPARQLAssertTest.EMPTY_PROPERTY_URI);
        List notEmptyItems = getSPARQLAsserts(instance, SPARQLAssertTest.NOT_EMPTY_PROPERTY_URI);
        List results = new ArrayList();
        addItems(emptyItems, OWLIcons.ASSERT_FALSE, results, instance);
        addItems(notEmptyItems, OWLIcons.ASSERT_TRUE, results, instance);
        return results;
    }
}
