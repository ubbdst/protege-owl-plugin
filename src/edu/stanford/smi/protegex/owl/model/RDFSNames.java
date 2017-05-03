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
 * Defines the names of the RDFS related parts of the OWL system ontology.
 * This corresponds to the Model interface in general Protege.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFSNames {
	
	//added by TT for use of the fully qualified name for the owl metamodel
	
	static String RDFS_NAMESPACE= "http://www.w3.org/2000/01/rdf-schema#";

    public static interface Cls {

        final static String CONTAINER = RDFS_NAMESPACE + "Container";

        final static String DATATYPE = RDFS_NAMESPACE + "Datatype";
        
        /**
         * @deprecated This is not a class.  Use RDFSNames.LITERAL instead.
         */
        final static String LITERAL = RDFSNames.LITERAL;

        final static String NAMED_CLASS = RDFS_NAMESPACE + "Class";
    }


    public interface ClsID {

        FrameID NAMED_CLASS = new FrameID(Cls.NAMED_CLASS);
    }

    public static interface Slot {

        final static String COMMENT = RDFS_NAMESPACE + "comment";

        final static String DOMAIN = RDFS_NAMESPACE + "domain";

        final static String IS_DEFINED_BY = RDFS_NAMESPACE + "isDefinedBy";

        final static String LABEL = RDFS_NAMESPACE + "label";

        /**
         * @deprecated use LABEL
         */
        final static String LABELS = RDFS_NAMESPACE + "label";

        final static String MEMBER = RDFS_NAMESPACE + "member";

        final static String RANGE = RDFS_NAMESPACE + "range";

        final static String SEE_ALSO = RDFS_NAMESPACE + "seeAlso";

        final static String SUB_CLASS_OF = RDFS_NAMESPACE + "subClassOf";

        final static String SUB_PROPERTY_OF = RDFS_NAMESPACE + "subPropertyOf";
    }
    
    final static String LITERAL = RDFS_NAMESPACE + "Literal";

    final static String RDFS_PREFIX = "rdfs";
}
