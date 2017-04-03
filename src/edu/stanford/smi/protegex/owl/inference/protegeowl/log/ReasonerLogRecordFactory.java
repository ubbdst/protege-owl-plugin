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

package edu.stanford.smi.protegex.owl.inference.protegeowl.log;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 23, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerLogRecordFactory {

    private static ReasonerLogRecordFactory instance;


    protected ReasonerLogRecordFactory() {

    }


    public synchronized static ReasonerLogRecordFactory getInstance() {
        if (instance == null) {
            instance = new ReasonerLogRecordFactory();
        }

        return instance;
    }


    public ReasonerLogRecord createInformationMessageLogRecord(String message,
                                                               ReasonerLogRecord parent) {
        return new InformationMessageLogRecord(null, message, parent);
    }
 

    public ReasonerLogRecord createWarningMessageLogRecord(RDFResource cause,
                                                           String message,
                                                           ReasonerLogRecord parent) {
        return new WarningMessageLogRecord(cause, message, parent);
    }


    public ReasonerLogRecord createErrorMessageLogRecord(String message,
                                                         ReasonerLogRecord parent) {
        return new ErrorMessageLogRecord(null, message, parent);
    }


    public ReasonerLogRecord createConceptConsistencyLogRecord(RDFSClass aClass, boolean consistent, ReasonerLogRecord parent) {
        return new DefaultConceptConsistencyLogRecord(aClass, consistent, parent);
    }


    public ReasonerLogRecord createOWLInstanceLogRecord(RDFResource instance, ReasonerLogRecord parent) {
        return new OWLInstanceLogRecord(instance, parent);
    }


    public ReasonerLogRecord createOWLPropertyLogRecord(RDFProperty prop,  ReasonerLogRecord parent) {
    	return new OWLPropertyLogRecord(prop, parent);
    }

    
    
    public ReasonerLogRecord createDIGReasonerExceptionLogRecord(DIGReasonerException ex, ReasonerLogRecord parent) {
        return new DIGErrorExceptionLogRecord(ex, parent);
    }
    
    public ReasonerLogRecord createReasonerExceptionLogRecord(ProtegeReasonerException ex, ReasonerLogRecord parent) {
        return new ReasonerErrorExceptionLogRecord(ex, parent);
    }

}

