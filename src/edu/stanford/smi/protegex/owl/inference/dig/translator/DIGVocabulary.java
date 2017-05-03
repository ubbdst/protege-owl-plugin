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

package edu.stanford.smi.protegex.owl.inference.dig.translator;


/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * This class contains the vocabulary used by the DIG Interface
 */
public interface DIGVocabulary {


    public static final String ASKS = "asks";

    public static final String TELLS = "tells";

    /**
     * Supported language elements
     */
    public interface Language {

        public static final String TOP = "top";

        public static final String BOTTOM = "bottom";

        public static final String CATOM = "catom";

        public static final String RATOM = "ratom";

        public static final String INT_MIN = "intmin";

        public static final String INT_MAX = "intmax";

        public static final String INT_RANGE = "intrange";

        public static final String INT_EQUALS = "intequals";

        public static final String STRING_EQUALS = "stringequals";

        public static final String DEFINED = "defined";

        public static final String AND = "and";

        public static final String OR = "or";

        public static final String NOT = "not";

        public static final String SOME = "some";

        public static final String ALL = "all";

        public static final String ATMOST = "atmost";

        public static final String ATLEAST = "atleast";

        public static final String ISET = "iset";

        public static final String CONCRETE = "concrete";

        public static final String INDIVIDUAL = "individual";

        public static final String FEATURE = "feature";

        public static final String ATTRIBUTE = "attribute";

        public static final String CHAIN = "chain";

        public static final String INVERSE = "inverse";

        public static final String SVAL = "sval";

        public static final String IVAL = "ival";
    }

    /**
     * Supported tell operations
     */
    public interface Tell {

        public static final String DEF_CONCEPT = "defconcept";

        public static final String DEF_ROLE = "defrole";

        public static final String DEF_FEATURE = "deffeature";

        public static final String DEF_ATTRIBUTE = "defattribute";

        public static final String DEF_INDIVIDUAL = "defindividual";

        public static final String IMPLIES_C = "impliesc";

        public static final String IMPLIES_R = "impliesr";

        public static final String EQUAL_C = "equalc";

        public static final String EQUAL_R = "equalr";

        public static final String DOMAIN = "domain";

        public static final String RANGE = "range";

        public static final String RANGE_INT = "rangeint";

        public static final String RANGE_STRING = "rangestring";

        public static final String TRANSITIVE = "transitive";

        public static final String FUNCTIONAL = "functional";

        public static final String DISJOINT = "disjoint";

        public static final String INSTANCE_OF = "instanceof";

        public static final String RELATED = "related";

        public static final String VALUE = "value";

        ////////////////////////////////////////////////////////////////////////
        // Non-Standard DIG elements, which are supported
        // by RACER 1.8 (might migrate into the next DIG standard)

        // A set of individuals are pairwise different
        public static final String ALL_DIFFERENT = "alldifferent";

        // Two individuals are the same as each other
        public static final String SAME_AS = "sameas";

        // Two individuals are different from each other
        public static final String DIFFERENT_FROM = "differentfrom";
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
    }

    /**
     * Supported ask operations/queries
     */
    public interface Ask {

        public static final String ALL_CONCEPT_NAMES = "allConceptNames";

        public static final String ALL_ROLE_NAMES = "allRoleNames";

        public static final String ALL_INDIVIDUALS = "allIndividuals";

        public static final String SATISFIABLE = "satisfiable";

        public static final String SUBSUMES = "subsumes";

        public static final String PARENTS = "parents";

        public static final String CHILDREN = "children";

        public static final String ANCESTORS = "ancestors";

        public static final String DESCENDANTS = "descendants";

        public static final String EQUIVALENT = "equivalents";

        public static final String R_PARENTS = "rparents";

        public static final String R_CHILDREN = "rchildren";

        public static final String R_ANCESTORS = "rancestors";

        public static final String R_DESCENDANTS = "rdescendants";

        public static final String INSTANCES = "instances";

        public static final String TYPES = "types";

        public static final String ROLE_FILLERS = "rolefillers";

        public static final String VALUES = "values";

        public static final String RELATED_INDIVIDUALS = "relatedIndividuals";

        public static final String TOLD_VALUES = "toldValues";

        public static final String INSTANCE = "instance";

        public static final String UNIQUE_NAME_ASSUMPTION = "uniqueNameAssumption";

        public static final String DISJOINT = "disjoint";
    }

    public interface Management {

        public static final String GET_IDENTIFIER = "getIdentifier";

        public static final String NEW_KNOWLEDGE_BASE = "newKB";

        public static final String CLEAR_KNOWLEDGE_BASE = "clearKB";

        public static final String RELEASE_KNOWLEDGE_BASE = "releaseKB";

    }

    /**
     * Response elements
     */
    public interface Response {

        public static final String ERROR = "error";

        public static final String TRUE = "true";

        public static final String FALSE = "false";

        public static final String CONCEPT_SET = "conceptSet";

        public static final String ROLE_SET = "roleSet";

        public static final String INDIVIDUAL_SET = "individualSet";

        public static final String INDIVIDUAL_PAIR_SET = "individualPairSet";

        public static final String INDIVIDUAL_PAIR = "individualPair";

        public static final String VALUES = "values";

        public static final String SYNONYMS = "synonyms";
    }
}
