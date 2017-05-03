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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class ExpressionInfoUtils {
  
  public static List<ExpressionInfo<OWLRestriction>> getDirectContainingRestrictions(OWLNamedClass c) {
    List<ExpressionInfo<OWLRestriction>> rs = new ArrayList<ExpressionInfo<OWLRestriction>>();
    for (Iterator it = c.getSuperclasses(false).iterator(); it.hasNext();) {
      RDFSClass superClass = (RDFSClass) it.next();
      if (superClass instanceof OWLRestriction) {
        ExpressionInfo<OWLRestriction> ri = new ExpressionInfo<OWLRestriction>((OWLRestriction) superClass, 
                                                                               (OWLAnonymousClass) superClass, 
                                                                               c);
        rs.add(ri);
      }
      else if (superClass instanceof OWLIntersectionClass) {
        OWLIntersectionClass intersectionClass = (OWLIntersectionClass) superClass;
        for (Iterator ot = intersectionClass.getOperands().iterator(); ot.hasNext();) {
          RDFSClass operand = (RDFSClass) ot.next();
          if (operand instanceof OWLRestriction) {
            ExpressionInfo<OWLRestriction> ri = new ExpressionInfo<OWLRestriction>((OWLRestriction) operand, 
                                                                                   intersectionClass, c);
            rs.add(ri);
          }
        }
      }
    }
    Collections.sort(rs, new Comparator<ExpressionInfo<OWLRestriction>>() {
      public int compare(ExpressionInfo<OWLRestriction> o1, ExpressionInfo<OWLRestriction> o2) {
        RDFSClass directType1 = o1.getExpression().getProtegeType();
        RDFSClass directType2 = o2.getExpression().getProtegeType();
        return directType1.getName().compareTo(directType2.getName());
      }
    });
    /* We used to remove redundant restrictions here */
    return rs;
  }

}
