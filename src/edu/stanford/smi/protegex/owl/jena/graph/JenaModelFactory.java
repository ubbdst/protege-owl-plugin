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

package edu.stanford.smi.protegex.owl.jena.graph;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A totally experimental class to create a Jena Model for a given OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaModelFactory {

    /**
     * Creates a new Jena Model from a given OWLModel.
     * This Model will directly reflect the structure of the underlying OWLModel
     * for query purposes.  It will not be possible to write to this Model.
     *
     * @param owlModel the OWLModel to get a Model for
     * @return a (read-only) Model
     */
    public static Model createModel(OWLModel owlModel) {
        TripleStoreModel tsm = owlModel.getTripleStoreModel();
        if (tsm.getTripleStores().size() == 2) {
            TripleStore ts = tsm.getTopTripleStore();
            ProtegeGraph graph = new ProtegeGraph(owlModel, ts);
            return ModelFactory.createModelForGraph(graph);
        }
        else {
            Collection graphs = new ArrayList();
            Iterator it = tsm.listUserTripleStores();
            while (it.hasNext()) {
                TripleStore ts = (TripleStore) it.next();
                ProtegeGraph graph = new ProtegeGraph(owlModel, ts);
                graphs.add(graph);
            }
            Graph unionGraph = new MultiUnion(graphs.iterator());
            return ModelFactory.createModelForGraph(unionGraph);
        }
    }
}
