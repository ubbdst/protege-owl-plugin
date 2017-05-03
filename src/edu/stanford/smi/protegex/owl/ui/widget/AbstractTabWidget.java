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

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * A convenience wrapper of the standard AbstractTabWidget class to make it
 * easier accessible for OWL programmers.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTabWidget extends edu.stanford.smi.protege.widget.AbstractTabWidget {

    /**
     * @see #getOWLModel
     * @deprecated use getOWLModel() instead
     */
    public KnowledgeBase getKnowledgeBase() {
        return super.getKnowledgeBase();
    }


    public OWLModel getOWLModel() {
        return (OWLModel) super.getKnowledgeBase();
    }
}
