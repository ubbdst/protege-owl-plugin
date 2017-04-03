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
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceIgnoreCaseComparator;

/**
 * A JTextField with special support for editing expressions in languages like
 * the OWL compact syntax or SWRL.
 * This is prepared to be used as a CellEditor for tables (e.g., the RestrictionsTable).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class SymbolTextField extends JTextField
        implements KeyListener, SymbolEditor {

    private SymbolEditorHandler editorHandler;

    private JComboBox comboBox;

    private SymbolErrorDisplay errorDisplay;

    private ResourceNameMatcher resourceNameMatcher;

    private OWLModel owlModel;

    private KeyEvent previousKeyPressed;

    public final static int SCALABLE_FRAME_COUNT = 1000;

    private SyntaxConverter syntaxConverter;

    // Flag that can (optionally) be used by checkUniCodeExpression
    // methods in subclasses to determine if they should modify their
    // expression checking behaviour. Some editors may chose not to
    // signal an error for correct but incomplete rules while they are
    // being entered but will signal an error if an attempt is made to
    // save such a rule. inSaveTestMode is set to true to indicate to
    // checkUniCodeExpression that it being used to determine if an
    // expression is saveable; when it is false, it signals that the
    // method is used to perform normal interactive input checking.
    //
    // Subclasses that do not distinguish between edit and save mode
    // testing can ignore this flag.

    private boolean inSaveTestMode = false;


    public SymbolTextField(OWLModel kb, SymbolErrorDisplay errorDisplay,
                           ResourceNameMatcher resourceNameMatcher, SyntaxConverter syntaxConverter) {
        this.resourceNameMatcher = resourceNameMatcher;
        this.owlModel = kb;
        this.syntaxConverter = syntaxConverter;
        this.errorDisplay = errorDisplay;
        setBackground(Color.white);
        removeAll();
        addKeyListener(this);
        addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                if (previousKeyPressed != null) {
                    int keyCode = previousKeyPressed.getKeyCode();
                    if (!isIdChar(previousKeyPressed.getKeyChar()) &&
                            keyCode != KeyEvent.VK_BACK_SPACE &&
                            keyCode != KeyEvent.VK_DELETE) {
                        closeComboBox();
                    }
                }
            }
        });
    }


    private void acceptSelectedResource() {
        String text = getText();
        int pos = getCaretPosition();
        int i = pos - 1;
        while (i >= 0 && (isIdChar(text.charAt(i)) && text.charAt(i) == '?')) i--;

        String prefix = text.substring(i + 1, pos);
        RDFResource resource = ((RDFResource) comboBox.getSelectedItem());
        extendPartialName(prefix, resourceNameMatcher.getInsertString(resource));
        updateErrorDisplay();
        closeComboBox();
    }


    // Implements SymbolEditor
    public void assignExpression() {
        handleEnter();
    }


    public void backspace() {
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


    public void cancelEditing() {
        handleEscape();
    }


    /**
     * Used for error checking during input.
     *
     * @param text
     * @throws Throwable ParseExceptions etc.
     */
    protected abstract void checkUniCodeExpression(String text) throws Throwable;

    // See inSaveTestMode comment above.


    protected boolean isInSaveTestMode() {
        return inSaveTestMode;
    }


    private void closeComboBox() {
        removeAll();
    }


    public void displayError() {
        try {
            String uniCodeText = getText();
            checkUniCodeExpression(uniCodeText);
            // editedRestriction.checkFillerText(text);
            errorDisplay.displayError((Throwable) null);
        }
        catch (Throwable ex) {
            errorDisplay.displayError(ex);
        }
        requestFocus();
    }


    private void extendPartialName(String prefix, String fullName) {
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
        }
        else if (resources.size() > 1) {
            showComboBox(resources, i + 1);
        }
    }


    protected OWLModel getOWLModel() {
        return owlModel;
    }


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


    private void handleDown() {
        if (comboBox != null && comboBox.isVisible()) {
            int index = comboBox.getSelectedIndex();
            if (index < comboBox.getItemCount() - 1) {
                comboBox.setSelectedIndex(index + 1);
            }
        }
    }


    protected void handleEnter() {

        if (isComboBoxVisible()) {
            acceptSelectedResource();
        }
        else {
            inSaveTestMode = true;
            try {
                String uniCodeText = getText();

                checkUniCodeExpression(uniCodeText);
                stopEditing();
            }
            catch (Throwable ex) {
                errorDisplay.displayError(ex);
                requestFocus();
            }
            inSaveTestMode = false;
        }
    }


    private void handleEscape() {
        if (isComboBoxVisible()) {
            closeComboBox();
        }
        else {
            editorHandler.cancelEditing();
        }
    }


    private void handleUp() {
        if (isComboBoxVisible()) {
            int index = comboBox.getSelectedIndex();
            if (index > 0) {
                comboBox.setSelectedIndex(index - 1);
            }
        }
    }


    public void insertText(String text) {
        insertText(text, text.length());
    }


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
        requestFocus();
    }


    private boolean isComboBoxVisible() {
        return comboBox != null && comboBox.isVisible() && comboBox.isShowing();
    }


    public static boolean isIdChar(char ch) {
        return Character.isJavaIdentifierPart(ch) || ch == ':' || ch == '-';
    }


    public void keyPressed(KeyEvent e) {
        updateErrorDisplay();
        previousKeyPressed = e;
        int code = e.getKeyCode();
        if (e.getKeyCode() == KeyEvent.VK_TAB || (e.getKeyChar() == ' ' &&
                (e.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) != 0)) {
            extendPartialName(true);
            e.consume();
        }
        else {
            switch (code) {
                case KeyEvent.VK_DOWN: {
                    handleDown();
                    e.consume();
                    break;
                }
                case KeyEvent.VK_UP: {
                    handleUp();
                    e.consume();
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    handleEscape();
                    e.consume();
                    break;
                }
                case KeyEvent.VK_ENTER: {
                    handleEnter();
                    e.consume();
                    break;
                }
            }
        }
    }


    public void keyReleased(KeyEvent e) {
        perhapsConvertSyntax(e);
        updateErrorDisplay();
        int code = e.getKeyCode();
        if (code != KeyEvent.VK_DOWN && code != KeyEvent.VK_UP) {
            refreshComboBox();
        }
    }


    public void keyTyped(KeyEvent e) {
        perhapsConvertSyntax(e);
    }


    private void perhapsConvertSyntax(KeyEvent e) {
        if (syntaxConverter != null) {
            char ch = e.getKeyChar();
            int code = e.getKeyCode();
            if (!isIdChar(ch) &&
                    code != KeyEvent.VK_BACK_SPACE &&
                    code != KeyEvent.VK_DELETE) {
                syntaxConverter.convertSyntax(this);
            }
        }
    }


    private void refreshComboBox() {
        if (isComboBoxVisible()) {
            String text = getText();
            int pos = getCaretPosition();
            int i = ParserUtils.findSplittingPoint(text.substring(0, pos));
            String prefix = text.substring(i, pos);
            String leftString = text.substring(0, i);
            Set<RDFResource> frames = resourceNameMatcher.getMatchingResources(prefix, leftString, owlModel);
            if (frames.size() == 0) {
                closeComboBox();
            }
            else {
                showComboBox(frames, i + 1);
            }
        }
    }


    void replaceText(String text) {
        setText(text);
        updateErrorDisplay();
        requestFocus();
    }


    public void setSymbolEditorHandler(SymbolEditorHandler editorHandler) {
        this.editorHandler = editorHandler;
    }


    private void showComboBox(Set<RDFResource> frames, int startIndex) {
        closeComboBox();
        Frame[] fs = frames.toArray(new Frame[0]);
        Arrays.sort(fs, new ResourceIgnoreCaseComparator());
        comboBox = new JComboBox(fs);
        comboBox.setBackground(Color.white);
        comboBox.setRenderer(new FrameRenderer());
        comboBox.setSize(comboBox.getPreferredSize().width + 20, 0);
        int x = getXOfPosition(startIndex) - 16;
        comboBox.setLocation(x, getHeight());

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
                    acceptSelectedResource();
                }
            }
        });
        add(comboBox);
        comboBox.showPopup();
    }


    protected void stopEditing() {
        if (editorHandler != null) {
            editorHandler.stopEditing();
        }
    }


    private void updateErrorDisplay() {
        String uniCodeText = getText();
        try {
            //String text = OWLTextFormatter.getParseableString(uniCodeText);
            checkUniCodeExpression(uniCodeText);
            errorDisplay.displayError((Throwable) null);
            setBackground(Color.white);
        }
        catch (Throwable ex) {
            errorDisplay.setErrorFlag(true);
            setBackground(new Color(240, 240, 240));
        }
    }


    protected void updateSyntax() {
        OWLTextFormatter.updateSyntax(this, owlModel);
    }
}
