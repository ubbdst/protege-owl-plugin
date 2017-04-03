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

package edu.stanford.smi.protegex.owl.model;

import edu.stanford.smi.protege.model.FrameID;


/**
 * Defines the names of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLNames {
	
	//added by TT for use of the fully qualified name for the owl metamodel
	// !check the namespace of constants again! not all of them should have the owl namespace
	
	static String OWL_NAMESPACE= "http://www.w3.org/2002/07/owl#";

    public static interface Cls {

        public final static String ALL_DIFFERENT = (OWL_NAMESPACE + "AllDifferent").intern();

        public final static String ALL_VALUES_FROM_RESTRICTION = (OWL_NAMESPACE + "AllValuesFromRestriction").intern();

        public final static String ANNOTATION_PROPERTY = (OWL_NAMESPACE + "AnnotationProperty").intern();

        public final static String ANONYMOUS_CLASS = (OWL_NAMESPACE + "AnonymousClass").intern();

        public final static String ANONYMOUS_ROOT = ":OWL-ANONYMOUS-ROOT".intern();

        public final static String CARDINALITY_RESTRICTION = (OWL_NAMESPACE + "CardinalityRestriction").intern();

        public final static String COMPLEMENT_CLASS = (OWL_NAMESPACE + "ComplementClass").intern();

        public final static String DATATYPE_PROPERTY = (OWL_NAMESPACE + "DatatypeProperty").intern();

        public final static String DATA_RANGE = (OWL_NAMESPACE + "DataRange").intern();

        public final static String DEPRECATED_CLASS = (OWL_NAMESPACE + "DeprecatedClass").intern();

        public final static String DEPRECATED_PROPERTY = (OWL_NAMESPACE + "DeprecatedProperty").intern();

        public final static String ENUMERATED_CLASS = (OWL_NAMESPACE + "EnumeratedClass").intern();

        public final static String FUNCTIONAL_PROPERTY = (OWL_NAMESPACE + "FunctionalProperty").intern();

        public final static String HAS_VALUE_RESTRICTION = (OWL_NAMESPACE + "HasValueRestriction").intern();

        public final static String INTERSECTION_CLASS = (OWL_NAMESPACE + "IntersectionClass").intern();

        public final static String INVERSE_FUNCTIONAL_PROPERTY = (OWL_NAMESPACE + "InverseFunctionalProperty").intern();

        public final static String LOGICAL_CLASS = (OWL_NAMESPACE + "LogicalClass").intern();

        public final static String MAX_CARDINALITY_RESTRICTION = (OWL_NAMESPACE + "MaxCardinalityRestriction").intern();

        public final static String MIN_CARDINALITY_RESTRICTION = (OWL_NAMESPACE + "MinCardinalityRestriction").intern();

        public final static String NAMED_CLASS = (OWL_NAMESPACE + "Class").intern();

        public final static String NOTHING = (OWL_NAMESPACE + "Nothing").intern();

        public final static String ONTOLOGY = (OWL_NAMESPACE + "Ontology").intern();

        public final static String OBJECT_PROPERTY = (OWL_NAMESPACE + "ObjectProperty").intern();

        public final static String OWL_CLASS = ":OWL-CLASS".intern();

        public final static String RESTRICTION = (OWL_NAMESPACE + "Restriction").intern();

        public final static String SOME_VALUES_FROM_RESTRICTION = (OWL_NAMESPACE + "SomeValuesFromRestriction").intern();

        public final static String SYMMETRIC_PROPERTY = (OWL_NAMESPACE + "SymmetricProperty").intern();

        public final static String THING = (OWL_NAMESPACE + "Thing").intern();

        public final static String TRANSITIVE_PROPERTY = (OWL_NAMESPACE + "TransitiveProperty").intern();

        public final static String UNION_CLASS = (OWL_NAMESPACE + "UnionClass").intern();
        
        public final static String OWL_ONTOLOGY_POINTER_CLASS = ":OWL-ONTOLOGY-POINTER-CLASS".intern();
    }

    public static interface Slot {

        public final static String ALL_VALUES_FROM = (OWL_NAMESPACE + "allValuesFrom").intern();

        public final static String BACKWARD_COMPATIBLE_WITH = (OWL_NAMESPACE + "backwardCompatibleWith").intern();

        public final static String CARDINALITY = (OWL_NAMESPACE + "cardinality").intern();

        public final static String COMPLEMENT_OF = (OWL_NAMESPACE + "complementOf").intern();

        public final static String DIFFERENT_FROM = (OWL_NAMESPACE + "differentFrom").intern();

        public final static String DISJOINT_WITH = (OWL_NAMESPACE + "disjointWith").intern();

        public final static String DISTINCT_MEMBERS = (OWL_NAMESPACE + "distinctMembers").intern();

        public final static String EQUIVALENT_CLASS = (OWL_NAMESPACE + "equivalentClass").intern();

        public final static String EQUIVALENT_PROPERTY = (OWL_NAMESPACE + "equivalentProperty").intern();

        /**
         * @deprecated use EQUIVALENT_PROPERTY instead
         */
        public final static String EQUIVALENT_PROPERTIES = (OWL_NAMESPACE + "equivalentProperty").intern();

        public final static String HAS_VALUE = (OWL_NAMESPACE + "hasValue").intern();

        public final static String INCOMPATIBLE_WITH = (OWL_NAMESPACE + "incompatibleWith").intern();

        public final static String INTERSECTION_OF = (OWL_NAMESPACE + "intersectionOf").intern();

        public final static String INVERSE_OF = (OWL_NAMESPACE + "inverseOf").intern();

        public final static String MAX_CARDINALITY = (OWL_NAMESPACE + "maxCardinality").intern();

        public final static String MIN_CARDINALITY = (OWL_NAMESPACE + "minCardinality").intern();

        public final static String ON_PROPERTY = (OWL_NAMESPACE + "onProperty").intern();

        public final static String IMPORTS = (OWL_NAMESPACE + "imports").intern();

        public final static String ONE_OF = (OWL_NAMESPACE + "oneOf").intern();

        public final static String PRIOR_VERSION = (OWL_NAMESPACE + "priorVersion").intern();
        
        public final static String SAME_AS = (OWL_NAMESPACE + "sameAs").intern();

        public final static String SOME_VALUES_FROM = (OWL_NAMESPACE + "someValuesFrom").intern();

        public final static String SUBCLASSES_DISJOINT = (ProtegeNames.PREFIX + "subclassesDisjoint").intern();

        public final static String VALUES_FROM = (OWL_NAMESPACE + "valuesFrom").intern();

        public final static String VERSION_INFO = (OWL_NAMESPACE + "versionInfo").intern();

        public final static String UNION_OF = (OWL_NAMESPACE + "unionOf").intern();
        
        //Non-OWL slots that are used internally by Protege
        
        public final static String ONTOLOGY_PREFIXES = (ProtegeNames.PROTEGE_OWL_NAMESPACE + "OWL-ONTOLOGY-PREFIXES").intern();
                      
        public final static String RESOURCE_URI = (ProtegeNames.PROTEGE_OWL_NAMESPACE + "OWL-RESOURCE-URI").intern();
        
        public final static String OWL_ONTOLOGY_POINTER_PROPERTY = (ProtegeNames.PROTEGE_OWL_NAMESPACE + "OWL-ONTOLOGY-POINTER-PROPERTY").intern();
        
    }


    final static int CLASSIFICATION_STATUS_UNDEFINED = 0;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED = 1;

    final static int CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED = 3;

    /**
     * @deprecated Please use the <code>isConsistent</code> method
     *             on <code>OWLNamedClass</code>.
     */
    final static int CLASSIFICATION_STATUS_INCONSISTENT = 2;

    final static String OWL_PREFIX = "owl";


    public interface ClsID {

        FrameID NAMED_CLASS = new FrameID(Cls.NAMED_CLASS);

        FrameID THING = new FrameID(Cls.THING);
    }
}
