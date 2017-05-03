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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.SWRLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;

/**
 * A utility class that can (and should) be used to create and access SWRL related objects in an ontology.
 * 
 * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLFactoryFAQ">here</a> for documentation on using this class.
 */
public class SWRLFactory
{
	private final OWLModel owlModel;
	private final SWRLSystemFrames systemFrames;

	public SWRLFactory(OWLModel owlModel)
	{
		this.owlModel = owlModel;
		this.systemFrames = owlModel.getSystemFrames();
	}

	public SWRLImp createImp()
	{
		String name = getNewImpName();
		return (SWRLImp)this.systemFrames.getImpCls().createInstance(name);
	}

	public SWRLImp createImpWithGivenName(String name)
	{
		RDFSClass impCls = this.owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP);
		return (SWRLImp)impCls.createInstance(name);
	}

	public SWRLImp createImp(String expression) throws SWRLParseException
	{
		SWRLParser parser = new SWRLParser(this.owlModel);
		parser.setParseOnly(false);
		return parser.parse(expression);
	}

	public SWRLImp createImp(String name, String expression) throws SWRLParseException
	{
		SWRLParser parser = new SWRLParser(this.owlModel);
		SWRLImp imp = createImpWithGivenName(name);
		parser.setParseOnly(false);
		return parser.parse(expression, imp);
	} // createImp

	public SWRLImp createImp(SWRLAtom headAtom, List<SWRLAtom> bodyAtoms)
	{
		SWRLAtomList head = createAtomList(Collections.singleton(headAtom));
		SWRLAtomList body = createAtomList(bodyAtoms);
		return createImp(head, body);
	}

	public SWRLImp createImp(SWRLAtomList head, SWRLAtomList body)
	{
		SWRLImp swrlImp = createImp();
		swrlImp.setHead(head);
		swrlImp.setBody(body);
		head.setInHead(true);
		head.setInHead(false);
		return swrlImp;
	} // SWRLImp

	public SWRLAtomList createAtomList()
	{
		return (SWRLAtomList)this.systemFrames.getAtomListCls().createAnonymousInstance();
	} // createAtomList

	public SWRLAtomList createAtomList(Collection<SWRLAtom> atoms)
	{
		SWRLAtomList list = createAtomList();
		for (Iterator<SWRLAtom> it = atoms.iterator(); it.hasNext();) {
			Object o = it.next();
			list.append(o);
		}
		return list;
	}

	public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin, Iterator<RDFObject> arguments)
	{
		RDFList li = this.owlModel.createRDFList(arguments);
		return createBuiltinAtom(swrlBuiltin, li);
	} // createBuiltinAtom

	public SWRLBuiltinAtom createBuiltinAtom(SWRLBuiltin swrlBuiltin, RDFList arguments)
	{
		SWRLBuiltinAtom swrlBuiltinAtom;

		swrlBuiltinAtom = (SWRLBuiltinAtom)this.systemFrames.getBuiltinAtomCls().createAnonymousInstance();

		swrlBuiltinAtom.setBuiltin(swrlBuiltin);
		swrlBuiltinAtom.setArguments(arguments);

		return swrlBuiltinAtom;
	} // createBuiltinAtom

	public SWRLClassAtom createClassAtom(RDFSNamedClass aClass, RDFResource iObject)
	{
		SWRLClassAtom swrlClassAtom;

		swrlClassAtom = (SWRLClassAtom)this.systemFrames.getClassAtomCls().createAnonymousInstance();

		swrlClassAtom.setClassPredicate(aClass);
		swrlClassAtom.setArgument1(iObject);

		return swrlClassAtom;

	} // createClassAtom

	public SWRLDataRangeAtom createDataRangeAtom(OWLDataRange dataRange, RDFObject dObject)
	{
		SWRLDataRangeAtom swrlDataRangeAtom = (SWRLDataRangeAtom)this.systemFrames.getDataRangeAtomCls().createAnonymousInstance();

		swrlDataRangeAtom.setArgument1(dObject);
		swrlDataRangeAtom.setDataRange(dataRange);

		return swrlDataRangeAtom;
	} // createDataRangeAtom

	public SWRLDatavaluedPropertyAtom createDatavaluedPropertyAtom(OWLDatatypeProperty datatypeSlot, RDFResource iObject, RDFObject dObject)
	{
		SWRLDatavaluedPropertyAtom swrlDatavaluedPropertyAtom = (SWRLDatavaluedPropertyAtom)this.systemFrames.getDataValuedPropertyAtomCls()
				.createAnonymousInstance();

		swrlDatavaluedPropertyAtom.setPropertyPredicate(datatypeSlot);
		swrlDatavaluedPropertyAtom.setArgument1(iObject);
		swrlDatavaluedPropertyAtom.setArgument2(dObject);

		return swrlDatavaluedPropertyAtom;

	} // createDatavaluedPropertyAtom

	public SWRLIndividualPropertyAtom createIndividualPropertyAtom(OWLObjectProperty objectSlot, RDFResource iObject1, RDFResource iObject2)
	{
		SWRLIndividualPropertyAtom swrlIndividualPropertyAtom;

		swrlIndividualPropertyAtom = (SWRLIndividualPropertyAtom)this.systemFrames.getIndividualPropertyAtomCls().createAnonymousInstance();

		swrlIndividualPropertyAtom.setPropertyPredicate(objectSlot);
		swrlIndividualPropertyAtom.setArgument1(iObject1);
		swrlIndividualPropertyAtom.setArgument2(iObject2);

		return swrlIndividualPropertyAtom;
	} // createIndividualPropertyAtom

	public SWRLDifferentIndividualsAtom createDifferentIndividualsAtom(RDFResource argument1, RDFResource argument2)
	{
		SWRLDifferentIndividualsAtom swrlDifferentIndividualsAtom;

		swrlDifferentIndividualsAtom = (SWRLDifferentIndividualsAtom)this.systemFrames.getDifferentIndividualsAtomCls().createAnonymousInstance();
		swrlDifferentIndividualsAtom.setArgument1(argument1);
		swrlDifferentIndividualsAtom.setArgument2(argument2);

		return swrlDifferentIndividualsAtom;
	} // createDifferentIndividualsAtom

	public SWRLSameIndividualAtom createSameIndividualAtom(RDFResource argument1, RDFResource argument2)
	{
		SWRLSameIndividualAtom swrlSameIndividualAtom;

		swrlSameIndividualAtom = (SWRLSameIndividualAtom)this.systemFrames.getSameIndividualAtomCls().createAnonymousInstance();
		swrlSameIndividualAtom.setArgument1(argument1);
		swrlSameIndividualAtom.setArgument2(argument2);

		return swrlSameIndividualAtom;
	} // createSameIndividualAtom

	public SWRLVariable createVariable(String name)
	{
		return (SWRLVariable)this.owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE).createInstance(name);
	} // createVariable

	public SWRLBuiltin createBuiltin(String name)
	{
		return (SWRLBuiltin)this.owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN).createInstance(name);
	} // createBuiltin

	public SWRLBuiltin getBuiltin(String name)
	{
		RDFResource resource = this.owlModel.getRDFResource(name);
		if (resource instanceof SWRLBuiltin) {
			return (SWRLBuiltin)resource;
		} else {
			System.err.println("[SWRLFactory]  Invalid attempt to cast " + name + " into SWRLBuiltin (real type is " + resource.getProtegeType() + ")");
			return null;
		}
	} // getBuiltin

	public Collection<SWRLBuiltin> getBuiltins()
	{
		RDFSNamedClass builtinCls = this.owlModel.getRDFSNamedClass(SWRLNames.Cls.BUILTIN);
		Set<SWRLBuiltin> result = new HashSet<SWRLBuiltin>();

		for (Object o : builtinCls.getInstances(true))
			if (o instanceof SWRLBuiltin)
				result.add((SWRLBuiltin)o);

		return result;
	}

	public Collection<SWRLImp> getImps()
	{
		Collection<SWRLImp> imps = new HashSet<SWRLImp>();

		for (Object o : this.systemFrames.getImpCls().getInstances(true))
			if (o instanceof SWRLImp)
				imps.add((SWRLImp)o);

		return imps;
	} // getImps

	public Collection<SWRLImp> getEnabledImps()
	{
		return getImps(new HashSet<String>(), true);
	}

	public Collection<SWRLImp> getEnabledImps(Set<String> ruleGroupNames)
	{
		return getImps(ruleGroupNames, true);
	}

	public Collection<SWRLImp> getEnabledImps(String ruleGroupName)
	{
		Set<String> ruleGroupNames = new HashSet<String>();
		ruleGroupNames.add(ruleGroupName);
		return getImps(ruleGroupNames, true);
	}

	// If the ruleGroupNames is empty, return all imps.
	private Collection<SWRLImp> getImps(Set<String> ruleGroupNames, boolean isEnabled)
	{
		Collection<SWRLImp> result = new ArrayList<SWRLImp>();
		Collection<SWRLImp> imps = getImps();

		if (imps != null) {
			for (SWRLImp imp : imps) {
				if (ruleGroupNames.isEmpty() || imp.isInRuleGroups(ruleGroupNames)) {
					if (imp.isEnabled() == isEnabled)
						result.add(imp);
				}
			}
		}
		return result;
	}

	public void deleteImps()
	{
		for (SWRLImp imp : getImps())
			imp.deleteImp();
	}

	public void deleteImp(String name) throws SWRLFactoryException
	{
		getImp(name).deleteImp();
	}

	public void replaceImps(OWLModel sourceOWLModel) throws SWRLFactoryException
	{
		deleteImps();
		copyImps(sourceOWLModel);
	} // replaceImps

	public void copyImps(OWLModel sourceOWLModel) throws SWRLFactoryException
	{
		SWRLFactory sourceSWRLFactory = new SWRLFactory(sourceOWLModel);

		for (Object o : sourceSWRLFactory.getImps()) {
			SWRLImp imp = (SWRLImp)o;
			String ruleName = imp.getLocalName();
			String expression = imp.getBrowserText();

			if (hasImp(ruleName))
				throw new SWRLFactoryException("attempt to copy rule '" + ruleName + "' that has same name as an existing rule");

			try {
				createImp(ruleName, expression);
			} catch (SWRLParseException e) {
				throw new SWRLFactoryException("error copying rule '" + ruleName + "': " + e.getMessage());
			}
		}
	}

	public SWRLImp getImp(String name) throws SWRLFactoryException
	{
		RDFResource resource = this.owlModel.getRDFResource(name);
		SWRLImp result = null;

		if (resource instanceof SWRLImp || resource == null)
			result = (SWRLImp)resource;
		else
			throw new SWRLFactoryException("invalid attempt to cast " + name + " into SWRLImp (real type is " + resource.getProtegeType() + ")");

		return result;
	} // getImp

	public boolean hasImp(String name)
	{
		RDFResource resource = this.owlModel.getRDFResource(name);

		return (resource != null) && (resource instanceof SWRLImp);
	} // hasImp

	public String getNewImpName()
	{
		String prefix = this.owlModel.getNamespaceManager().getDefaultNamespace();
		String base = prefix + "Rule-";
		int i = Math.max(1, this.systemFrames.getImpCls().getInstances(false).size());
		while (this.owlModel.getRDFResource(base + i) != null) {
			i++;
		}
		return base + i;
	}

	public SWRLVariable getVariable(String name)
	{
		return (SWRLVariable)this.owlModel.getRDFResource(name);
	}

	public Collection<SWRLVariable> getVariables()
	{
		RDFSClass variableCls = this.owlModel.getRDFSNamedClass(SWRLNames.Cls.VARIABLE);
		Set<SWRLVariable> result = new HashSet<SWRLVariable>();

		for (Object o : variableCls.getInstances(true))
			if (o instanceof SWRLVariable)
				result.add((SWRLVariable)o);

		return result;
	}

	public Collection<SWRLImp> getReferencedImps(RDFResource rdfResource)
	{
		Collection<SWRLImp> result = new ArrayList<SWRLImp>();

		if (rdfResource != null) {
			for (SWRLImp imp : getImps()) {
				Set<RDFResource> resources = imp.getReferencedInstances();
				if (resources.contains(rdfResource) && !result.contains(imp))
					result.add(imp);
			} // while
		} // if
		return result;
	} 

	public void enableAll()
	{
		enableStatusUpdate(new HashSet<String>(), true);
	}

	public void disableAll()
	{
		enableStatusUpdate(new HashSet<String>(), false);
	}

	public void enableAll(Set<String> ruleGroupNames)
	{
		enableStatusUpdate(ruleGroupNames, true);
	}

	public void disableAll(Set<String> ruleGroupNames)
	{
		enableStatusUpdate(ruleGroupNames, false);
	}

	public void enableAll(String ruleGroupName)
	{
		Set<String> ruleGroupNames = new HashSet<String>();
		ruleGroupNames.add(ruleGroupName);
		enableStatusUpdate(ruleGroupNames, true);
	}

	public void disableAll(String ruleGroupName)
	{
		Set<String> ruleGroupNames = new HashSet<String>();
		ruleGroupNames.add(ruleGroupName);
		enableStatusUpdate(ruleGroupNames, false);
	}

	public static boolean isSWRLFilteredResource(RDFResource resource)
	{
		return (resource instanceof SWRLAtomList) || (resource instanceof SWRLBuiltinAtom) || (resource instanceof SWRLClassAtom)
				|| (resource instanceof SWRLDataRangeAtom) || (resource instanceof SWRLDatavaluedPropertyAtom) || (resource instanceof SWRLDifferentIndividualsAtom)
				|| (resource instanceof SWRLImp) || (resource instanceof SWRLIndividualPropertyAtom) || (resource instanceof SWRLSameIndividualAtom)
				|| (resource instanceof SWRLAtom) || (resource instanceof SWRLVariable);
	}

	// Add SWRLBuiltin to list above.
	public static boolean isSWRLResource(RDFResource resource)
	{
		return (resource instanceof SWRLAtomList) || (resource instanceof SWRLBuiltinAtom) || (resource instanceof SWRLClassAtom)
				|| (resource instanceof SWRLDataRangeAtom) || (resource instanceof SWRLDatavaluedPropertyAtom) || (resource instanceof SWRLDifferentIndividualsAtom)
				|| (resource instanceof SWRLImp) || (resource instanceof SWRLIndividualPropertyAtom) || (resource instanceof SWRLSameIndividualAtom)
				|| (resource instanceof SWRLBuiltin) || (resource instanceof SWRLAtom) || (resource instanceof SWRLVariable);
	}

	private void enableStatusUpdate(Set<String> ruleGroupNames, boolean enable)
	{
		for (SWRLImp imp : getImps()) {
			if (ruleGroupNames.isEmpty() || imp.isInRuleGroups(ruleGroupNames)) {
				if (enable)
					imp.enable();
				else
					imp.disable();
			} 
		}
	}

	public Collection<RDFSNamedClass> getSWRLClasses()
	{
		ArrayList<RDFSNamedClass> swrlClasses = new ArrayList<RDFSNamedClass>();
		swrlClasses.add(this.systemFrames.getAtomListCls());
		swrlClasses.add(this.systemFrames.getBuiltinAtomCls());
		swrlClasses.add(this.systemFrames.getClassAtomCls());
		swrlClasses.add(this.systemFrames.getDataRangeAtomCls());
		swrlClasses.add(this.systemFrames.getDataValuedPropertyAtomCls());
		swrlClasses.add(this.systemFrames.getDifferentIndividualsAtomCls());
		swrlClasses.add(this.systemFrames.getImpCls());
		swrlClasses.add(this.systemFrames.getIndividualPropertyAtomCls());
		swrlClasses.add(this.systemFrames.getSameIndividualAtomCls());
		swrlClasses.add(this.systemFrames.getBuiltInCls());
		swrlClasses.add(this.systemFrames.getAtomCls());
		swrlClasses.add(this.systemFrames.getVariableCls());

		return swrlClasses;
	}

	public Collection<RDFProperty> getSWRLProperties()
	{
		ArrayList<RDFProperty> swrlProperties = new ArrayList<RDFProperty>();
		swrlProperties.add(this.systemFrames.getBodyProperty());
		swrlProperties.add(this.systemFrames.getHeadProperty());
		swrlProperties.add(this.systemFrames.getArgumentsProperty());
		swrlProperties.add(this.systemFrames.getBuiltInProperty());
		swrlProperties.add(this.systemFrames.getArgument1Property());
		swrlProperties.add(this.systemFrames.getArgument2Property());
		swrlProperties.add(this.systemFrames.getClassPredicateProperty());
		swrlProperties.add(this.systemFrames.getPropertyPredicateProperty());
		swrlProperties.add(this.systemFrames.getDataRangeProperty());

		return swrlProperties;
	}

	public Collection<RDFProperty> getSWRLBProperties()
	{
		ArrayList<RDFProperty> swrlbProperties = new ArrayList<RDFProperty>();

		RDFProperty swrlbArgs = this.owlModel.getRDFProperty(SWRLNames.Slot.ARGS);
		if (swrlbArgs != null) {
			swrlbProperties.add(swrlbArgs);
		}

		RDFProperty swrlbMinArgs = this.owlModel.getRDFProperty(SWRLNames.Slot.MIN_ARGS);
		if (swrlbMinArgs != null) {
			swrlbProperties.add(swrlbMinArgs);
		}

		RDFProperty swrlbMaxArgs = this.owlModel.getRDFProperty(SWRLNames.Slot.MAX_ARGS);
		if (swrlbMaxArgs != null) {
			swrlbProperties.add(swrlbMaxArgs);
		}

		return swrlbProperties;
	}

	@SuppressWarnings("unchecked")
	// To deal with groady non generics Protege-OWL API
	public boolean areImpsEqual(SWRLImp imp1, SWRLImp imp2)
	{
		Iterator<SWRLAtom> iterator1, iterator2;

		iterator1 = imp1.getBody().getValues().iterator();
		iterator2 = imp2.getBody().getValues().iterator();

		if (!areAtomListsEqual(iterator1, iterator2))
			return false;

		iterator1 = imp1.getHead().getValues().iterator();
		iterator2 = imp2.getHead().getValues().iterator();

		return areAtomListsEqual(iterator1, iterator2);
	}

	private boolean areAtomListsEqual(Iterator<SWRLAtom> iterator1, Iterator<SWRLAtom> iterator2)
	{
		SWRLAtom atom1, atom2;

		while (iterator1.hasNext()) {
			if (!iterator2.hasNext())
				return false;

			atom1 = iterator1.next();
			atom2 = iterator2.next();

			if (!areAtomsEqual(atom1, atom2))
				return false;
		} // for

		return !iterator2.hasNext();
	}

	private boolean areAtomsEqual(SWRLAtom atom1, SWRLAtom atom2)
	{
		if (atom1 instanceof SWRLBuiltinAtom)
			return (atom2 instanceof SWRLBuiltinAtom) && areBuiltInAtomsEqual((SWRLBuiltinAtom)atom1, (SWRLBuiltinAtom)atom2);
		else if (atom1 instanceof SWRLClassAtom)
			return (atom2 instanceof SWRLClassAtom) && areClassAtomsEqual((SWRLClassAtom)atom1, (SWRLClassAtom)atom2);
		else if (atom1 instanceof SWRLDatavaluedPropertyAtom)
			return (atom2 instanceof SWRLDatavaluedPropertyAtom)
					&& areDatavaluedPropertyAtomsEqual((SWRLDatavaluedPropertyAtom)atom1, (SWRLDatavaluedPropertyAtom)atom2);
		else if (atom1 instanceof SWRLIndividualPropertyAtom)
			return (atom2 instanceof SWRLIndividualPropertyAtom)
					&& areIndividualPropertyAtomsEqual((SWRLIndividualPropertyAtom)atom1, (SWRLIndividualPropertyAtom)atom2);
		else if (atom1 instanceof SWRLDifferentIndividualsAtom)
			return (atom2 instanceof SWRLDifferentIndividualsAtom)
					&& areDifferentIndividualsAtomsEqual((SWRLDifferentIndividualsAtom)atom1, (SWRLDifferentIndividualsAtom)atom2);
		else if (atom1 instanceof SWRLSameIndividualAtom)
			return (atom2 instanceof SWRLSameIndividualAtom) && areSameIndividualAtomsEqual((SWRLSameIndividualAtom)atom1, (SWRLSameIndividualAtom)atom2);
		else if (atom1 instanceof SWRLDataRangeAtom)
			return (atom2 instanceof SWRLDataRangeAtom) && areDataRangeAtomsEqual((SWRLDataRangeAtom)atom1, (SWRLDataRangeAtom)atom2);
		else
			throw new RuntimeException("unknowl SWRL atom type " + atom1.getLocalName());
	}

	private boolean areBuiltInAtomsEqual(SWRLBuiltinAtom atom1, SWRLBuiltinAtom atom2)
	{
		if (!atom1.getBuiltin().getURI().equals(atom2.getBuiltin().getURI()))
			return false;
		RDFList rdfList1 = atom1.getArguments();
		RDFList rdfList2 = atom2.getArguments();

		Iterator<?> iterator1 = rdfList1.getValues().iterator();
		Iterator<?> iterator2 = rdfList2.getValues().iterator();
		while (iterator1.hasNext()) {
			if (!iterator2.hasNext())
				return false;

			Object argument1 = iterator1.next();
			Object argument2 = iterator2.next();

			if (!areAtomArgumentsEqual(argument1, argument2))
				return false;
		}

		return !iterator2.hasNext();
	}

	private boolean areClassAtomsEqual(SWRLClassAtom atom1, SWRLClassAtom atom2)
	{
		return atom1.getClassPredicate().getURI().equals(atom2.getClassPredicate().getURI()) && areAtomArgumentsEqual(atom1.getArgument1(), atom2.getArgument1());
	}

	private boolean areIndividualPropertyAtomsEqual(SWRLIndividualPropertyAtom atom1, SWRLIndividualPropertyAtom atom2)
	{
		return atom1.getPropertyPredicate().getURI().equals(atom2.getPropertyPredicate().getURI())
				&& areAtomArgumentsEqual(atom1.getArgument1(), atom2.getArgument1()) && areAtomArgumentsEqual(atom1.getArgument2(), atom2.getArgument2());
	}

	private boolean areDatavaluedPropertyAtomsEqual(SWRLDatavaluedPropertyAtom atom1, SWRLDatavaluedPropertyAtom atom2)
	{
		return atom1.getPropertyPredicate().getURI().equals(atom2.getPropertyPredicate().getURI())
				&& areAtomArgumentsEqual(atom1.getArgument1(), atom2.getArgument1()) && areAtomArgumentsEqual(atom1.getArgument2(), atom2.getArgument2());
	}

	private boolean areSameIndividualAtomsEqual(SWRLSameIndividualAtom atom1, SWRLSameIndividualAtom atom2)
	{
		return areAtomArgumentsEqual(atom1.getArgument1(), atom2.getArgument1()) && areAtomArgumentsEqual(atom1.getArgument2(), atom2.getArgument2());
	}

	private boolean areDifferentIndividualsAtomsEqual(SWRLDifferentIndividualsAtom atom1, SWRLDifferentIndividualsAtom atom2)
	{
		return areAtomArgumentsEqual(atom1.getArgument1(), atom2.getArgument1()) && areAtomArgumentsEqual(atom1.getArgument2(), atom2.getArgument2());
	}

	private boolean areDataRangeAtomsEqual(SWRLDataRangeAtom atom1, SWRLDataRangeAtom atom2)
	{
		throw new RuntimeException("data range atoms not implemented");
	}

	private boolean areAtomArgumentsEqual(Object argument1, Object argument2)
	{
		if (argument1 instanceof RDFResource) {
			if (!(argument2 instanceof RDFResource))
				return false;

			RDFResource resource1 = (RDFResource)argument1;
			RDFResource resource2 = (RDFResource)argument2;

			return resource1.getURI().equals(resource2.getURI());
		} else if (argument1 instanceof RDFSLiteral) {
			if (!(argument2 instanceof RDFSLiteral))
				return false;

			RDFSLiteral literal1 = (RDFSLiteral)argument1;
			RDFSLiteral literal2 = (RDFSLiteral)argument2;

			return literal1.compareTo(literal2) == 0;
		} else if (argument1 instanceof String) {
			if (!(argument2 instanceof String))
				return false;

			String s1 = (String)argument1;
			String s2 = (String)argument2;

			return s1.equals(s2);
		} else { // TODO This will work for the type of arguments the SWRL atoms get, but is not nice in general
			if (argument1 instanceof Comparable)
				return ((Comparable)argument1).compareTo(argument2) != 0;
			else
				return false;
		}
	}
}
