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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.cls.Hierarchy;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A SubsumptionTreePanel optimized for the inferred tree.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InferredSubsumptionTreePanel extends SubsumptionTreePanel {

    private Action assertAction = new AbstractAction("Assert selected change(s)",
                                                     OWLIcons.getAssertChangeIcon()) {
        public void actionPerformed(ActionEvent e) {
            assertSelectedChange();
        }
    };

    private Action displayChangedAction = new AbstractAction("Display changed classes in list",
                                                             OWLIcons.getDisplayChangedClassesIcon()) {
        public void actionPerformed(ActionEvent e) {
            OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(InferredSubsumptionTreePanel.this);
            if (tab != null) {
                tab.refreshChangedClses();
            }
        }
    };

    private Action saveInferredAction = new AbstractAction("Save inferred version as file...",
                                                           OWLIcons.getSaveInferredIcon()) {
        public void actionPerformed(ActionEvent e) {
            saveInferred();
        }
    };

    private final static String TITLE = "Inferred Hierarchy";


    public InferredSubsumptionTreePanel(OWLModel owlModel) {
        super(owlModel.getOWLThingClass(),
              ((AbstractOWLModel) owlModel).getProtegeInferredSubclassesProperty(),
              ((AbstractOWLModel) owlModel).getProtegeInferredSuperclassesProperty(),
              true);

        assertAction.setEnabled(false);
        getLabeledComponent().addHeaderButton(assertAction);
        getLabeledComponent().addHeaderButton(displayChangedAction);
        getLabeledComponent().addHeaderButton(saveInferredAction);
    }


    private void assertSelectedChange() {
        OWLNamedClass cls = getSelectedCls();
        OWLModel owlModel = getOWLModel();
        ChangedClassesPanel ccp = ChangedClassesPanel.get(owlModel);
        try {
            owlModel.beginTransaction("Assert change for " + cls.getBrowserText());
            ccp.getTableModel().assertChange(cls);
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    public Hierarchy createClone() {
        return new InferredSubsumptionTreePanel(getOWLModel());
    }


    @Override
	protected ClassTree createSelectableTree(Action viewAction, LazyTreeRoot root) {
        return new InferredChangesClassTree(viewAction, root);
    }


    @Override
	protected Action createViewClsAction() {
        return new AbstractAction("View class", OWLIcons.getViewIcon()) {
            public void actionPerformed(ActionEvent e) {
                Collection selection = getSelection();
                if (!selection.isEmpty()) {
                    Cls cls = (Cls) selection.iterator().next();
                    cls.getProject().show(cls);
                }
            }
        };
    }


    public String getTitle() {
        return TITLE;
    }


    public void navigateToResource(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            setSelectedClass((RDFSClass) resource);
        }
    }

    @Override
	public void setSelectedClass(RDFSClass cls) {
    	OWLUI.setSelectedNodeInTree((SelectableTree) getTree(), cls, getOWLModel().getSystemFrames().getProtegeInferredSuperclassesProperty());
    }


    @SuppressWarnings("deprecation")
    private void saveInferred() {
        JFileChooser fileChooser = ComponentFactory.createFileChooser("Select file", "owl");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            OWLModel owlModel = getOWLModel();
            Collection clses = new ArrayList();
            for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
                OWLNamedClass cls = (OWLNamedClass) it.next();
                if (!cls.isProbeClass()) {
                    clses.add(cls);
                }
            }
            String baseURI = owlModel.getNamespaceManager().getDefaultNamespace();
            if (baseURI == null) {
                baseURI = owlModel.getDefaultOWLOntology().getName();
            }
            if (baseURI.endsWith("#")) {
                baseURI = baseURI.substring(0, baseURI.length() - 1);
            }
            JenaCreator jenaCreator = new JenaCreator(owlModel, false, true, clses,
                    new ModalProgressBarManager("Preparing File"));
            OntModel ontModel = jenaCreator.createOntModel();
            //engine.run();
            final String ns = owlModel.getNamespaceManager().getDefaultNamespace();
            try {
                Ontology ontology = Jena.getDefaultJenaOntology(ns, ontModel);
                ontology.addProperty(OWL.versionInfo, ontModel.createTypedLiteral("classified"));
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Could not set the ontology annotation classified. Probably the ontology has an invalid name.", e);
            }
            boolean success = false;
            try {
                JenaOWLModel.save(file, ontModel, FileUtils.langXMLAbbrev, ns);
                success = true;
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "There was an error at saving the inferred ontology.", e);
                success = false;
            }
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    (success ?  "Successfully saved inferred ontology" : "There was an error saving inferred ontology") +
                    " to " + file + ".");
        }
    }


    @Override
	protected void updateActions() {
        super.updateActions();
        OWLNamedClass cls = getSelectedCls();
        assertAction.setEnabled(cls != null &&
                                cls.getClassificationStatus() == OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED &&
                                ChangedClassesPanel.get(getOWLModel()).getTableModel().contains(cls));
    }


    private class InferredChangesClassTree extends ClassTree {

        public InferredChangesClassTree(Action viewAction, LazyTreeRoot root) {
            super(viewAction, root);

            RDFProperty property = getOWLModel().getRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
            setCellRenderer(new MovedResourcesRenderer(property));
        }

        @Override
		public String getToolTipText(MouseEvent event) {
            String str = null;
            int row = getRowForLocation(event.getX(), event.getY());
            TreePath path = getPathForRow(row);
            if (path != null && path.getPathCount() > 0) {
                SubsumptionTreeNode node = (SubsumptionTreeNode) path.getLastPathComponent();
                RDFSClass cls = (RDFSClass) node.getCls();
                ChangedClassesPanel ccp = ChangedClassesPanel.get(getOWLModel());
                str = ccp.getChangeText(cls);
                if (str == null /*&& OWLMenuProjectPlugin.isProseActivated()*/) {
                    str = OWLUI.getOWLToolTipText(cls);
                }
            }
            return str;
        }
    }

    private class MovedResourcesRenderer extends ResourceRenderer {

        public MovedResourcesRenderer(Slot directSuperclassesSlot) {
            super(directSuperclassesSlot);
        }

        @Override
		protected Color getTextColor() {
            ChangedClassesPanel ccp = ChangedClassesPanel.get(getOWLModel());
            if (ccp.contains(loadedClass)) {
                return Color.blue;
            }
            return super.getTextColor();
        }
    }
}
