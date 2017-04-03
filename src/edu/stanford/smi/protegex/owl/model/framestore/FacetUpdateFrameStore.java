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

package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.framestore.updater.AbstractRestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.framestore.updater.AllValuesFromRestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.framestore.updater.CardinalityRestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.framestore.updater.HasValueRestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.framestore.updater.RestrictionUpdater;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class FacetUpdateFrameStore extends FrameStoreAdapter {

	private OWLModel owlModel;
	/**
	 * A flag to prevent infinite recursion when a facet override has been changed.
	 */
	protected boolean superclassHandlingBlocked;

	/**
	 * A Hashtable from Java restriction Class objects to the responsible RestrictionUpdaters
	 */
	private Hashtable<Class, AbstractRestrictionUpdater> class2Updater = new Hashtable<Class, AbstractRestrictionUpdater>();

	/**
	 * A Hashtable from Facets to the responsible RestrictionUpdaters
	 */
	private Hashtable<Facet, AbstractRestrictionUpdater> facet2Updater = new Hashtable<Facet, AbstractRestrictionUpdater>();

	/**
	 * A flag to prevent infinite recursion when a superclass has been added or removed
	 */
	protected boolean facetHandlingBlocked;

	private AllValuesFromRestrictionUpdater allValuesFromRestrictionUpdater;

	private CardinalityRestrictionUpdater cardinalityRestrictionUpdater;

	private HasValueRestrictionUpdater hasValueRestrictionUpdater;

	public FacetUpdateFrameStore(OWLModel owlModel) {
		this.owlModel = owlModel;
		initRestrictionUpdaters();
	}

	private void initRestrictionUpdaters() {

		allValuesFromRestrictionUpdater = new AllValuesFromRestrictionUpdater(owlModel);
		cardinalityRestrictionUpdater = new CardinalityRestrictionUpdater(owlModel);
		hasValueRestrictionUpdater = new HasValueRestrictionUpdater(owlModel);

		facet2Updater.put(owlModel.getSystemFrames().getValueTypeFacet(), allValuesFromRestrictionUpdater);
		facet2Updater.put(owlModel.getSystemFrames().getMaximumCardinalityFacet(), cardinalityRestrictionUpdater);
		facet2Updater.put(owlModel.getSystemFrames().getMinimumCardinalityFacet(), cardinalityRestrictionUpdater);
		facet2Updater.put(owlModel.getSystemFrames().getValuesFacet(), hasValueRestrictionUpdater);

		// TODO: This should be generalized, independent from Default implementation classes
		class2Updater.put(DefaultOWLAllValuesFrom.class, allValuesFromRestrictionUpdater);
		class2Updater.put(DefaultOWLCardinality.class, cardinalityRestrictionUpdater);
		class2Updater.put(DefaultOWLHasValue.class, hasValueRestrictionUpdater);
		class2Updater.put(DefaultOWLMaxCardinality.class, cardinalityRestrictionUpdater);
		class2Updater.put(DefaultOWLMinCardinality.class, cardinalityRestrictionUpdater);
	}



	@SuppressWarnings("deprecation")
	public void copyFacetValuesIntoNamedClses() {
		boolean oldUndo = owlModel.isUndoEnabled();
		TripleStoreModel tsm = owlModel.getTripleStoreModel();

		TripleStore activeTripleStore = tsm.getActiveTripleStore();

		owlModel.setUndoEnabled(false);

		try {
			for (TripleStore ts : tsm.getTripleStores()) {
				tsm.setActiveTripleStore(ts);

				for (OWLRestriction restriction : getRestrictionsForTS(ts)) {
					copyFacetValuesIntoOWLNamedClass(restriction);
				}
			}
		} finally {
			owlModel.setUndoEnabled(oldUndo);
			owlModel.getTripleStoreModel().setActiveTripleStore(activeTripleStore);
		}
	}


	/**
	 * Funky method to get all the restrictions defined in a triplestore
	 * @param ts
	 * @return
	 */
	private Collection<OWLRestriction> getRestrictionsForTS(TripleStore ts) {
		Collection<OWLRestriction> restrictions = new ArrayList<OWLRestriction>();

		RDFSNamedClass restrictionClass = ((AbstractOWLModel) owlModel).getOWLRestrictionClass();

		for (Iterator iterator = restrictionClass.getSubclasses(true).iterator(); iterator.hasNext();) {
			RDFSNamedClass restrictionType = (RDFSNamedClass) iterator.next();

			NarrowFrameStore nfs = ts.getNarrowFrameStore();
			Collection insts = nfs.getValues(restrictionType, owlModel.getSystemFrames().getDirectInstancesSlot(), null, false);

			for (Iterator iterator2 = insts.iterator(); iterator2.hasNext();) {
			    Object inst = iterator2.next();
				if (inst instanceof OWLRestriction) {
					restrictions.add((OWLRestriction)inst);
				}
			}
		}

		return restrictions;
	}



	private void copyFacetValuesIntoOWLNamedClass(OWLRestriction restriction) {
	    Object[] restrictedClasses = restriction.getSubclasses(false).toArray();
		if (restrictedClasses.length == 1 && restrictedClasses[0] instanceof RDFSNamedClass) {
			RDFSNamedClass namedCls = (RDFSNamedClass) restrictedClasses[0];
			copyFacetValuesIntoOWLNamedClass(namedCls, restriction);
		}
	}

	private void copyFacetValuesIntoOWLNamedClass(RDFSNamedClass cls, OWLRestriction restriction) {
		Class clazz = restriction.getClass();
		RestrictionUpdater ru = class2Updater.get(clazz);
		if (ru != null) {
			facetHandlingBlocked = true;
			ru.copyFacetValuesIntoNamedClass(cls, restriction);
			facetHandlingBlocked = false;
		}
	}


	private void updateRestrictions(OWLNamedClass cls, RDFProperty slot, Facet facet) {
		RestrictionUpdater ru = facet2Updater.get(facet);
		if (ru != null) {
			superclassHandlingBlocked = true;
			ru.updateRestrictions(cls, slot, facet);
			superclassHandlingBlocked = false;
		}
	}

	/*
	 * FrameStore implementations
	 */

	 @Override
	 public Slot createSlot(FrameID id, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
		 Slot slot = super.createSlot(id, directTypes, directSuperslots, loadDefaults);
		 if (slot instanceof RDFProperty) {
			 RDFProperty rdfProperty = (RDFProperty) slot;
			 slot.setAllowsMultipleValues(true);
		 }
		 return slot;
	 }

	 @Override
	 public void addDirectSuperclass(Cls cls, Cls superCls) {
		 super.addDirectSuperclass(cls, superCls);
		 if (!superclassHandlingBlocked && cls instanceof OWLNamedClass) {
			 OWLNamedClass namedCls = (OWLNamedClass) cls;
			 if (superCls instanceof OWLRestriction) {
				 copyFacetValuesIntoOWLNamedClass(namedCls, (OWLRestriction) superCls);
			 }
		 }
	 }

	 @Override
	 public void removeDirectSuperclass(Cls cls, Cls superCls) {
		 super.removeDirectSuperclass(cls, superCls);
		 if (!superclassHandlingBlocked) {
			 if (cls instanceof OWLNamedClass && superCls instanceof OWLRestriction) {
				 copyFacetValuesIntoOWLNamedClass((OWLNamedClass) cls, (OWLRestriction) superCls);
			 }
		 }
	 }

	 @Override
	 public void setDirectTemplateFacetValues(Cls cls, Slot slot, Facet facet, Collection values) {

		 super.setDirectTemplateFacetValues(cls, slot, facet, values);

		 if (!facetHandlingBlocked) {
			 if (cls instanceof OWLNamedClass && slot instanceof RDFProperty) {
				 updateRestrictions((OWLNamedClass) cls, (RDFProperty) slot, facet);
			 }
		 }

		 if (!superclassHandlingBlocked) {
			 if (cls instanceof OWLRestriction) {
				 OWLRestriction restriction = (OWLRestriction) cls;
				 copyFacetValuesIntoOWLNamedClass(restriction);
			 }
		 }
	 }

	 @Override
	 public void setDirectTemplateSlotValues(Cls cls, Slot slot, Collection values) {
		 super.setDirectTemplateSlotValues(cls, slot, values);

		 if (!facetHandlingBlocked) {
			 if (cls instanceof OWLNamedClass && slot instanceof RDFProperty) {
				 updateRestrictions((OWLNamedClass) cls, (RDFProperty) slot, owlModel.getFacet(Model.Facet.VALUES));
			 }
		 }
	 }

}
