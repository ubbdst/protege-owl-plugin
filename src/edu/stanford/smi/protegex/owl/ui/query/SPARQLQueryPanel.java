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

package edu.stanford.smi.protegex.owl.ui.query;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.testing.constraints.SPARQLAssertTest;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SPARQLQueryPanel extends JPanel {

    private OWLModel owlModel;

    private JTextArea textArea;

    private static String TEXT = "SELECT ?subject ?object\nWHERE { ?subject rdfs:subClassOf ?object }";


    public SPARQLQueryPanel(OWLModel owlModel) {

        this.owlModel = owlModel;

        textArea = new JTextArea(TEXT);
        OWLLabeledComponent lc = new OWLLabeledComponent("Query", new JScrollPane(textArea));
        JButton queryButton = new JButton("Execute Query");
        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        lc.addHeaderButton(new AbstractAction("Assert matches for this query", OWLIcons.getImageIcon(OWLIcons.ASSERT_TRUE)) {
            public void actionPerformed(ActionEvent e) {
                handleAssertMatches(SPARQLAssertTest.NOT_EMPTY_PROPERTY_URI, SPARQLAssertTest.EMPTY_PROPERTY_URI);
            }
        });

        lc.addHeaderButton(new AbstractAction("Assert no matches for this query", OWLIcons.getImageIcon(OWLIcons.ASSERT_FALSE)) {
            public void actionPerformed(ActionEvent e) {
                handleAssertMatches(SPARQLAssertTest.EMPTY_PROPERTY_URI, SPARQLAssertTest.NOT_EMPTY_PROPERTY_URI);
            }
        });
        lc.setPreferredSize(new Dimension(300, 50));
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        add(BorderLayout.SOUTH, queryButton);
    }


    private boolean canAssert(String propertyURI) {
        String propertyName = owlModel.getResourceNameForURI(propertyURI);
        if (propertyName != null && owlModel.getRDFProperty(propertyName) != null) {
            if (ProtegeUI.getProjectView(owlModel.getProject()).getTabbedPane().getSelectedComponent() instanceof OWLClassesTab) {
                return true;
            }
            else {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "You need to select a class in the OWLClasses tab\nbefore you can assert a query.");
                return false;
            }
        }
        else {
            if (owlModel instanceof JenaOWLModel) {
                if (ProtegeUI.getModalDialogFactory().showConfirmDialog(this,
                        "You need to import the following ontology before you\ncan use the assert queries support:\n" +
                                SPARQLAssertTest.URI + "\nShall this be added now?", "Missing import")) {
                    try {
                        owlModel.getNamespaceManager().setPrefix(SPARQLAssertTest.NAMESPACE, SPARQLAssertTest.PREFIX);
	                    ImportHelper importHelper = new ImportHelper((JenaOWLModel) owlModel);
	                    importHelper.addImport(URI.create(SPARQLAssertTest.URI));
	                    importHelper.importOntologies();
                    }
                    catch (Exception ex) {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                "Import failed: " + ex);
                    }
                }
            }
            return false;
        }
    }


    private void executeQuery() {
        SPARQLResultsPanel resultsPanel = SPARQLOWLModelAction.show(owlModel, false);
        String queryText = getQueryText();
        resultsPanel.executeQuery(queryText);
    }


    public String getQueryText() {
        return textArea.getText();
    }


    private void handleAssertMatches(String propertyURI, String inversePropertyURI) {
        if (canAssert(propertyURI)) {
            OWLClassesTab classesTab = (OWLClassesTab) ProtegeUI.getProjectView(owlModel.getProject()).getTabByClassName(OWLClassesTab.class.getName());
            RDFSNamedClass subject = classesTab.getSelectedClass();
            if (subject != null) {
                String propertyName = owlModel.getResourceNameForURI(propertyURI);
                RDFProperty property = owlModel.getRDFProperty(propertyName);
                String queryText = getQueryText();
                if (!subject.getPropertyValues(property).contains(queryText)) {
                    subject.addPropertyValue(property, queryText);
                }
                String inversePropertyName = owlModel.getResourceNameForURI(inversePropertyURI);
                RDFProperty inverseProperty = owlModel.getRDFProperty(inversePropertyName);
                if (inverseProperty != null) {
                    subject.removePropertyValue(inverseProperty, queryText);
                }
            }
        }
    }


    public void rememberQueryText() {
        TEXT = getQueryText();
    }


    public void setQueryText(String str) {
        textArea.setText(str);
    }
}
