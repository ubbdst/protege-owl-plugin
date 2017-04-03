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


package edu.stanford.smi.protegex.owl.swrl.model;

import java.util.Set;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;

/**
 * @author Martin O'Connor <moconnor@smi.stanford.edu>
 * @author Holger Knublauch <holger@knublauch.com>
 */
public interface SWRLImp extends SWRLIndividual
{
	SWRLImp createClone();

	/**
	 * Deletes this and all depending objects of the rule.
	 */
	void deleteImp();

	Set<RDFResource> getReferencedInstances();

	SWRLAtomList getBody();

	void setBody(SWRLAtomList swrlAtomList);

	SWRLAtomList getHead();

	void setHead(SWRLAtomList swrlAtomList);

	/**
	 * Tries to parse the given text to create head and body of this Imp. This will replace the old content. This method can be used to implement editing of
	 * existing rules without deleting them.
	 * 
	 * @param parsableText
	 *          a SWRL expression
	 */
	void setExpression(String parsableText) throws SWRLParseException;

	// Annotation-driven methods
	boolean isEnabled();

	void enable();

	void disable();

	Set<String> getRuleGroupNames();

	boolean addRuleGroup(String name); // Return true on successful addition

	boolean removeRuleGroup(String name); // Return true on successful deletion

	boolean isInRuleGroups(Set<String> names);

	boolean isInRuleGroup(String name);

	void enable(String ruleGroupName);

	void enable(Set<String> ruleGroupNames);

	void disable(String ruleGroupName);

	void disable(Set<String> ruleGroupNames);
}
