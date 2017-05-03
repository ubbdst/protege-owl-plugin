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

package edu.stanford.smi.protegex.owl.writer.xml;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 8, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class XMLWriterPreferences {

    private static XMLWriterPreferences instance;

    private boolean useNamespaceEntities;

    private boolean indenting;

    private int indentSize;


    private XMLWriterPreferences() {
        useNamespaceEntities = true;
        indenting = true;
        indentSize = 4;
    }


    public static XMLWriterPreferences getInstance() {
        if (instance == null) {
            instance = new XMLWriterPreferences();
        }
        return instance;
    }


    public boolean isUseNamespaceEntities() {
        return useNamespaceEntities;
    }


    public void setUseNamespaceEntities(boolean useNamespaceEntities) {
        this.useNamespaceEntities = useNamespaceEntities;
    }


    public boolean isIndenting() {
        return indenting;
    }


    public void setIndenting(boolean indenting) {
        this.indenting = indenting;
    }


    public int getIndentSize() {
        return indentSize;
    }


    public void setIndentSize(int indentSize) {
        this.indentSize = indentSize;
    }
}

