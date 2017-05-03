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

package edu.stanford.smi.protegex.owl.ui.results;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OverlayIcon;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The base class of components that can appear in the results area in the
 * bottom of the screen.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class ResultsPanel extends JPanel implements Disposable {

    public static MouseListener mouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent event) {
            AbstractButton button = getButton(event);
            button.setBorderPainted(button.isEnabled());
        }


        public void mouseExited(MouseEvent event) {
            getButton(event).setBorderPainted(false);
        }


        private AbstractButton getButton(MouseEvent event) {
            return (AbstractButton) event.getSource();
        }
    };

    private OWLModel owlModel;

    protected JToolBar toolBar;


    /**
     * @deprecated please use the other constructor
     */
    protected ResultsPanel() {
        this(null);
    }


    protected ResultsPanel(OWLModel owlModel) {

        this.owlModel = owlModel;

        setLayout(new BorderLayout());

        toolBar = ComponentFactory.createToolBar();
        toolBar.setOrientation(JToolBar.VERTICAL);
        toolBar.add(Box.createVerticalGlue());

        Action closeAction = new AbstractAction("Close", OWLIcons.getCloseIcon()) {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        };

        addButton(closeAction);
        addSeparator();

        add(BorderLayout.CENTER, new JPanel());
        toolBar.add(Box.createVerticalBox());
        add(BorderLayout.WEST, toolBar);
    }


    protected JButton addButton(Action action) {
        JButton button = ComponentFactory.addToolBarButton(toolBar, action);
        Icon icon = (Icon) action.getValue(Action.SMALL_ICON);
        if (icon instanceof OverlayIcon) {
            button.setDisabledIcon(((OverlayIcon) icon).getGrayedIcon());
        }
        if (action instanceof ResourceSelectionAction) {
            ((ResourceSelectionAction) action).activateComboBox(button);
        }
        toolBar.add(button, 1);
        return button;
    }


    protected void addSeparator() {
        toolBar.add(new JToolBar.Separator(), 1);
    }


    public void close() {
        ResultsPanelManager.closeResultsPanel(owlModel, this);
    }


    /**
     * Called when this is closed by the user.  Can be overloaded to do clean-up etc.
     */
    public void dispose() {
        // Does nothing by default
    }


    public Icon getIcon() {
        return null;
    }


    public OWLModel getOWLModel() {
        return owlModel;
    }


    public abstract String getTabName();


    public boolean isReplaceableBy(ResultsPanel resultsPanel) {
        return getTabName().equals(resultsPanel.getTabName());
    }


    /**
     * Puts the main component into the center of this.
     * This method should be called in the constructor.
     *
     * @param component the Component to put into the center
     */
    public void setCenterComponent(Component component) {
        add(BorderLayout.CENTER, component);
    }


    /**
     * @deprecated use showHostResource
     */
    public void showHostInstance(Instance instance) {
        if (instance instanceof RDFResource) {
            showHostResource((RDFResource) instance);
        }
    }


    public void showHostResource(RDFResource resource) {
        ResultsPanelManager.showHostResource(resource);
    }


    public void showTriple(Triple triple) {
        ResultsPanelManager.showTriple(triple);
    }
}
