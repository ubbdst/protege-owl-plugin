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

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.testing.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class NoImportOfSystemOntologiesOWLDLTest extends AbstractOWLTest implements OWLDLTest, OWLModelTest {

    public final static String[] ILLEGAL_URIS = {
            OWL.getURI(),
            RDF.getURI(),
            RDFS.getURI()
    };


    public NoImportOfSystemOntologiesOWLDLTest() {
        super(GROUP, null);
    }


    public List test(OWLModel owlModel) {
        List results = new ArrayList();
        Iterator it = owlModel.getOWLOntologyClass().getInstances(false).iterator();
        while (it.hasNext()) {
            OWLOntology oi = (OWLOntology) it.next();
            for (int i = 0; i < ILLEGAL_URIS.length; i++) {
                String uri = ILLEGAL_URIS[i];
                uri = uri.substring(0, uri.length() - 1);
                if (oi.getImports().contains(uri)) {
                    results.add(new DefaultOWLTestResult("OWL DL ontologies must not import \"" + uri + "\".",
                            oi,
                            OWLTestResult.TYPE_OWL_FULL,
                            this));
                }
            }
        }
        return results;
    }
}
