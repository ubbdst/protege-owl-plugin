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

package edu.stanford.smi.protegex.owl.swrl.ui.code;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLIncompleteRuleException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextField;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A SymbolTextField with special support for editing SWRL expressions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTextField extends SymbolTextField {

    private final static char[][] charMap = {
            {'^', SWRLParser.AND_CHAR},
            {'.', SWRLParser.RING_CHAR},
            //{'>', SWRLParser.IMP_CHAR}
    };


    public SWRLTextField(OWLModel owlModel, SymbolErrorDisplay errorDisplay) {
        super(owlModel, errorDisplay, new SWRLResourceNameMatcher(), new SWRLSyntaxConverter(owlModel));
        initKeymap(this);
    }


    protected void checkUniCodeExpression(String uniCodeText) throws Throwable {
        SWRLParser parser = new SWRLParser(getOWLModel());

        if (isInSaveTestMode()) {
            parser.parse(uniCodeText);
        }
        else {
            try {
                parser.parse(uniCodeText);
            }
            catch (SWRLIncompleteRuleException e) {
                // Ignore incomplete rules during non save mode parsing
            } // try
        } // if
    } // checkUniCodeExpression


    public static void initKeymap(JTextComponent textComponent) {
        textComponent.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char ch = evt.getKeyChar();
                for (int i = 0; i < charMap.length; i++) {
                    char[] chars = charMap[i];
                    if (chars[0] == ch) {
                        JTextComponent c = (JTextComponent)evt.getSource();
                        try {
                        	String leftString = c.getDocument().getText(0, c.getCaretPosition()).trim();
                        	if (leftString.length() == 0 || leftString.endsWith(")"))
                        		c.getDocument().insertString(c.getCaretPosition(), "" + chars[1], null);
                        	else
                        		c.getDocument().insertString(c.getCaretPosition(), "" + ch, null);
                            evt.consume();
                            return;
                        }
                        catch (BadLocationException e) {
                        }
                    }
                }
            }
        });
    }
}
