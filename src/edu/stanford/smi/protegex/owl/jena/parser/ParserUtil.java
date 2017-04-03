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

import java.util.UUID;

import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class ParserUtil {

    public static String PARSERS_UNIQUE_SESSION_ID = UUID.randomUUID().toString().replace("-", "_");


    public static void resetUniqueSessionId() {
        PARSERS_UNIQUE_SESSION_ID = UUID.randomUUID().toString().replace("-", "_");
    }


	public static String getResourceName(AResource resource) {
		if (resource.isAnonymous()) {
		    /*
		     * Argh...  Must ensure that this cannot conflict with owlModel.getNextAnonymousId();
		     *          See the stuff involving the creation of logical named classes in the
		     *          TripleFrameCache code.  This is *nasty* but I don't yet the better way.
		     */
			StringBuffer buffer = new StringBuffer(AbstractOWLModel.ANONYMOUS_BASE);
			buffer.append(resource.getAnonymousID());
			buffer.append("_");
			buffer.append(PARSERS_UNIQUE_SESSION_ID);
			return buffer.toString();
		} else {
			return resource.getURI();
		}
	}

	public static SimpleFrameStore getSimpleFrameStore(KnowledgeBase kb) {
	    return (SimpleFrameStore) ((DefaultKnowledgeBase) kb).getTerminalFrameStore();
	}


	public static SimpleFrameStore getSimpleFrameStore(Frame frame) {
	    return getSimpleFrameStore(frame.getKnowledgeBase());
	}

}
