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

package edu.stanford.smi.protegex.owl.ui.clsdesc;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.ui.clsdesc.manchester.ManchesterOWLTextPane;
import edu.stanford.smi.protegex.owl.ui.code.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClassDescriptionEditorComponent extends SymbolEditorComponent {

    private ManchesterOWLTextPane textPane;


    public ClassDescriptionEditorComponent(OWLModel model, SymbolErrorDisplay errorDisplay, boolean multiline) {
        super(model, errorDisplay, multiline);
        textPane = new ManchesterOWLTextPane(model, errorDisplay,
                new OWLResourceNameMatcher(),
                new OWLSyntaxConverter(model));
        setLayout(new BorderLayout());
        add(textPane);
        textPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setFocusable(true);
    }


    public JTextComponent getTextComponent() {
        return textPane;
    }


    public void setSymbolEditorHandler(SymbolEditorHandler symbolEditorHandler) {
        super.setSymbolEditorHandler(symbolEditorHandler);
        textPane.setSymbolEditorHandler(symbolEditorHandler);
    }


    protected void parseExpression() throws OWLClassParseException {
        getModel().getOWLClassDisplay().getParser().checkClass(getModel(), getTextComponent().getText());
    }
}

