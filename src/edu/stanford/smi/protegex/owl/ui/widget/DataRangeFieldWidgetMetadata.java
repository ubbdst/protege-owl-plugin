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

import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DataRangeFieldWidgetMetadata implements OWLWidgetMetadata {

    public int getSuitability(RDFSNamedClass type, RDFProperty predicate) {
        if (type instanceof OWLNamedClass) {
            final OWLNamedClass namedClass = ((OWLNamedClass) type);
            int maxCardinality = namedClass.getMaxCardinality(predicate);
            if (maxCardinality >= 0 && maxCardinality <= 1) {
                RDFResource allValuesFrom = namedClass.getAllValuesFrom(predicate);
                if (allValuesFrom instanceof OWLDataRange) {
                    return DEFAULT;
                }
                RDFResource someValuesFrom = namedClass.getSomeValuesFrom(predicate);
                if (someValuesFrom instanceof OWLDataRange) {
                    return DEFAULT;
                }
            }
        }
        if (predicate.getRange() instanceof OWLDataRange && predicate.isFunctional()) {
            return DEFAULT;
        }
        else {
            return NOT_SUITABLE;
        }
    }
}
