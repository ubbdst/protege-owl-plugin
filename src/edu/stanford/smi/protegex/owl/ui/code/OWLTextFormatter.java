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

package edu.stanford.smi.protegex.owl.ui.code;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplayFactory;
import edu.stanford.smi.protegex.owl.model.classparser.compact.CompactParserUtil;

/**
 * A static utility class for JTextComponents with specific features to edit OWL
 * expressions in the compact syntax.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTextFormatter {

    private static OWLClassDisplay display = OWLClassDisplayFactory.getDefaultDisplay();


    private static String[][] charMap;

    private static Hashtable<String, String> symbolMap = new Hashtable<String, String>();


    // Fill the default abbreviations
    static {
        updateDisplay(OWLClassDisplayFactory.getDefaultDisplay());
    }

    public static void updateDisplay(OWLClassDisplay display) {
        String [][] newCharMap = {
                {"<", display.getOWLMaxCardinalitySymbol()},
                {"=", display.getOWLCardinalitySymbol()},
                {">", display.getOWLMinCardinalitySymbol()},
                {"*", display.getOWLAllValuesFromSymbol()},
                {"?", display.getOWLSomeValuesFromSymbol()},
                {"$", display.getOWLHasValueSymbol()},
                {"!", display.getOWLComplementOfSymbol()},
                {"&", display.getOWLIntersectionOfSymbol()},
                {"|", display.getOWLUnionOfSymbol()}
        };
        charMap = newCharMap;
        
        symbolMap.clear();
        symbolMap.put("all", "" + display.getOWLAllValuesFromSymbol());
        symbolMap.put("allValuesFrom", "" + display.getOWLAllValuesFromSymbol());
        symbolMap.put("forall", "" + display.getOWLAllValuesFromSymbol());
        symbolMap.put("only", "" + display.getOWLAllValuesFromSymbol());
        symbolMap.put("some", "" + display.getOWLSomeValuesFromSymbol());
        symbolMap.put("someValuesFrom", "" + display.getOWLSomeValuesFromSymbol());
        symbolMap.put("exists", "" + display.getOWLSomeValuesFromSymbol());
        symbolMap.put("has", "" + display.getOWLHasValueSymbol());
        symbolMap.put("hasValue", "" + display.getOWLHasValueSymbol());
        symbolMap.put("value", "" + display.getOWLHasValueSymbol());
        symbolMap.put("and", "" + display.getOWLIntersectionOfSymbol());
        symbolMap.put("or", "" + display.getOWLUnionOfSymbol());
        symbolMap.put("not", "" + display.getOWLComplementOfSymbol());
    }

    public static String getDisplayString(String str) {
        int originalLength = str.length();
        String n = "";
        StringTokenizer tokenizer = new StringTokenizer(str, " \t\n,(){}[]", true);
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() == 1) {
                char c = token.charAt(0);
                for (int i = 0; i < charMap.length; i++) {
                    String[] chars = charMap[i];
                    if (chars[0].charAt(0) == c) {
                        token = "" + chars[1];
                        break;
                    }
                }
            }
            index += token.length();
            String symbol = (String) symbolMap.get(token);
            if (symbol != null && index < originalLength) {
                n += symbol;
            }
            else {
                n += token;
            }
        }
        return n;
    }


    /**
     * @deprecated Moved to CompactParserUtil - this method is no longer needed as it will be called by
     *             the parser on the fly
     */
    public static String getParseableString(String str) {
        return CompactParserUtil.getParseableString(str);
    }


    public static void initKeymap(JTextComponent textComponent) {
        textComponent.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char ch = evt.getKeyChar();
                for (int i = 0; i < charMap.length; i++) {
                    String[] chars = charMap[i];
                    if (chars[0].charAt(0) == ch) {
                        JTextComponent c = (JTextComponent) evt.getSource();
                        try {
                            c.getDocument().insertString(c.getCaretPosition(), "" + chars[1], null);
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

    /*
    private static void map(JTextComponent textComponent, final char key, final char newChar) {
        Keymap keymap = textComponent.getKeymap();
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        keymap.addActionForKeyStroke(keyStroke, new InsertAction(textComponent, c));
    }*/


    /**
     * Replaces all occurences of special symbols with the corresponding unicode symbols.
     */
    public static void updateSyntax(JTextComponent textComponent) {
        updateSyntax(textComponent, null);
    }


    /**
     * Replaces all occurences of special symbols with the corresponding unicode symbols.
     */
    public static void updateSyntax(JTextComponent textComponent, OWLModel owlModel) {
        updateSyntax(textComponent, owlModel, symbolMap);
    }


    public static void updateSyntax(JTextComponent textComponent, OWLModel owlModel, Map smap) {
        String str = textComponent.getText();
        int pos = textComponent.getCaretPosition();
        int originalLength = str.length();
        String n = "";
        StringTokenizer tokenizer = new StringTokenizer(str, " \t\n,(){}[]", true);
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            index += token.length();
            String symbol = (String) smap.get(token);
            if (symbol != null && index < originalLength &&
                    (owlModel == null || owlModel.getRDFResource(token) == null)) {
                n += symbol;
                if (n.length() < pos) {
                    pos = pos - token.length() + symbol.length();
                }
            }
            else {
                n += token;
            }
        }
        if (!str.equals(n)) {
            textComponent.setText(n);
            textComponent.setCaretPosition(pos);
        }
    }


    private static class InsertAction extends AbstractAction {

        private char c;

        private JTextComponent textComponent;


        InsertAction(JTextComponent textComponent, char c) {
            this.c = c;
            this.textComponent = textComponent;
        }


        public void actionPerformed(ActionEvent e) {
            int offset = textComponent.getCaretPosition();
            try {
                textComponent.getDocument().insertString(offset, "" + c, null);
            }
            catch (BadLocationException ex) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
    }

}
