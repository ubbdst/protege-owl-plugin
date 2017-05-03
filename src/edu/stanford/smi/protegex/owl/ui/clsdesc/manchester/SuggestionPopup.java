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

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SuggestionPopup extends JWindow {

    private DocumentListener documentListener;

    private MouseListener mouseListener;

    private MouseListener textComponentMouseListener;

    private ComponentListener componentListener;

    private HierarchyListener hierarchyListener;

    private Timer timer;

    private OWLModel model;

    private ImageIcon icon;

    private JPanel iconPanel;

    private Collection suggestions;

    private SuggestionList suggestionList;

    private JTextComponent textComponent;

    private KeyListener keyListener;


    public SuggestionPopup(Window owner, OWLModel model) {
        super(owner);
        setFocusableWindowState(false);
        this.model = model;
        icon = OWLIcons.getDownIcon();
        iconPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                icon.paintIcon(iconPanel, g, 0, 0);
            }
        };
        if (System.getProperty("os.name").indexOf("Mac") != -1) {
            iconPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        else {
            iconPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2))
            );
        }
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(iconPanel);
        suggestionList = new SuggestionList(this);
        suggestionList.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                reset();
            }
        });
        setSize(icon.getIconWidth(), icon.getIconHeight());
        timer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                parse();
            }
        });
        documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
                reset();
            }


            public void removeUpdate(DocumentEvent e) {
                timer.restart();
                reset();
            }


            public void changedUpdate(DocumentEvent e) {
            }
        };
        hierarchyListener = new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if (isVisible() && textComponent.isShowing() == false) {
                    reset();
                }
            }
        };
        mouseListener = new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                showOptions();
            }
        };
        textComponentMouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isVisible()) {
                    reset();
                }
            }
        };
        iconPanel.addMouseListener(mouseListener);
        componentListener = new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                reset();
            }
        };
        owner.addComponentListener(componentListener);
        keyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (isVisible() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    reset();
                }
            }
        };
    }


    public void setCurrentEditorPane(JTextComponent textComponent) {
        removeListeners();
        this.textComponent = textComponent;
        addListeners();
    }


    private void removeListeners() {
        if (textComponent != null) {
            textComponent.getDocument().removeDocumentListener(documentListener);
            textComponent.removeKeyListener(keyListener);
            textComponent.removeHierarchyListener(hierarchyListener);
            textComponent.removeMouseListener(textComponentMouseListener);
        }
    }


    private void addListeners() {
        if (textComponent != null) {
            textComponent.getDocument().addDocumentListener(documentListener);
            textComponent.addKeyListener(keyListener);
            textComponent.addHierarchyListener(hierarchyListener);
            textComponent.addMouseListener(textComponentMouseListener);
        }
    }


    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        icon.paintIcon(this, g, 0, 0);
    }


    private void offerSuggestions(OWLClassParseException e) {
        try {
            String lastToken = e.currentToken;
            if (lastToken != null &&
                    model.getRDFResource(lastToken) == null &&
                    model.isValidResourceName(lastToken, model.getOWLThingClass())) {
                suggestions = SuggestionFactory.getSuggestions(model, e);
                if (suggestions.size() > 0 && lastToken.equals("") == false) {
                    int caretPos = textComponent.getCaretPosition();
                    Rectangle rect = textComponent.modelToView(caretPos);
                    Point screenPos = rect.getLocation();
                    screenPos.x = 0 - icon.getIconWidth() - 1;
                    SwingUtilities.convertPointToScreen(screenPos, textComponent);
                    setLocation(screenPos);
                    setVisible(true);
                }
            }

        }
        catch (BadLocationException e1) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e1);
        }
    }


    public void reset() {
        setVisible(false);
        setSize(icon.getIconWidth(), icon.getIconHeight());
        getContentPane().remove(suggestionList);
        getContentPane().add(iconPanel);
    }


    private void parse() {
        if (textComponent != null) {
            String text = textComponent.getText();
            try {
                model.getOWLClassDisplay().getParser().checkClass(model, text);
                reset();
            }
            catch (OWLClassParseException e) {
                offerSuggestions(e);
            }
            catch (Error e) {
                if (ParserUtils.isLexError(e)) {
                    Log.emptyCatchBlock(e);
                }
                else {
                    throw e;
                }
            }
        }
    }


    public void updateCurrentEditorPane() {
        if (textComponent != null) {
            textComponent.setText(textComponent.getText());
        }
    }


    private void showOptions() {
        if (isVisible() && suggestions.size() > 0) {
            suggestionList.setListData(suggestions.toArray());
            getContentPane().remove(iconPanel);
            getContentPane().add(suggestionList);
            pack();
        }
    }
}
