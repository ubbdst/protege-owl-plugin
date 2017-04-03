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


package edu.stanford.smi.protegex.owl.swrl.model.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/**
 * @author Daniel Elenius
 * @author Martin O'Connor
 */
public class SWRLUtil 
{
  static final String XSD_STRING = XSDDatatype.XSDstring.getURI();

  /**
   * This replaces RDFSLiteral.getBrowserText() for the SWRL editor.  We need some special handling, e.g. adding quotes to strings.
   */
  public static String getSWRLBrowserText(RDFObject o) {
    if (o instanceof RDFResource)
      return o.getBrowserText();
    else if (o instanceof RDFSLiteral) {
      RDFSLiteral literal = (RDFSLiteral) o;
      String literalType = literal.getDatatype().getURI();
      // Add quotes to strings
      if (literalType.equals(XSD_STRING)) {
        return "\"" + literal.toString() + "\"";
      }
      return literal.toString();
    }
    return "<unknown d-object>";
  }

  public static String getSWRLBrowserText(Object o, String resourceType)
  {
    String s = "";

    if (o == null) s += "<DELETED_" + resourceType + ">";
    else if (o instanceof RDFUntypedResource) { // Try to substitute the prefix.
      RDFUntypedResource resource = (RDFUntypedResource)o;
      OWLModel owlModel = resource.getOWLModel(); 
      NamespaceManager namespaceManager = owlModel.getNamespaceManager();
      String name = NamespaceUtil.getPrefixedName(namespaceManager, resource.getName());
      s += "<INVALID_" + resourceType;
      s += "[" + name + "]>";
    } else if (o instanceof RDFSLiteral) s += SWRLUtil.getSWRLBrowserText((RDFSLiteral)o); 
    else if (o instanceof SWRLAtomList) s += ((SWRLAtomList)o).getBrowserText();
    else if (o instanceof SWRLAtom) s += ((SWRLAtom)o).getBrowserText();
    else if (o instanceof SWRLVariable) s += ((SWRLVariable)o).getBrowserText();
    else if (o instanceof RDFSClass 
               || o instanceof RDFIndividual 
               || o instanceof RDFProperty) {
    	String bt = ((RDFResource) o).getBrowserText(); // TODO: test should go away when quoteIfNeeded is fixed
    	s += bt.startsWith("'") ? bt : ParserUtils.quoteIfNeeded(bt);
    }
    else if (o instanceof OWLDataRange) s += ((OWLDataRange)o).getBrowserText();
    else if (o instanceof RDFResource) s += ((RDFResource)o).getBrowserText();
    else s += o.toString();
      
    s += "";

    return s;      
  } // getSWRLBrowserText

} // SWRLUtil
	
