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

package edu.stanford.smi.protegex.owl.ui.tooltips;

import java.net.URI;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLToolTipGenerator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Mar-2006
 */
public class HomeOntologyToolTipGenerator implements OWLToolTipGenerator {

	public String getToolTipText(RDFSClass aClass) {
		return getToolTipText((RDFResource) aClass);
	}

	public String getToolTipText(RDFProperty prop) {
		return getToolTipText((RDFResource) prop);
	}

	public String getToolTipText(RDFResource res) {
		String text = null;
		if (res != null) {
			text = "";

			if (!(res instanceof OWLAnonymousClass)) {
				text += "<b>" + res.getURI() + "</b>";
			}

			OWLModel owlModel = res.getOWLModel();
			TripleStore homeTS = owlModel.getTripleStoreModel()
					.getHomeTripleStore(res);
			if (homeTS != null) {
				OWLOntology homeOnt = (OWLOntology) homeTS.getOWLOntology();
				try {
					URI homeOntURI = new URI(homeOnt.getURI());
					text += "<br><b>ontology:</b> " + homeOntURI;

					RepositoryManager repManager = owlModel
							.getRepositoryManager();
					Repository homeRep = repManager.getRepository(homeOntURI);
					String homeLocation = "";
					if (homeRep != null) {
						homeLocation = homeRep
								.getOntologyLocationDescription(homeOntURI);
					} else {
						String projname = owlModel.getProject().getName();
						if (projname == null) {
							projname = "not yet saved";
						}
						homeLocation = "main ontology [" + projname + "]";
					}
					text += "<br><b>location:</b> " + homeLocation;
				} catch (Exception e) { // just do not print
					Log.emptyCatchBlock(e);
				}
			}

			if (!text.equals("")) {
				text = "<html><body>" + text + "</body></html>";
			} else {
				text = null;
			}
		}

		return text;
	}
}
