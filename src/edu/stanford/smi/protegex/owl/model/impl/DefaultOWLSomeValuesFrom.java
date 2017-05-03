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

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * The default implementation of OWLSomeValuesFrom.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLSomeValuesFrom extends AbstractOWLQuantifierRestriction
        implements OWLSomeValuesFrom {

    /**
     * The unicode operator symbol for this kind of restriction
     */
    public final static char OPERATOR = '\u2203';


    public DefaultOWLSomeValuesFrom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLSomeValuesFrom() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLSomeValuesFrom(this);
    }


    public RDFProperty getFillerProperty() {
        return getOWLModel().getRDFProperty(OWLNames.Slot.SOME_VALUES_FROM);
    }


    public String getIconName() {
        return OWLIcons.OWL_SOME_VALUES_FROM;
    }


    public char getOperator() {
        return OPERATOR;
    }


    public RDFResource getSomeValuesFrom() {
        return getFiller();
    }


    public void setSomeValuesFrom(RDFResource someValuesFrom) {
        setFiller(someValuesFrom);
    }
}
