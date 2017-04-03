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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.widget.URLWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.net.URI;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExternalResourceURLWidget extends URLWidget {

    public void commitChanges() {
        if (getInvalidTextDescription(getText()) == null) {
            super.commitChanges();
        }
    }


    protected String getInvalidTextDescription(String text) {
        try {
            if (text.startsWith("http://") ||
                    text.startsWith("file:") ||
                    text.startsWith("mailto:") ||
                    text.startsWith("urn:")) {
                new URI(text);
                if (isDuplicateURL(text)) {
                    return "This URL is already used elsewhere.\nPlease reuse the existing untyped resource.";
                }
                return null;
            }
        }
        catch (Exception ex) {
        }
        return "Not a valid URI, such as http://protege.stanford.edu";
    }


    // Only allow valid values to be assigned
    public Collection getValues() {
        String text = getText();
        if (getInvalidTextDescription(text) == null) {
            return super.getValues();
        }
        else {
            return getInstance().getDirectOwnSlotValues(getSlot());
        }
    }


    private boolean isDuplicateURL(String text) {
        Frame frame = getKnowledgeBase().getFrame(text);
        return frame != null && !getInstance().equals(frame);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if (cls.getKnowledgeBase() instanceof OWLModel) {
            return URLWidget.isSuitable(cls, slot, facet);
        }
        else {
            return false;
        }
    }
}
