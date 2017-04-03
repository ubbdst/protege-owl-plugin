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


package edu.stanford.smi.protegex.owl.swrl.ui.code;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.ui.code.OWLResourceNameMatcher;

public class SWRLResourceNameMatcher extends OWLResourceNameMatcher
{
  public String getInsertString(RDFResource resource) 
  {
  	if (resource instanceof SWRLVariable) return "?" + resource.getPrefixedName();
  	else return super.getInsertString(resource);
  } 

  public Set<RDFResource> getMatchingResources(String prefix, String leftString, OWLModel owlModel) 
  {
  	if (leftString.endsWith("?")) {
  		Set<RDFResource> resources = new HashSet<RDFResource>();
  		for (Iterator<?> it = owlModel.getOWLNamedClass(SWRLNames.Cls.VARIABLE).getInstances(true).iterator(); it.hasNext();) {
  			SWRLVariable var = (SWRLVariable)it.next();
  			if (var.getName().startsWith(prefix)) resources.add(var);
  		} // for
  		return resources;
  	} else return super.getMatchingResources(prefix, leftString, owlModel);
  } 
  
  @Override
  protected boolean couldBeClass(OWLModel owlModel, String prefix) { return true; }
  
  @Override
  protected boolean couldBeProperty(OWLModel owlModel, String prefix) { return true; }
  
  @Override
  protected boolean couldBeIndividual(OWLModel owlModel, String prefix) { return true; }
    
  @Override
  protected boolean couldBeDatatype(OWLModel owlModel, String prefix) { return true; }
} 
