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

package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter;

import java.util.Comparator;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class NativeWriterComparator extends FrameComparator<RDFResource> implements
        Comparator<RDFResource> {
    
    @Override
    public int compare(RDFResource f1, RDFResource f2) {
        int type1 = getType(f1);
        int type2 = getType(f2);
        if (type1 > type2) {
            return 1;
        }
        else if (type2 > type1) {
            return -1;
        }
        else {
            return super.compare(f1, f2);
        }
    }
    
    private static int getType(Frame f) {
        if (f instanceof OWLClass) {
            return 0;
        }
        else if (f instanceof OWLProperty) {
            return 1;
        }
        else if (f instanceof OWLIndividual) {
            return 2;
        }
        else {
            return -1;
        }
    }
}
