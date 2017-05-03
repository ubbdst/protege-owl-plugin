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

package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Disposable;

public class MultipleTypesInstanceCache implements Disposable {

	//maybe it should be singleton?

	private HashMap<Instance, Collection<Cls>> instWithMultipleTypesMap = new HashMap<Instance, Collection<Cls>>();

	public void addType(Instance inst, Cls type) {
		Collection<Cls> types = getTypesForInstance(inst);
		types.add(type);
		instWithMultipleTypesMap.put(inst, types);
	}

	public Collection<Cls> getTypesForInstance(Instance inst) {
		Collection<Cls> types = instWithMultipleTypesMap.get(inst);
		if (types == null) {
			types = new ArrayList<Cls>();
		}

		return types;
	}

	public Set<Instance> getInstancesWithMultipleTypes() {
		return instWithMultipleTypesMap.keySet();
	}

	public Set<Cls> getTypesForInstanceAsSet(Instance inst) {
		return new HashSet<Cls>(getTypesForInstance(inst));
	}

	public void dispose() {
		instWithMultipleTypesMap.clear();
	}

}
