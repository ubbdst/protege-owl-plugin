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

/**
 *
 */
package edu.stanford.smi.protegex.owl.swrl.model.factory;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;


public enum SWRLFrameTypeId {
	/*
	 * Don't change the order!
	 */
	SWRL_ATOM_LIST(SWRLNames.Cls.ATOM_LIST, DefaultSWRLAtomList.class),
    SWRL_BUILTIN_ATOM(SWRLNames.Cls.BUILTIN_ATOM, DefaultSWRLBuiltinAtom.class),
    SWRL_CLASS_ATOM(SWRLNames.Cls.CLASS_ATOM, DefaultSWRLClassAtom.class),
    SWRL_DATARANGE_ATOM(SWRLNames.Cls.DATA_RANGE_ATOM, DefaultSWRLDataRangeAtom.class),
    SWRL_DATAVALUED_PROPERTY_ATOM(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM, DefaultSWRLDatavaluedPropertyAtom.class),
    SWRL_INDIVIDUAL_PROPERTY_ATOM(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM, DefaultSWRLIndividualPropertyAtom.class),
    SWRL_DIFFERENT_INDIVIDUALS_ATOM(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM, DefaultSWRLDifferentIndividualsAtom.class),
    SWRL_SAME_INDIVIDUAL_ATOM(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM, DefaultSWRLSameIndividualAtom.class),
    SWRL_BUILTIN(SWRLNames.Cls.BUILTIN, DefaultSWRLBuiltin.class),
    SWRL_IMP(SWRLNames.Cls.IMP, DefaultSWRLImp.class),
    SWRL_VARIABLE(SWRLNames.Cls.VARIABLE, DefaultSWRLVariable.class);


	public final static int SWRL_FRAME_TYPE_ID_BASE = 42; // leave some room for expansion of the owl java class ids.
    public final static int SWRL_FRAME_TYPE_ID_END  = SWRL_FRAME_TYPE_ID_BASE + SWRLFrameTypeId.values().length - 1;

    private String protegeName;
    private Class<? extends SimpleInstance> javaClass;


    private SWRLFrameTypeId(String protegeName, Class<? extends SimpleInstance> javaClass) {
    	this.protegeName = protegeName;
        this.javaClass = javaClass;
    }

    public int getFrameTypeId() {
        return SWRL_FRAME_TYPE_ID_BASE + ordinal();
    }

    public String getProtegeName() {
    	return protegeName;
    }

    public Class<? extends SimpleInstance> getJavaClass() {
        return javaClass;
    }
}