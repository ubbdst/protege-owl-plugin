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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.framestore.updater.RestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;

/**
 * This class contains a variety of useful OWL Utilities.  A major goal of this class is 
 * to take things out of OWLFrameStore where they do not belong.
 * 
 * @author tredmond
 *
 */

public class OWLFrameStoreUtils {
  
  public static Collection convertValueListToRDFLiterals(OWLModel owlModel, Collection values) {
    if (!values.isEmpty()) {
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof String && DefaultRDFSLiteral.isRawValue((String) o)) {
                return copyValueListToRDFLiterals(owlModel, values);
            }
        }
    }
    return values;
  }

  @SuppressWarnings("unchecked")
  public static List copyValueListToRDFLiterals(OWLModel owlModel, Collection values) {
    List result = new LinkedList();
    for (Iterator it = values.iterator(); it.hasNext();) {
        Object o = it.next();
        if (o instanceof String) {
            final String str = (String) o;
            if (DefaultRDFSLiteral.isRawValue(str)) {
                result.add(new DefaultRDFSLiteral(owlModel, str));
            }
            else {
                result.add(o);
            }
        }
        else {
            result.add(o);
        }
    }
    return result;
  }
}
