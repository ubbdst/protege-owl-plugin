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

package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLDatatypePropertySubpropertyPane extends OWLSubpropertyPane {

	public OWLDatatypePropertySubpropertyPane(OWLModel owlModel) {
		super(owlModel);
	}


	protected OWLPropertySubpropertyRoot createRoot() {
		return new OWLDatatypePropertySubpropertyRoot(getOWLModel());
	}


	protected String getHeaderLabel() {
		return "Datatype Properties";
	}


	protected Icon getHeaderIcon() {
		return OWLIcons.getImageIcon("OWLDatatypeProperty");
	}


	protected Collection getActions() {
		ArrayList actions = new ArrayList();
		actions.add(getCreateOWLDatatypePropertyAction());
		Action createSubPropertyAction = getCreateSubpropertyAction();
		createSubPropertyAction.putValue(Action.SMALL_ICON, OWLIcons.getCreatePropertyIcon("DatatypeSubProperty"));
		actions.add(createSubPropertyAction);
		getDeletePropertyAction().putValue(Action.SMALL_ICON, OWLIcons.getDeleteIcon("OWLDatatypeProperty"));
		return actions;
	}
}

