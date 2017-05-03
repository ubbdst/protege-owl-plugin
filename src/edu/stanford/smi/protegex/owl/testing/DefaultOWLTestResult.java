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

package edu.stanford.smi.protegex.owl.testing;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLTestResult implements OWLTestResult {

    private Icon icon;

    private String message;

    private RDFResource source;

    private OWLTest test;

    private int type;

    private Object userObject;


    public DefaultOWLTestResult(String message,
                                RDFResource source,
                                int type,
                                OWLTest test) {
        this(message, source, type, test, null);
    }


    public DefaultOWLTestResult(String message,
                                RDFResource source,
                                int type,
                                OWLTest test,
                                Icon icon) {
        this.message = message;
        this.source = source;
        this.type = type;
        this.test = test;
        this.icon = icon;
    }


    public RDFResource getHost() {
        return source;
    }


    public Icon getIcon() {
        if (icon == null) {
            int type = getType();
            if (type == OWLTestResult.TYPE_ERROR) {
                return OWLIcons.getOWLTestErrorIcon();
            }
            else if (type == OWLTestResult.TYPE_WARNING) {
                return OWLIcons.getOWLTestWarningIcon();
            }
            else if (type == OWLTestResult.TYPE_OWL_FULL) {
                return OWLIcons.getOWLFullIcon();
            }
            else {
                return Icons.getBlankIcon();
            }
        }
        else {
            return icon;
        }
    }


    public String getMessage() {
        return message;
    }


    public OWLTest getOWLTest() {
        return test;
    }


    public int getType() {
        return type;
    }


    public String getTypeString() {
        if (type == OWLTestResult.TYPE_ERROR) {
            return "Error";
        }
        else if (type == OWLTestResult.TYPE_WARNING) {
            return "Warning";
        }
        else if (type == OWLTestResult.TYPE_OWL_FULL) {
            return "OWL Full";
        }
        else {
            return "";
        }
    }


    public Object getUserObject() {
        return userObject;
    }


    public void setUserObject(Object value) {
        userObject = value;
    }


    public String toString() {
        String hostStr = getHost() == null ? "" : " (at " + getHost().getBrowserText() + ")";
        return getTypeString() + hostStr + ": " + getMessage();
    }
}
