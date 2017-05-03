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

package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;


/**
 * This class represents information that should be collected about parts of 
 * expressions when they are parsed from the Protege Frames knowledge base.  The
 * intention is that this class will be utilized instead of simply
 * collecting the expressions and then calling getExpressionRoot
 * later.  getExpressionRoot is extremely expensive and not
 * recommended.
 */

public class ExpressionInfo<E extends OWLAnonymousClass> {
  private E expression;
  private OWLAnonymousClass headOfExpression;
  private OWLNamedClass directNamedClass;
        
  public ExpressionInfo(E expression, 
                        OWLAnonymousClass headOfExpression, 
                        OWLNamedClass directNamedClass) {
    this.expression = expression;
    this.headOfExpression = headOfExpression;
    this.directNamedClass = directNamedClass;
  }
  public OWLNamedClass getDirectNamedClass() {
    return directNamedClass;
  }
  public OWLAnonymousClass getHeadOfExpression() {
    return headOfExpression;
  }

  public E getExpression() {
    return expression;
  }
        
        
}

