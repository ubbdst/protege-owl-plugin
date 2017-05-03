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
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.testing.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.query.SPARQLOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.query.SPARQLResultsPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SPARQLAssertTest extends AbstractOWLTest implements RDFSClassTest, RepairableOWLTest {

    public final static String PREFIX = "assert";

    public final static String URI = "http://www.owl-ontologies.com/assert.owl";

    public final static String NAMESPACE = URI + "#";

    public final static String EMPTY_PROPERTY_URI = NAMESPACE + "empty";

    public final static String NOT_EMPTY_PROPERTY_URI = NAMESPACE + "notEmpty";


    public SPARQLAssertTest() {
        super("Constraints", "SPARQL Asserts");
    }


    public boolean repair(OWLTestResult testResult) {
        SPARQLResultsPanel resultsPanel = SPARQLOWLModelAction.show(testResult.getHost().getOWLModel(), true);
        String queryText = (String) testResult.getUserObject();
        resultsPanel.setQueryText(queryText);
        resultsPanel.executeQuery(queryText);
        return false;
    }


    public List test(RDFSClass aClass) {
        OWLModel owlModel = aClass.getOWLModel();
        String matchesName = owlModel.getResourceNameForURI(NOT_EMPTY_PROPERTY_URI);
        if (matchesName != null) {
            RDFProperty assertMatchesProperty = owlModel.getRDFProperty(matchesName);
            if (assertMatchesProperty != null) {
                List results = new ArrayList();
                test(aClass, assertMatchesProperty, true, "at least one match", OWLIcons.ASSERT_TRUE, results);
                String noMatchesName = owlModel.getResourceNameForURI(EMPTY_PROPERTY_URI);
                RDFProperty assertNoMatchesProperty = owlModel.getRDFProperty(noMatchesName);
                if (assertNoMatchesProperty != null) {
                    test(aClass, assertNoMatchesProperty, false, "no matches", OWLIcons.ASSERT_FALSE, results);
                }
                return results;
            }
        }
        return Collections.EMPTY_LIST;
    }


    private void test(RDFSClass aClass,
                      RDFProperty assertProperty,
                      boolean expected,
                      String expectedString,
                      String iconName,
                      List results) {
        OWLModel owlModel = aClass.getOWLModel();
        Iterator it = aClass.listPropertyValues(assertProperty);
        while (it.hasNext()) {
            Object value = it.next();
            if (value instanceof String) {
                String str = (String) value;
                try {
                    QueryResults queryResults = owlModel.executeSPARQLQuery(str);
                    if (queryResults.hasNext() != expected) {
                        DefaultOWLTestResult r = new DefaultOWLTestResult("Query asserted to have " +
                                expectedString + ": " + str,
                                aClass, OWLTestResult.TYPE_ERROR, this, OWLIcons.getImageIcon(iconName));
                        r.setUserObject(str);
                        results.add(r);
                    }
                }
                catch (Exception ex) {
                    results.add(new DefaultOWLTestResult("Query could not be executed: " + str + ". " + ex.getMessage(), aClass, OWLTestResult.TYPE_WARNING, this));
                }
            }
        }
    }
}
