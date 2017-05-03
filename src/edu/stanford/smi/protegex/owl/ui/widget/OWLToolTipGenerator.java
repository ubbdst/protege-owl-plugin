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

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * An interface for objects that can generate tool tip texts for class trees,
 * OWLTables, etc.
 * <p/>
 * To change the default tool tip renderer use OWLUI.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLToolTipGenerator {

    /**
     * Gets a tool tip text for a given RDFSClass.
     *
     * @param aClass the RDFSClass to get the tool tip for
     * @return the text or null if there is no tool tip available for aClass
     */
    String getToolTipText(RDFSClass aClass);

    /**
     * Gets a tool tip text for a given RDFProperty.
     *
     * @param prop the RDFProperty to get the tool tip for
     * @return the text or null if there is no tool tip available for prop
     */
    String getToolTipText(RDFProperty prop);

    String getToolTipText(RDFResource res);
}
