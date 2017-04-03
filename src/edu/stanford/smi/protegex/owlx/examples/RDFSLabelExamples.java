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

package edu.stanford.smi.protegex.owlx.examples;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;


/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 12, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RDFSLabelExamples {

	public static void main(String [] args) {
		String URI = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl";
		try {
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(URI);
			OWLNamedClass cls = owlModel.getOWLNamedClass("CheeseTopping");
			// The ontology actually only had labels in Portuguese, so for
			// the purposes of this example, add in the English label
			cls.addLabel("CheeseTopping", "en");
			Collection values = cls.getLabels();
			for(Iterator it = values.iterator(); it.hasNext(); ) {
				RDFSLiteral rdfsLiteral = (RDFSLiteral) it.next();
				// Print out the Portuguese label
				if(rdfsLiteral.getLanguage().equals("pt")) {
					System.out.println(rdfsLiteral.getString());
				}
			}
		}
		catch(Exception e) {
                  Log.getLogger().log(Level.SEVERE, "Exception caught", e);
		}
	}
}

