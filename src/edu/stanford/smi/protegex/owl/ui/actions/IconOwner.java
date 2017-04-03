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

package edu.stanford.smi.protegex.owl.ui.actions;

/**
 * A common base interface for things that can be represented by an icon.
 * This is on purpose abstracted from the Swing Icon class, so that instances
 * of this class can also be used under non-Swing environments like Eclipse.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface IconOwner {


    /**
     * Gets a Class that is in the same folder as the icon file.
     *
     * @return the icon resource Class
     */
    Class getIconResourceClass();


    /**
     * Gets the relative name of an (optional) icon.
     *
     * @return the icon name such as <CODE>"classify.gif"</CODE> or null
     */
    String getIconFileName();
}
