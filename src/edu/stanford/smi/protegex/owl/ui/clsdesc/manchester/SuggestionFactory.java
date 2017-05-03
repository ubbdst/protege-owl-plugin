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

package edu.stanford.smi.protegex.owl.ui.clsdesc.manchester;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SuggestionFactory {

    public static Collection getSuggestions(OWLModel model, OWLClassParseException e) {
        ArrayList suggestions = new ArrayList();
        if (e.nextCouldBeClass) {
            suggestions.add(new CreateClassSuggestion(model, getLastToken(e)));
            suggestions.add(new CreateSubClassSuggestion(model, getLastToken(e)));
        }
        if (e.nextCouldBeProperty) {
            suggestions.add(new CreateObjectPropertySuggestion(model, getLastToken(e)));
        }
        if (e.nextCouldBeIndividual) {
            suggestions.add(new CreateIndividualSuggestion(model, getLastToken(e)));
        }

        return suggestions;
    }


    private static String getLastToken(OWLClassParseException e) {
        return e.currentToken;
    }
}

