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

package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;

/**
 * An entry in the FindUsageTableModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindUsageTableItem {

    public final static int SUPERCLASS = 0;

    public final static int EQUIVALENT_CLASS = 1;

    public final static int DISJOINT_CLASS = 2;

    public final static int RANGE = 3;

    public final static int VALUE = 4;

    public RDFResource host;

    public RDFResource usage;

    public int type;


    public FindUsageTableItem(int type, RDFResource host, RDFResource usage) {
        this.type = type;
        this.host = host;
        this.usage = usage;
    }


    public boolean contains(Frame frame) {
        return host.equals(frame) || usage.equals(frame);
    }


    public Icon getIcon() {
        switch (type) {
            case SUPERCLASS:
                return OWLIcons.getImageIcon(OWLIcons.RDFS_SUBCLASS_OF);
            case EQUIVALENT_CLASS:
                return OWLIcons.getImageIcon(OWLIcons.OWL_EQUIVALENT_CLASS);
            case DISJOINT_CLASS:
                return OWLIcons.getImageIcon(OWLIcons.OWL_DISJOINT_CLASSES);
            case RANGE:
                return OWLIcons.getImageIcon(OWLIcons.RDFS_RANGE);
            case VALUE:
                return OWLIcons.getImageIcon(OWLIcons.OWL_OBJECT_PROPERTY);
        }
        return null;
    }
}
