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

package edu.stanford.smi.protegex.owl.model.classparser.manchester;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ManchesterOWLClassParser implements OWLClassParser {

    public void checkClass(OWLModel owlModel,
                           String expression)
            throws OWLClassParseException {
        try {
            ManchesterOWLParser.checkClass(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    public void checkHasValueFiller(OWLModel owlModel,
                                    String expression)
            throws OWLClassParseException {
        try {
            ManchesterOWLParser.parseHasValueFiller(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    public void checkQuantifierFiller(OWLModel owlModel,
                                      String expression)
            throws OWLClassParseException {
        try {
            ManchesterOWLParser.parseQuantifierFiller(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    public RDFSClass parseClass(OWLModel owlModel,
                                String expression)
            throws OWLClassParseException {
        try {
            return ManchesterOWLParser.parseClass(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    public Object parseHasValueFiller(OWLModel owlModel,
                                      String expression)
            throws OWLClassParseException {
        try {
            return ManchesterOWLParser.parseHasValueFiller(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    public RDFResource parseQuantifierFiller(OWLModel owlModel,
                                             String expression)
            throws OWLClassParseException {
        try {
            return (RDFResource) ManchesterOWLParser.parseQuantifierFiller(owlModel, expression);
        }
        catch (ParseException e) {
            throw wrapException(e);
        }
    }


    private OWLClassParseException wrapException(ParseException ex) {
        OWLClassParseException e = new OWLClassParseException(ManchesterOWLParser.errorMessage);
        e.currentToken = ex.currentToken == null ? null : ex.currentToken.image;
        e.nextCouldBeClass = ManchesterOWLParser.nextCouldBeCls;
        e.nextCouldBeIndividual = ManchesterOWLParser.nextCouldBeInstance;
        e.nextCouldBeProperty = ManchesterOWLParser.nextCouldBeSlot;
        e.recentHasValueProperty = ManchesterOWLParser.recentHasValueProperty;
	    e.nextCouldBeDatatypeName = ManchesterOWLParser.nextCouldBeDatatypeName;
        return e;
    }
}
