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

package edu.stanford.smi.protegex.owl.model.factory;

import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLComplementClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDataRange;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFUntypedResource;

// ToDo - move all the java types and the frame type id 2 owl java class code here?

public enum OWLFactoryClassType {

    OWL_NAMED_CLASS(OWLNames.Cls.NAMED_CLASS, DefaultOWLNamedClass.class, false),

    PROPERTY(RDFNames.Cls.PROPERTY, DefaultRDFProperty.class, false),

    RDFS_NAMED_CLASS(RDFSNames.Cls.NAMED_CLASS, DefaultRDFSNamedClass.class, false),

    ENUMERATED_CLASS(OWLNames.Cls.ENUMERATED_CLASS, DefaultOWLEnumeratedClass.class, false),

    ALL_VALUES_FROM_RESTRICTION(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION, DefaultOWLAllValuesFrom.class, true),
    SOME_VALUES_FROM_RESTRICTION(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION, DefaultOWLSomeValuesFrom.class, true),
    HAS_VALUE_RESTRICTION(OWLNames.Cls.HAS_VALUE_RESTRICTION, DefaultOWLHasValue.class, true),
    MAX_CARDINALITY_RESTRICTION(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION, DefaultOWLMaxCardinality.class, true),
    MIN_CARDINALITY_RESTRICTION(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION, DefaultOWLMinCardinality.class, true),
    CARDINALITY_RESTRICTION(OWLNames.Cls.CARDINALITY_RESTRICTION, DefaultOWLCardinality.class, true),

    COMPLEMENT_CLASS(OWLNames.Cls.COMPLEMENT_CLASS, DefaultOWLComplementClass.class, false),
    INTERSECTION_CLASS(OWLNames.Cls.INTERSECTION_CLASS, DefaultOWLIntersectionClass.class, false),
    UNION_CLASS(OWLNames.Cls.UNION_CLASS, DefaultOWLUnionClass.class, false),

    ALL_DIFFERENT(OWLNames.Cls.ALL_DIFFERENT, DefaultOWLAllDifferent.class, false),
    EXTERNAL_RESOURCE(RDFNames.Cls.EXTERNAL_RESOURCE, DefaultRDFUntypedResource.class, false),
    DATATYPE(RDFSNames.Cls.DATATYPE, DefaultRDFSDatatype.class, false),
    DATA_RANGE(OWLNames.Cls.DATA_RANGE, DefaultOWLDataRange.class, false),
    RDF_LIST(RDFNames.Cls.LIST, DefaultRDFList.class, false),
    ONTOLOGY(OWLNames.Cls.ONTOLOGY, DefaultOWLOntology.class, false),

    DATATYPE_PROPERTY(OWLNames.Cls.DATATYPE_PROPERTY, DefaultOWLDatatypeProperty.class, false),
    OBJECT_PROPERTY(OWLNames.Cls.OBJECT_PROPERTY, DefaultOWLObjectProperty.class, false);

    private String typeName;
    private Class<? extends RDFResource> implementingClass;
    private boolean fakeProtege3Type;
    
    private OWLFactoryClassType(String typeName, Class<? extends RDFResource> implementingClass,  boolean fake) {
        this.typeName = typeName;
        this.implementingClass = implementingClass;
        this.fakeProtege3Type = fake;
    }

    public String getTypeName() {
        return typeName;
    }

    public Class<? extends RDFResource> getImplementingClass() {
        return implementingClass;
    }

    public boolean isFakeProtege3Type() {
        return fakeProtege3Type;
    }
    
    

}
