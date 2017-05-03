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

package edu.stanford.smi.protegex.owl.ui.wizard;

import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Dec 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLWizardPage extends WizardPage {

	private JComponent contentComponent;

	private JComponent helpComponent;

	private static final String HELP_PANEL_LOCATION = BorderLayout.SOUTH;

	public OWLWizardPage(String name, OWLWizard wizard) {
		super(name, wizard);
		contentComponent = new JPanel(new BorderLayout());
		setLayout(new BorderLayout());
		add(contentComponent);
		helpComponent = new JPanel();
		add(helpComponent, BorderLayout.SOUTH);
	}

	public void nextPressed() {
		// Do nothing
	}

	public void prevPressed() {
		// Do nothing
	}

	public void pageSelected() {
		// Do nothing
	}

	public JComponent getContentComponent() {
		return contentComponent;
	}

	public void setHelpText(String title, String text) {
		if(helpComponent != null) {
			remove(helpComponent);
		}
		if(text != null) {
			helpComponent = OWLUI.createHelpPanel(text, title);
		}
		else {
			helpComponent = new JPanel();
		}
		add(helpComponent, HELP_PANEL_LOCATION);
		revalidate();
	}

	public OWLWizard getOWLWizard() {
		return (OWLWizard) getWizard();
	}
}

