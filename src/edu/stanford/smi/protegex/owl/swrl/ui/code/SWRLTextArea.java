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

import javax.swing.UIManager;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLIncompleteRuleException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextArea;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextField;

/**
 * A SymbolTextArea with special support for editing SWRL expressions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTextArea extends SymbolTextArea 
{
  private SWRLParser parser;
  private SWRLSymbolPanel symbolPanel;

  public SWRLTextArea(OWLModel owlModel, SWRLSymbolPanel errorDisplay) 
  {
    super(owlModel, errorDisplay, new SWRLResourceNameMatcher(), new SWRLSyntaxConverter(owlModel));
    symbolPanel = errorDisplay;
    parser = new SWRLParser(owlModel);
    setFont(UIManager.getFont("TextArea.font"));
    SWRLTextField.initKeymap(this);
  }
  
  @Override
  public void setEnabled(boolean isEnabled)
  {
  	super.setEnabled(isEnabled);
  	symbolPanel.setEnabled(isEnabled);
  }
  
  protected void checkUniCodeExpression(String uniCodeText) throws Throwable 
  {
    try {
      parser.parse(uniCodeText);
    }  catch (SWRLIncompleteRuleException e) {
      // Ignore incomplete rules on input checking. (Unlike SymbolTextField, SymbolTextArea only calls checkUniCodeExpression when it
      // is checking an expression for errors, not when it is determining if an expression can be saved.
    } // try
  }

  public void reformatText() 
  {
    String text = getText();
    text = reformatText(text);
    setText(text);
  } // reformatText
  
  public static String reformatText(String text) 
  {
    // text = text.replaceAll("" + SWRLParser.IMP_CHAR, "\n" + SWRLParser.IMP_CHAR + " ");
    
    return text;
  } // reformatText
  
  protected void acceptSelectedFrame() 
  {
    String text = getText();
    int pos = getCaretPosition();
    int i = pos - 1;
    while (i >= 0 && (SymbolTextField.isIdChar(text.charAt(i)) || text.charAt(i) == '?')) {
      i--;
    }
    String prefix = text.substring(i + 1, pos);
    
    extendPartialName(prefix, ((Frame) getComboBox().getSelectedItem()).getBrowserText());
    updateErrorDisplay();
    closeComboBox();
  }
} 
