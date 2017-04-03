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

package edu.stanford.smi.protegex.owl.model.classparser.compact;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CompactOWLClassParser implements OWLClassParser {

    public void checkClass(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            CompactParser.checkClass(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    public void checkHasValueFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            CompactParser.checkHasValueFiller(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    public void checkQuantifierFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            CompactParser.checkQuantifierFiller(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    public RDFSClass parseClass(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            return CompactParser.parseClass(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    public Object parseHasValueFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            return CompactParser.parseHasValueFiller(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    public RDFResource parseQuantifierFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        try {
            return (RDFResource) CompactParser.parseQuantifierFiller(owlModel, expression);
        }
        catch (ParseException ex) {
            throw wrapException(ex);
        }
    }


    private OWLClassParseException wrapException(ParseException ex) {
        OWLClassParseException e = new OWLClassParseException(CompactParser.errorMessage);
        e.currentToken = ex.currentToken == null ? null : ex.currentToken.image;
        e.nextCouldBeClass = CompactParser.nextCouldBeCls;
        e.nextCouldBeIndividual = CompactParser.nextCouldBeInstance;
        e.nextCouldBeProperty = CompactParser.nextCouldBeSlot;
        e.recentHasValueProperty = CompactParser.recentHasValueProperty;
	    e.nextCouldBeDatatypeName = CompactParser.nextCouldBeDatatypeName;
        return e;
    }
}
