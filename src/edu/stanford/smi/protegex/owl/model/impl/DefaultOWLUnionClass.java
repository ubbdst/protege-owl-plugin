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
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * The default implementation of OWLUnionClass.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLUnionClass extends AbstractOWLNAryLogicalClass
        implements OWLUnionClass {

    /**
     * The unicode operator symbol for this kind of class
     */
    public final static char OPERATOR = '\u2294';


    public DefaultOWLUnionClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLUnionClass() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLUnionClass(this);
    }


    public String getIconName() {
        return OWLIcons.OWL_UNION_CLASS;
    }


    public RDFProperty getOperandsProperty() {
        return getOWLModel().getOWLUnionOfProperty();
    }


    public char getOperatorSymbol() {
        return OPERATOR;
    }
}
