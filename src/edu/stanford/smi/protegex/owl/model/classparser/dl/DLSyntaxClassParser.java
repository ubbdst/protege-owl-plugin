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

package edu.stanford.smi.protegex.owl.model.classparser.dl;

import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.OWLClass;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 25, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLSyntaxClassParser implements OWLClassParser {

    public void checkClass(OWLModel owlModel, String expression) throws OWLClassParseException {
        parse(owlModel, expression, false);
    }

    public void checkHasValueFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        parse(owlModel, expression, false);
    }

    public void checkQuantifierFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        parse(owlModel, expression, false);
    }

    public RDFSClass parseClass(OWLModel owlModel, String expression) throws OWLClassParseException {
        return parse(owlModel, expression, true);
    }

    public Object parseHasValueFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        return parse(owlModel, expression, true);
    }

    public RDFResource parseQuantifierFiller(OWLModel owlModel, String expression) throws OWLClassParseException {
        return parse(owlModel, expression, true);
    }

    private OWLClass parse(OWLModel owlModel, String expression, boolean create) throws OWLClassParseException {
        try {
            String parsableExpr = DLSyntaxParserUtil.getParseableString(expression);
            OWLClass cls = DLSyntaxParser.parseExpression(owlModel, parsableExpr, create);
            return cls;
        } catch (ParseException e) {
            throw wrapAndThrowException(e);
        }
    }

    private OWLClassParseException wrapAndThrowException(ParseException e) {
        OWLClassParseException parseException = new OWLClassParseException(e.getMessage());
        parseException.nextCouldBeClass = contains(e.expectedTokenSequences, DLSyntaxParserConstants.CLASS_ID);
        parseException.nextCouldBeProperty = contains(e.expectedTokenSequences, DLSyntaxParserConstants.DATATYPE_PROPERTY_ID) |
                contains(e.expectedTokenSequences, DLSyntaxParserConstants.OBJECT_PROPERTY_ID);
        parseException.nextCouldBeIndividual = contains(e.expectedTokenSequences, DLSyntaxParserConstants.INDIVIDUAL_ID);
        parseException.nextCouldBeDatatypeName = contains(e.expectedTokenSequences, DLSyntaxParserConstants.DATATYPE_ID);
        return parseException;
    }


    private boolean contains(int tokenSequences [] [], int val) {
        for(int i = 0; i < tokenSequences.length; i++) {
            int [] seq = tokenSequences[i];
            for(int j = 0; j < seq.length; j++) {
                if(seq[j] == val) {
                    return true;
                }
            }
        }
        return false;
    }

}
