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

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultProtegeOWLParserLogger implements ProtegeOWLParserLogger {
    private static transient final Logger log = Log.getLogger(DefaultProtegeOWLParserLogger.class);
    private int count = 0;


    private final static String PREFIX = "[ProtegeOWLParser] ";


    public void logImport(String uri, String physicalURL) {
        String msg = PREFIX + "Importing " + uri;
        if (!uri.equals(physicalURL)) {
            msg += " (from " + physicalURL + ")";
        }       
        log.info(msg);
    }


    public void logTripleAdded(RDFResource subject, RDFProperty predicate, Object object) {
        if (log.isLoggable(Level.FINER)) {
            if(object instanceof Frame) {
                log.finer(" + " + subject.getName() + " " + predicate.getName() + " " + ((Frame)object).getName());
            }
            else {
                log.finer(" + " + subject.getName() + " " + predicate.getName() + " " + object);
            }
        }
        if ((++count % 10000) == 0) {
            log.info(PREFIX + "Triple " + count);
        }
    }


    public void logWarning(String message) {
        log.warning(PREFIX + "Warning: " + message);
    }
}
