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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.compact.TokenMgrError;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A JPanel hosting buttons that accellerate editing of OWL expressions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLSymbolPanel extends SymbolPanel {

    private Action allValuesFromAction;

    private Action cardinalityAction;

    private Action complementOfAction;

    private Action enumerationAction;

    private ResourceSelectionAction rdfDatatypeAction;

    private Action falseAction;

    private Action hasValueAction;

    private Action intersectionOfAction;

    private Action maxCardinalityAction;

    private Action minCardinalityAction;

    private Action roundBracketsAction;

    private Action someValuesFromAction;

    private Action trueAction;

    private Action unionOfAction;


    public OWLSymbolPanel(OWLModel owlModel, final boolean closable) {
        this(owlModel, closable, false);
    }


    public OWLSymbolPanel(OWLModel owlModel, final boolean closable, boolean draggable) {
        super(owlModel, closable, draggable);
    }


    public void enableActions(Slot slot, Cls restrictionsMetaCls) {
        boolean clses = false;
        boolean instances = false;
        boolean booleans = false;
        boolean datatypes = false;
        if (restrictionsMetaCls != null && slot != null) {
            String metaClsName = restrictionsMetaCls.getName();
            if (ProfilesManager.isFeatureSupported(getOWLModel(), OWLProfiles.Qualified_Cardinality_Restrictions)) {
                metaClsName = OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION;
            }
            if (OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION.equals(metaClsName) ||
                    OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION.equals(metaClsName)) {
                if (!(slot instanceof OWLDatatypeProperty)) {
                    clses = true;
                    instances = true;
                    booleans = true;
                }
                datatypes = true;
            }
            else if (OWLNames.Cls.HAS_VALUE_RESTRICTION.equals(metaClsName)) {
                if (slot instanceof OWLDatatypeProperty) {
                    booleans = true;
                }
                else {
                    boolean full = OWLUtil.hasOWLFullProfile(getOWLModel());
                    clses = full;
                    booleans = full;
                    datatypes = full;
                    instances = true;
                }
            }
        }
        enableActions(clses, instances, datatypes, booleans);
    }


    public void enableActions(boolean clses, boolean instances) {
        super.enableActions(clses, instances);
        enableActions(clses, instances, true, true);
    }


    public void enableActions(boolean clses, boolean instances, boolean datatypes, boolean booleans) {

        super.enableActions(clses, instances);

        OWLModel owlModel = getOWLModel();
        allValuesFromAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.AllValuesFrom_Restrictions));
        cardinalityAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.MaxCardinality_Restrictions) &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.MinCardinality_Restrictions));
        complementOfAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Complement_Classes));
        rdfDatatypeAction.setEnabled(datatypes);
        hasValueAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.HasValue_Restrictions));
        intersectionOfAction.setEnabled(clses);
        maxCardinalityAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.MaxCardinality_Restrictions));
        minCardinalityAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.MinCardinality_Restrictions));
        someValuesFromAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.SomeValuesFrom_Restrictions));
        unionOfAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Union_Classes));
        roundBracketsAction.setEnabled(clses);
        enumerationAction.setEnabled(clses &&
                ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Enumerated_Classes));

        falseAction.setEnabled(booleans);
        trueAction.setEnabled(booleans);
    }


    public String getDisplayErrorMessage(Throwable ex) {
        String message = null;
        if (ex instanceof OWLClassParseException) {
            OWLClassParseException pex = (OWLClassParseException) ex;
            if (pex.getMessage() != null) {
                message = pex.getMessage() + " at ";
            }
            else {
                message = "Invalid symbol after ";
            }
            String image = pex.currentToken;
            String token = OWLTextFormatter.getDisplayString(image == null ? "" : image);
            message += "\"" + token + "\"";
        }
        else if (ex instanceof TokenMgrError) {
            TokenMgrError error = (TokenMgrError) ex;
            message = error.getMessage();
        }
        else if (ex instanceof NumberFormatException) {
            message = "Please enter a number " + DefaultOWLMinCardinality.OPERATOR + " 0";
        }
        else {
            message = ex.toString();
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        return message;
    }


    protected void initMiddleBar(JToolBar middleBar) {

        classAction.activateComboBox(addButton(middleBar, classAction));
        propertyAction.activateComboBox(addButton(middleBar, propertyAction));
        individiualAction.activateComboBox(addButton(middleBar, individiualAction));
        middleBar.addSeparator();

        rdfDatatypeAction = new ResourceSelectionAction("Insert datatype...",
                OWLIcons.getImageIcon(OWLIcons.RDF_DATATYPE)) {

            public Collection getSelectableResources() {
                Collection valids = new ArrayList(getOWLModel().getRDFSDatatypes());
                valids.remove(getOWLModel().getRDFXMLLiteralType());
                return valids;
            }


            public void resourceSelected(RDFResource resource) {
                insertIndividual(resource);
            }
            
            @Override
            public RDFSDatatype pickResource() {
                return ProtegeUI.getSelectionDialogFactory().selectDatatype(OWLSymbolPanel.this, getOWLModel());
            }
        };
        JButton db = addButton(middleBar, rdfDatatypeAction);
        rdfDatatypeAction.activateComboBox(db);
        trueAction = addAction("true", "True", "true", middleBar);
        falseAction = addAction("false", "False", "false", middleBar);

        middleBar.addSeparator();

        enumerationAction = new AbstractAction("Insert curly brackets for enumerations: { }",
                OWLIcons.getImageIcon("OWLEnumeratedClass")) {
            public void actionPerformed(ActionEvent e) {
                getSymbolEditor().insertText("{}", 1);
            }
        };
        addButton(middleBar, enumerationAction);
        roundBracketsAction = new AbstractAction("Insert round brackets: ( )",
                OWLIcons.getImageIcon("RoundBrackets")) {
            public void actionPerformed(ActionEvent e) {
                getSymbolEditor().insertText("()", 1);
            }
        };
        addButton(middleBar, roundBracketsAction);
    }


    protected void initTopBar(JToolBar topBar) {
        OWLClassDisplay renderer = getOWLModel().getOWLClassDisplay();
        allValuesFromAction = addAction("allValuesFrom", "OWLAllValuesFrom", "" + renderer.getOWLAllValuesFromSymbol() + " ", topBar);
        someValuesFromAction = addAction("someValuesFrom", "OWLSomeValuesFrom", "" + renderer.getOWLSomeValuesFromSymbol() + " ", topBar);
        hasValueAction = addAction("hasValue", "OWLHasValue", " " + renderer.getOWLHasValueSymbol() + " ", topBar);
        topBar.addSeparator();
        cardinalityAction = addAction("cardinality", "OWLCardinality", " " + renderer.getOWLCardinalitySymbol() + " ", topBar);
        minCardinalityAction = addAction("minCardinality", "OWLMinCardinality", " " + renderer.getOWLMinCardinalitySymbol() + " ", topBar);
        maxCardinalityAction = addAction("maxCardinality", "OWLMaxCardinality", " " + renderer.getOWLMaxCardinalitySymbol() + " ", topBar);
        topBar.addSeparator();
        intersectionOfAction = addAction("intersectionOf", "OWLIntersectionClass", " " + renderer.getOWLIntersectionOfSymbol() + " ", topBar);
        unionOfAction = addAction("unionOf", "OWLUnionClass", " " + renderer.getOWLUnionOfSymbol() + " ", topBar);
        complementOfAction = addAction("complementOf", "OWLComplementClass", "" + renderer.getOWLComplementOfSymbol(), topBar);
    }


    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        enableActions(enabled, enabled, enabled, enabled);
    }
}
