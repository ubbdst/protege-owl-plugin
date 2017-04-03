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

package edu.stanford.smi.protegex.owl.ui;

import java.util.Collection;

import javax.swing.Icon;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameWithBrowserTextRenderer;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class ResourceRendererWithBrowserText extends
		FrameWithBrowserTextRenderer {
		
	private RDFSNamedClass owlDeprecatedClass;
	
	private static final long serialVersionUID = 5885951703279077675L;

	public ResourceRendererWithBrowserText(OWLModel owlModel) {		
		this.owlDeprecatedClass = owlModel.getSystemFrames().getOwlDeprecatedClassClass();
	}
	
	@Override
	public void load(Object value) {	
		super.load(value);
		if (value instanceof FrameWithBrowserText) {			
			if (isDeprecated((FrameWithBrowserText) value)) {
				addIcon(OWLIcons.getDeprecatedIcon());
			}
		}
	}
	
	protected boolean isDeprecated(FrameWithBrowserText fbt) {
		Collection<Cls> directTypes = fbt.getTypes();
		return directTypes != null && owlDeprecatedClass != null && directTypes.contains(owlDeprecatedClass);
	}
	
	@Override
	protected Icon getFbtIcon(FrameWithBrowserText fbt) {
		String iconName = fbt.getIconName();
		Frame frame = fbt.getFrame();
		if (iconName != null) {			
			return (frame.getKnowledgeBase() instanceof OWLModel) ?
					(frame.isEditable() ? OWLIcons.getImageIcon(iconName) : OWLIcons.getReadOnlyClsIcon(iconName)) :
					super.getFbtIcon(fbt);
		} else {
			return null;
		}
	}
	
	public void setMainText(String text) {        	
		super.setMainText(StringUtilities.unquote(text));
	}
}
