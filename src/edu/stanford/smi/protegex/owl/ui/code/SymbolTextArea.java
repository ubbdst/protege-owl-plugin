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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.CellEditor;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceIgnoreCaseComparator;

/**
 * A remake of OWLTextField, but with JTextArea as base class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class SymbolTextArea extends JTextArea
        implements KeyListener, SymbolEditor {

    private CellEditor cellEditor;
    private JComboBox comboBox;
    private SymbolErrorDisplay errorDisplay;
    private ResourceNameMatcher resourceNameMatcher;
    private OWLModel owlModel;
    private KeyEvent previousKeyPressed;
    private SyntaxConverter syntaxConverter;

    public SymbolTextArea(OWLModel kb,
                          SymbolErrorDisplay errorDisplay,
                          ResourceNameMatcher resourceNameMatcher,
                          SyntaxConverter syntaxConverter) {
        this.owlModel = kb;
        this.resourceNameMatcher = resourceNameMatcher;
        this.errorDisplay = errorDisplay;
        this.syntaxConverter = syntaxConverter;
        setBackground(Color.white);
        removeAll();
        addKeyListener(this);
        addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                if (previousKeyPressed != null) {
                    int keyCode = previousKeyPressed.getKeyCode();
                    if (!SymbolTextField.isIdChar(previousKeyPressed.getKeyChar()) &&
                            keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_DELETE) {
                        closeComboBox();
                    }
                }
            }
        });
        setFont(getFont().deriveFont((float) 16));
    }
    
  public JComboBox getComboBox() { return comboBox; }

  protected void acceptSelectedFrame() 
  {
    String text = getText();
    int pos = getCaretPosition();
    int i = ParserUtils.findSplittingPoint(text.substring(0, pos));
    String prefix = text.substring(i, pos);
    
    extendPartialName(prefix, resourceNameMatcher.getInsertString((RDFResource) comboBox.getSelectedItem()));
    updateErrorDisplay();
    closeComboBox();
  }

    // Implements SymbolEditor
    public void assignExpression() { handleEnter(); }

  public void backspace() 
  {
    String selText = getSelectedText();
    if (selText != null && selText.length() > 0) {
      int start = getSelectionStart();
      try {
        getDocument().remove(start, getSelectionEnd() - start);
      }
      catch (BadLocationException ex) {
        Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
      }
      setCaretPosition(start);
    }
    else {
      int pos = getCaretPosition();
      if (pos > 0) {
        try {
          getDocument().remove(pos - 1, 1);
        }
        catch (BadLocationException ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        setCaretPosition(pos - 1);
      }
    }
    updateErrorDisplay();
    requestFocus();
  }

  public void cancelEditing() { handleEscape(); }


  /**
   * Used for error checking during input.
   *
   * @param text
   * @throws Throwable ParseExceptions etc.
   */
  protected abstract void checkUniCodeExpression(String text) throws Throwable;
  
  protected void closeComboBox() { removeAll(); }

  public void displayError() 
  {
    try {
      String uniCodeText = getText();
      checkUniCodeExpression(uniCodeText);
      errorDisplay.displayError((Throwable) null);
      // editedRestriction.checkFillerText(text);
        }
    catch (Throwable ex) {
      errorDisplay.displayError(ex);
    }
    requestFocus();
  }

  protected void extendPartialName(String prefix, String fullName) {
    try {
      getDocument().remove(getCaretPosition() - prefix.length(), prefix.length());
      getDocument().insertString(getCaretPosition(), fullName, null);
    }
    catch (BadLocationException ex) {
    }
  }

  private void extendPartialName(boolean autoInsert) {
    String text = getText();
    int pos = getCaretPosition();
    int i = ParserUtils.findSplittingPoint(text.substring(0, pos));

    String prefix = text.substring(i, pos);
    String leftString = text.substring(0, i);
    Set<RDFResource> resources = resourceNameMatcher.getMatchingResources(prefix, leftString, owlModel);
    if (autoInsert && resources.size() == 1) {
      RDFResource resource = resources.iterator().next();
      extendPartialName(prefix, resourceNameMatcher.getInsertString(resource));
      closeComboBox();
    } else if (resources.size() > 1) {
      showComboBox(resources, i + 1);
    }
  }
  
  public String getIndentedClsString(OWLNAryLogicalClass cls, String indentation) {
    boolean first = indentation.length() == 0;
    String str = first ? "" : "(";
    OWLClassDisplay classDisplay = owlModel.getOWLClassDisplay();    
    String operator = cls instanceof OWLUnionClass ?
                classDisplay.getOWLUnionOfSymbol() :
                classDisplay.getOWLIntersectionOfSymbol();
    for (Iterator it = cls.getOperands().iterator(); it.hasNext();) {
      RDFSClass operand = (RDFSClass) it.next();
      str += indentation;
      if (operand instanceof OWLNAryLogicalClass) {
                str += getIndentedClsString((OWLNAryLogicalClass) operand, indentation + "    ");
      }
      else {
        str += operand.getBrowserText();
      }
      if (it.hasNext()) {
        str += " " + operator + "\n";
      }
    }
        if (!first) {
          str += ")";
        }
        return str;
  }
  
  protected OWLModel getOWLModel() { return owlModel; }

  /**
   * Gets the x (pixel) position of the start of a given character in the text.
   *
   * @param pos the character position
   * @return the pixel position
   */
  private int getXOfPosition(int pos) {
    Font font = getFont();
    FontMetrics metrics = getFontMetrics(font);
    String text = getText();       
    return (text.length() > pos) ? metrics.stringWidth(text.substring(0, pos)) : metrics.stringWidth(text);    
  }
  
  
  private boolean handleDown() 
  {
    if (isComboBoxVisible()) { 
      int index = comboBox.getSelectedIndex();
      if (index < comboBox.getItemCount() - 1) {
        comboBox.setSelectedIndex(index + 1);
      }
      return true;
    } else {
      return false;
    }
  }
  
  boolean handleEnter() {
    if (isComboBoxVisible()) {
      acceptSelectedFrame();
      return true;
    }
    else {
      return false;
    }
  }

  private void handleEscape() {
    if (isComboBoxVisible()) {
      closeComboBox();
        }
    else if (cellEditor != null) {
      cellEditor.cancelCellEditing();
    }
  }

  private boolean handleUp() {
    if (isComboBoxVisible()) {
      int index = comboBox.getSelectedIndex();
      if (index > 0) {
        comboBox.setSelectedIndex(index - 1);
      }
      return true;
    }
    else {
      return false;
    }
  }

  public void insertText(String text) { insertText(text, text.length()); }

  public void insertText(String text, int caretOffset) {
    String selText = getSelectedText();
    if (selText != null && selText.length() > 0) {
      int start = getSelectionStart();
      try {
        getDocument().remove(start, getSelectionEnd() - start);
        getDocument().insertString(start, text, null);
        setCaretPosition(start + caretOffset);
      }
      catch (BadLocationException ex) {
        Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
      }
    }
    else {
      try {
        int pos = getCaretPosition();
        getDocument().insertString(pos, text, null);
        setCaretPosition(pos + caretOffset);
      }
      catch (BadLocationException ex) {
        Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
      }
    }
    updateErrorDisplay();
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          requestFocus();
        }
      });
    // requestFocus();
  }

  private boolean isComboBoxVisible() { return comboBox != null && comboBox.isVisible() && comboBox.isShowing();  }

  public void keyPressed(KeyEvent e) {
    updateErrorDisplay();
    previousKeyPressed = e;
    int code = e.getKeyCode();
    if (e.getKeyCode() == KeyEvent.VK_TAB || (e.getKeyChar() == ' ' && (e.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) != 0)) {
      extendPartialName(true);
      e.consume();
    } else {
      switch (code) {
      case KeyEvent.VK_DOWN: 
        if (handleDown()) e.consume(); break;
      case KeyEvent.VK_UP: 
        if (handleUp()) e.consume(); break;
      case KeyEvent.VK_ESCAPE: 
        handleEscape(); e.consume();
        break;
      case KeyEvent.VK_ENTER: 
        if (handleEnter()) e.consume();
        break;
      } // switch
    } // else
  } // keyPressed

  public void keyReleased(KeyEvent e) 
  {
    updateErrorDisplay();
    perhapsUpdateSyntax(e);
    int code = e.getKeyCode();
    if (code != KeyEvent.VK_DOWN && code != KeyEvent.VK_UP) {
      refreshComboBox();
    }
  }

  public void keyTyped(KeyEvent e) { perhapsUpdateSyntax(e); }

  private void perhapsUpdateSyntax(KeyEvent e) 
  {
    if (syntaxConverter != null) {
      char ch = e.getKeyChar();
      int code = e.getKeyCode();
      if (!SymbolTextField.isIdChar(ch) && code != KeyEvent.VK_BACK_SPACE && code != KeyEvent.VK_DELETE) {
        syntaxConverter.convertSyntax(this);
      }
    }
  } // perhapsUpdateSyntax

  private void refreshComboBox() 
  {
    if (isComboBoxVisible()) {
      String text = getText();
      int pos = getCaretPosition();
      int i = ParserUtils.findSplittingPoint(text.substring(0, pos));
      String prefix = text.substring(i, pos);
      String leftString = text.substring(0, i);
      Set<RDFResource> frames = resourceNameMatcher.getMatchingResources(prefix, leftString, owlModel);
      if (frames.size() == 0) {
        closeComboBox();
      } else {
        showComboBox(frames, i + 1);
      }
    }
  }
  
  void replaceText(String text) 
  {
    setText(text);
    updateErrorDisplay();
    requestFocus();
  }

  /**
   * Establishs the reference from the TextField to the CellEditor that owns it.
   * This must be called after the CellEditor has been created.
   * The reference can not be passed in the constructor of this, because the constructor
   * of DefaultCellEditor already takes a JTextField as argument, so it has to be there
   * before the DefaultCellEditor can be created.
   *
   * @param cellEditor the CellEditor
   */
  public void setCellEditor(CellEditor cellEditor) { this.cellEditor = cellEditor; }

  
  private void showComboBox(Set<RDFResource> frames, int startIndex) {
    closeComboBox();
    Frame[] fs = frames.toArray(new Frame[0]);
    Arrays.sort(fs, new ResourceIgnoreCaseComparator());
    comboBox = new JComboBox(fs);
    comboBox.setBackground(Color.white);
    comboBox.setRenderer(new FrameRenderer());
    comboBox.setSize(comboBox.getPreferredSize().width + 20, 0);
    int x = getXOfPosition(startIndex) - 16;
    String str = getText();
    int h = 1;
    int caretPosition = getCaretPosition();
    String part = "";
    for (int i = 0; i < caretPosition; i++) {
      part += str.charAt(i);
      if (str.charAt(i) == '\n') {
        h++;
        part = "";
      }
    }
    FontMetrics fm = getFontMetrics(getFont());
    x = fm.stringWidth(part);
    comboBox.setLocation(x, getRowHeight() * h);
    
    comboBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
            acceptSelectedFrame();
          }
        }
      });
    add(comboBox);
    comboBox.showPopup();
  }

  protected void updateErrorDisplay() 
  {
    String uniCodeText = getText();
    try {
      checkUniCodeExpression(uniCodeText);
      errorDisplay.displayError((Throwable) null);
      setBackground(Color.white);
    } catch (Throwable ex) {
      errorDisplay.setErrorFlag(true);
      setBackground(new Color(240, 240, 240));
    }
  } // updateErrorDisplay
    
  public SymbolErrorDisplay getErrorSymbolDisplay() { return errorDisplay; }

} // SymbolTextArea
