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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A JPanel hosting buttons that accelerate editing of OWL/SWRL expressions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class SymbolPanel extends JPanel implements SymbolErrorDisplay {

    private Action backspaceAction;

    protected ResourceSelectionAction classAction;

    /**
     * The JLabel to display error messages
     */
    private JLabel errorLabel;

    protected ResourceSelectionAction individiualAction;

    /**
     * A JButton placed beside the errorLabel to display whether there is an error or not
     */
    private JButton nerdButton;

    private OWLModel owlModel;

    protected ResourceSelectionAction propertyAction;

    /**
     * The object that handles various button commands (e.g. the FillerTextField)
     */
    private SymbolEditor symbolEditor;


    public SymbolPanel(OWLModel owlModel, final boolean closable) {
        this(owlModel, closable, false);
    }


    public SymbolPanel(OWLModel owlModel, final boolean closable, boolean draggable) {
        this(owlModel, closable, draggable, true);
    }


    public SymbolPanel(OWLModel owlModel, final boolean closable, boolean draggable, boolean withMiddleBar) {

        this.owlModel = owlModel;
        setBackground(new Color(230, 230, 230));

        classAction = new InsertClassAction();
        propertyAction = new InsertPropertyAction();
        individiualAction = new InsertIndividualAction();

        errorLabel = new JLabel(" ");
        errorLabel.setFont(getFont().deriveFont(Font.PLAIN));
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(BorderLayout.CENTER, errorLabel);

        JToolBar topBar = ComponentFactory.createToolBar();

        JToolBar middleBar = withMiddleBar ? ComponentFactory.createToolBar() : null;
        JToolBar buttonBar = withMiddleBar ? middleBar : topBar;

        initTopBar(topBar);

        if (withMiddleBar) {
            initMiddleBar(middleBar);
        }
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(topBar);
        add(Box.createVerticalStrut(4));
        if (withMiddleBar) {
            add(middleBar);
            add(Box.createVerticalStrut(4));
        }
        add(southPanel);

        backspaceAction = new AbstractAction("Backspace", OWLIcons.getBackspaceIcon()) {
            public void actionPerformed(ActionEvent e) {
                symbolEditor.backspace();
            }
        };
        addButton(buttonBar, backspaceAction);
        buttonBar.addSeparator();

        topBar.addSeparator(new Dimension(20, 0));

        //nerdButton = new JButton(OWLIcons.getNerdSmilingIcon());
        nerdButton = ComponentFactory.addToolBarButton(topBar, new AbstractAction("Ok", OWLIcons.getNerdSmilingIcon()) {
            public void actionPerformed(ActionEvent e) {
                if (closable) {
                    symbolEditor.assignExpression();
                }
                else {
                    symbolEditor.displayError();
                }
            }
        });

        setErrorFlag(false);
        if (closable) {
            addButton(topBar, new AbstractAction("Cancel editing", OWLIcons.getCloseIcon()) {
                public void actionPerformed(ActionEvent e) {
                    symbolEditor.cancelEditing();
                }
            });
        }

        if (middleBar != null) {
            middleBar.add(Box.createHorizontalGlue());
        }
        topBar.add(Box.createHorizontalGlue());

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        setSize(getPreferredSize());

        if (draggable) {
            ComponentDragger l = new ComponentDragger(this);
            addMouseListener(l);
            addMouseMotionListener(l);
        }
    }


    protected Action addAction(String text, String iconName, String insert, JToolBar toolBar) {
        return addAction(text, insert, OWLIcons.getImageIcon(iconName), toolBar);
    }


    private Action addAction(String text, final String insert, Icon icon, JToolBar toolBar) {
        String label = "Insert " + text;
        if (!text.equals(insert)) {
            label += " (" + insert + ")";
        }
        final AbstractAction action = new AbstractAction(label, icon) {
            public void actionPerformed(ActionEvent e) {
                symbolEditor.insertText(insert);
            }
        };
        JButton button = toolBar.add(action);
        button.setToolTipText(label);
        return action;
    }


    protected JButton addButton(JToolBar toolBar, Action action) {
        JButton button = toolBar.add(action);
        String label = (String) action.getValue(Action.NAME);
        button.setToolTipText(label);
        return button;
    }


    public void displayError(Throwable ex) {
        if (ex == null) {
            setErrorFlag(false);
            errorLabel.setText(" ");
        }
        else {
            setErrorFlag(true);
            String message = getDisplayErrorMessage(ex);
            errorLabel.setText("Error: " + message);
        }
    }


    protected abstract String getDisplayErrorMessage(Throwable ex);


    public void displayError(String message) {
        setErrorFlag(true);
        errorLabel.setText("Error: " + message);
    }


    public void enableActions(boolean clses, boolean instances) {
        classAction.setEnabled(clses);
        propertyAction.setEnabled(clses);
        individiualAction.setEnabled(instances || clses);
    }


    public OWLModel getOWLModel() {
        return owlModel;
    }


    protected SymbolEditor getSymbolEditor() {
        return symbolEditor;
    }


    protected void initMiddleBar(JToolBar middleBar) {
        // Overload this to add buttons to the (optional) middle toolbar
    }


    protected abstract void initTopBar(JToolBar topBar);


    protected void insertCls(Cls cls) {
        String name = cls.getBrowserText();
        symbolEditor.insertText(name + " ");
    }


    protected void insertIndividual(RDFResource instance) {
        String name;
        if (instance instanceof RDFSDatatype) {
            name = instance.getLocalName();
        }
        else {
            name = instance.getBrowserText();
        }
        symbolEditor.insertText(name + " ");
    }


    protected void insertSlot(Slot slot) {
        String name = slot.getBrowserText();
        symbolEditor.insertText(name + " ");
    }


    public void setSymbolEditor(SymbolEditor editor) {
        this.symbolEditor = editor;
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        enableActions(enabled, enabled);
        nerdButton.setEnabled(enabled);
        backspaceAction.setEnabled(enabled);
    }


    public void setErrorFlag(boolean error) {
        nerdButton.setIcon(error ? OWLIcons.getNerdErrorIcon() : OWLIcons.getNerdSmilingIcon());
        nerdButton.setToolTipText(error ? "Show error message" : "Assign (Ok)");
    }


    private class InsertClassAction extends ResourceSelectionAction {

        InsertClassAction() {
            super("Insert class...", OWLIcons.getImageIcon(OWLIcons.PRIMITIVE_OWL_CLASS));
        }


        public void resourceSelected(RDFResource resource) {
            insertCls((Cls) resource);
        }


        public Collection getSelectableResources() {
            return OWLUtil.getSelectableNamedClses(owlModel);
        }


        public RDFResource pickResource() {
            return ProtegeUI.getSelectionDialogFactory().selectClass(SymbolPanel.this, owlModel);
        }
    }


    private class InsertIndividualAction extends ResourceSelectionAction {


        InsertIndividualAction() {
            super("Insert individual...", OWLIcons.getImageIcon(OWLIcons.RDF_INDIVIDUAL));
        }


        public void actionPerformed(ActionEvent e) {
            Collection sels = ProtegeUI.getSelectionDialogFactory().selectResourcesByType(SymbolPanel.this, owlModel,
                    Collections.singleton(owlModel.getOWLThingClass()), "Select the resources to insert");
            for (Iterator it = sels.iterator(); it.hasNext();) {
                Frame frame = (Frame) it.next();
                if (frame instanceof RDFResource) {
                    resourceSelected((RDFResource) frame);
                }
            }
        }


        public void resourceSelected(RDFResource resource) {
            insertIndividual(resource);
        }


        public Collection getSelectableResources() {
            Collection frames = owlModel.getOWLIndividuals();
            java.util.List copy = new ArrayList(frames);
            Collections.sort(copy, new FrameComparator());
            return copy;
        }


        public RDFResource pickResource() {
            Collection resources = getSelectableResources();
            return ProtegeUI.getSelectionDialogFactory().selectResourceFromCollection(SymbolPanel.this,
                    owlModel, resources, "Select the resource to insert");
        }
    }


    private class InsertPropertyAction extends ResourceSelectionAction {

        InsertPropertyAction() {
            super("Insert property...", OWLIcons.getImageIcon(OWLIcons.RDF_PROPERTY));
        }


        public void resourceSelected(RDFResource resource) {
            insertSlot((Slot) resource);
        }


        public Collection getSelectableResources() {
            java.util.List result = new ArrayList();
            Iterator it = owlModel.getVisibleUserDefinedRDFProperties().iterator();
            while (it.hasNext()) {
                RDFProperty property = (RDFProperty) it.next();
                if (!property.isAnnotationProperty()) {
                    result.add(property);
                }
            }
            return result;
        }


        public RDFResource pickResource() {
            Collection properties = getSelectableResources();
            return ProtegeUI.getSelectionDialogFactory().selectProperty(SymbolPanel.this, owlModel, properties, "Select the property to insert");
        }
    }
}
