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

package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.RelativeFileRepository;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 28, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RelativeFileURLSpecificationWizardPanel extends RelativeURLSpecificationWizardPanel {
	
	public RelativeFileURLSpecificationWizardPanel(WizardPage wizardPage,
			OWLModel model) {
		super(wizardPage, model);
	}

	@Override
	public Repository createRepository() {
		try {
			File file = RepositoryUtil.getRepositoryFileFromRelativePath(
					getOWLModel(), getRelativePath());

			if (file != null) {
				return new RelativeFileRepository(file, getRelativePath(),
						isForcedReadOnlySelected());
			}
		} catch (MalformedURLException e) {
			return null;
		} catch (URISyntaxException e) {
			return null;
		}

		return null;
	}

	@Override
	protected String getDocumentation() {
		return HELP_TEXT;
	}

	private static final String HELP_TEXT = "<p>Please specify a relative <b>URL</b> that points "
			+ "to an ontology file.</p>"
			+ "<p>The URL should be relative to the current pprj/owl file."
			+ "For example if the pprj/owl file "
			+ "is located at /A/B/C/c.owl, the relative "
			+ "URL ../B/b.owl would specify the ontology file "
			+ "with the path /A/B/b.owl"
			+ "<p>Note that the path separator for URLs is the forward "
			+ "slash '/', and spaces must be replaced with \"%20\".</p>";
}

