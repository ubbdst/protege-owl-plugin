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

package edu.stanford.smi.protegex.owl.ui.actions.triple;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTripleAction implements TripleAction {

    private String iconFileName;

    private Class iconResourceClass;

    private String name;


    public AbstractTripleAction(String name) {
        this(name, null, null);
    }


    public AbstractTripleAction(String name, String iconFileName, Class iconResourceClass) {
        this.name = name;
        this.iconFileName = iconFileName;
        this.iconResourceClass = iconResourceClass;
    }


    public String getGroup() {
        return null;
    }


    public String getIconFileName() {
        return iconFileName;
    }


    public Class getIconResourceClass() {
        return iconResourceClass;
    }


    public String getName() {
        return name;
    }
}
