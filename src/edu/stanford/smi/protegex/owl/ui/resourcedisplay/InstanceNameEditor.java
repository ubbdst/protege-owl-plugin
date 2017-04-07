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

package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.DocumentChangedListener;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Jan-2006
 */
public class InstanceNameEditor extends JTextField {

    private DocumentChangedListener documentListener = new DocumentChangedListener() {
        @Override
		public void insertUpdate(DocumentEvent event) {
            if (getText().indexOf(' ') > 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        replaceSpaces();
                    }
                });
            }
            else {
                onTextChange();
            }
        }

        public void stateChanged(ChangeEvent event) {
            onTextChange();
        }
    };

    private FrameListener frameListener = new FrameAdapter() {
        @Override
		public void deleted(FrameEvent event) {
        	if (event.isReplacementEvent()) { return; }
            instance = null;
            updateAll();
        }
    };

    private Instance instance;

    public InstanceNameEditor() {
        super();
        OWLUI.addCopyPastePopup(this);

        addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(FocusEvent e) {
                attemptCommit();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
			public void keyPressed(KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        attemptCommit();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        updateAll();
                        break;
                }
            }
        });

        getDocument().addDocumentListener(documentListener);
    }

    @Override
	public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + 130, size.height);
    }

    @Override
	public void addNotify() {
        super.addNotify();
        if (needsNameChange()) {
            selectAll();
        }
    }


    protected void attemptCommit() {
        if (instance != null && !instance.isBeingDeleted()) {
            String newName = getText();
            if (instance instanceof RDFResource && !newName.equals("")) {
                newName = OWLUtil.getInternalFullName((OWLModel) instance.getKnowledgeBase(), newName, true);
            }
            if (isValidName(newName)) {
                String oldName = instance.getName();
                if (!oldName.equals(newName)) {                	
                    try {
                    	instance = (Instance) instance.rename(newName);
                    } catch (Exception e) {
                        OWLUI.handleError(((OWLModel)instance.getKnowledgeBase()),e);
                    }
                }
            }
        }
    }


    public void dispose() {
        removeListener();
    }


    protected Instance getInstance() {
        return instance;
    }


    protected String getInvalidTextDescription(String text) {
        String invalidText = null;
        if (text == null || !isValidName(text)) {
            invalidText = "Invalid name";
        }
        return invalidText;
    }


    public boolean isValidName(String name) {
        edu.stanford.smi.protege.model.Frame currentFrame = getInstance();
        if (currentFrame != null) {
            edu.stanford.smi.protege.model.Frame frame = currentFrame.getKnowledgeBase().getFrame(name);
            boolean isDuplicate = frame != null && !frame.equals(currentFrame);
            boolean isValid = currentFrame.getKnowledgeBase().isValidFrameName(name, currentFrame);
            return isValid && !isDuplicate && name.length() > 0;
        }
        return true;
    }


    private boolean needsNameChange() {
        boolean needsNameChange = false;
        Instance instance = getInstance();
        if (instance != null && instance.isEditable()) {
            String name = instance.getName();
            if (name != null) {
                int index = name.lastIndexOf('_');
                String possibleIntegerString = name.substring(index + 1);
                //noinspection EmptyCatchBlock
                try {
                    Integer.parseInt(possibleIntegerString);
                    needsNameChange = true;
                }
                catch (Exception e) {
                }
            }
        }
        return needsNameChange;
    }


    private void onTextChange() {
        validateText(getText());
    }


    private void removeListener() {
        if (instance != null) {
            instance.removeFrameListener(frameListener);
        }
    }


    private void replaceSpaces() {
        String str = getText();
        str = str.replace(' ', '_');
        int pos = getCaretPosition();
        setText(str);
        setCaretPosition(pos);
        onTextChange();
    }


    @Override
	public void selectAll() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InstanceNameEditor.super.selectAll();
                requestFocus();
            }
        });
    }

    public void selectLocalName() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	if (instance != null) {
            		String name = instance.getName();
            		String namespace = NamespaceUtil.getNameSpace(name);
            		InstanceNameEditor.super.select(namespace.length(), name.length());
            		requestFocus();
            	}
            }
        });
    }

    public void setInstance(Instance instance) {
        attemptCommit(); // first commit changes to the previous instance
        removeListener();
        this.instance = instance;
        if (instance != null) {
            instance.addFrameListener(frameListener);
        }
        updateAll();
    }


    @Override
	public void setText(String text) {
        documentListener.disable();
        super.setText(text == null ? "" : text);
        documentListener.enable();
        validateText(text);
    }


    private void updateAll() {
        if (instance != null) {
            String name = instance.getName();
            setText(name);
            setEditable(instance.isEditable());
            onTextChange();
            if (needsNameChange()) {
                selectLocalName();
            }
        }
        else {
            setText("");
            setEditable(false);
        }
    }


    protected boolean validateText(String text) {
        String errorDescription = text == null ? null : getInvalidTextDescription(text);
        if (errorDescription == null) {
            setForeground(Color.black);
            setToolTipText(null);
        }
        else {
            setForeground(Color.red);
            setToolTipText(errorDescription);
        }
        return errorDescription == null;
    }
}