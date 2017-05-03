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

package edu.stanford.smi.protegex.owl.ui.profiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.project.SettingsMap;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * <P>A singleton that provides access to the language profile of an OWLModel
 * and buffers recently loaded profiles. </P>
 * <p/>
 * <P>Each OWLModel can store two profile related strings in its Project's
 * sources.  One stores the selected predefined profile, which is one of the
 * URIs of the classes such as OWLProfiles.OWL_Full.  The other value can hold
 * the name of a custom profile file.  This file must be a valid OWL (XML) file
 * that imports the OWLProfiles.owl ontology and defines a single owl:Class
 * that acts as a superclass to all selected profile classes.  Both files are
 * optional, and the custom file has precedence over the predefined one.</P>
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProfilesManager {

    /**
     * String -> OntModel
     */
    private static Map customOntModelCache = new HashMap();

    private static OntModel defaultOntModel;

    /**
     * String -> Set of OntClasses
     */
    private static Map featuresCache = new HashMap();

    public static final String CUSTOM_PROFILE_KEY = "OWL-CUSTOM-PROFILE-URI";

    public static final String PREDEFINED_PROFILE_URI_KEY = "OWL-PREDEFINED-PROFILE-URI";


    public static void addAltEntryForOWLProfiles(OntModel ontModel) {
    	try {
    		String altEntry = new File(ProtegeOWL.getPluginFolder(),
    				"OWLProfiles.owl").toURI().toString();
    		//String altEntry = "file:" + applDir + "/plugins/" + JenaLoader.ROOT_FOLDER +
    		//        "/OWLProfiles.owl";
    		String ns = OWLProfiles.NS;
    		ontModel.getDocumentManager().addAltEntry(ns, altEntry);
    		ns = ns.substring(0, ns.length() - 1);
    		ontModel.getDocumentManager().addAltEntry(ns, altEntry);

    	} catch (Exception e) {
    		Log.getLogger().log(Level.WARNING, "Error at getting profiles", e);
    	}
    }


    public static void clearCache(String uri) {
        customOntModelCache.remove(uri);
        featuresCache.remove(uri);
    }


    public static OntModel createProfile(String defaultNamespace) {
        OntModel ontModel = ModelFactory.createOntologyModel();
        ontModel.setNsPrefix("", defaultNamespace);
        addAltEntryForOWLProfiles(ontModel);
        String partialNamespace = defaultNamespace.substring(0, defaultNamespace.length() - 1);
        Ontology ontology = ontModel.createOntology(partialNamespace);
        ontology.addImport(ontModel.getResource(OWLProfiles.NS));
        return ontModel;
    }


    public static OntClass getCustomProfileFeaturesClass(OntModel ontModel) {
        StmtIterator it = ontModel.getBaseModel().listStatements(null, RDF.type, OWL.Class);
        if (it.hasNext()) {
            Resource subject = it.nextStatement().getSubject();
            return ontModel.getOntClass(subject.getURI());
        }
        else {
            return null;
        }
    }


    private static OntModel getCustomProfileOntModel(String uri) throws Exception {
        OntModel ontModel = (OntModel) customOntModelCache.get(uri);
        if (ontModel == null) {
            ontModel = ModelFactory.createOntologyModel();
            addAltEntryForOWLProfiles(ontModel);
            if (uri.startsWith("http://")) {
                ontModel.read(uri, FileUtils.langXMLAbbrev);
            }
            else {
                ontModel.read(new FileInputStream(uri), "http://dummy.de/ontology#", FileUtils.langXMLAbbrev);
            }
            customOntModelCache.put(uri, ontModel);
        }
        return ontModel;
    }


    public static String getCustomProfileURI(OWLModel owlModel) {
        return owlModel.getOWLProject().getSettingsMap().getString(CUSTOM_PROFILE_KEY);
    }


    public static OntModel getDefaultProfileOntModel() {
        if (defaultOntModel == null) {
            defaultOntModel = ModelFactory.createOntologyModel();
            addAltEntryForOWLProfiles(defaultOntModel);
            String ns = OWLProfiles.NS;
            ns = ns.substring(0, ns.length() - 1);
            try {
                defaultOntModel.read(ns, FileUtils.langXMLAbbrev);
                if (defaultOntModel.getOntClass(OWLProfiles.OWL_Full.getURI()) == null) {
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog((OWLModel)null,
                            "Could not open default OWL Profiles file.\n" +
                                    "Please make sure that the OWLProfiles.owl file\n" +
                                    "can be found in your OWL Plugin folder.");
                    defaultOntModel = null;
                    return null;
                }
			} catch (Exception e) {
				Log.getLogger().warning("Could not open default Profile file. Message: " + e.getMessage());
			}
        }
        return defaultOntModel;
    }


    public static String getDefaultProfileURI() {
        return OWLProfiles.OWL_DL.getURI();
    }


    public static Set getFeaturesSet(String uri) {
        Set set = (Set) featuresCache.get(uri);
        if (set == null) {
            try {
                OntModel ontModel = getProfileOntModel(uri);
                OntClass ontClass = ontModel.getOntClass(uri);
                if (ontClass == null) {
                    ontClass = getCustomProfileFeaturesClass(ontModel);
                }

                if (ontClass == null) {
                	return null;
                }
                set = getSelectedClasses(ontModel, ontClass);
            }
            catch (Exception ex) {
                //OWLUI.showErrorMessageDialog("Could not open profile ontology " + uri +
                //        "\nWill use default profile instead.", "Error");
                if (!uri.equals(getDefaultProfileURI())) {
                    set = getFeaturesSet(getDefaultProfileURI());
                }
            }
            featuresCache.put(uri, set);
        }
        return set;
    }


    public static String getPredefinedProfile(OWLModel owlModel) {
        final String predefined = owlModel.getOWLProject().getSettingsMap().getString(PREDEFINED_PROFILE_URI_KEY);
        if (predefined == null) {
            return getDefaultProfileURI();
        }
        else {
            return predefined;
        }
    }


    public static String getProfile(OWLModel owlModel) {
        final String custom = getCustomProfileURI(owlModel);
        if (custom != null) {
            return custom;
        }
        else {
            return getPredefinedProfile(owlModel);
        }
    }


    public static OntModel getProfileOntModel(OWLModel owlModel) throws Exception {
        String uri = getProfile(owlModel);
        return getProfileOntModel(uri);
    }


    public static OntModel getProfileOntModel(String uri) throws Exception {
        if (uri.startsWith(OWLProfiles.NS)) {
            return getDefaultProfileOntModel();
        }
        else {
            return getCustomProfileOntModel(uri);
        }
    }


    public static Cls[] getSupportedRestrictionMetaClses(OWLModel owlModel) {
        Collection result = new ArrayList();
        if (isFeatureSupported(owlModel, OWLProfiles.AllValuesFrom_Restrictions)) {
            result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION));
        }
        if (isFeatureSupported(owlModel, OWLProfiles.SomeValuesFrom_Restrictions)) {
            result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION));
        }
        if (isFeatureSupported(owlModel, OWLProfiles.HasValue_Restrictions)) {
            result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.HAS_VALUE_RESTRICTION));
        }
        if (isFeatureSupported(owlModel, OWLProfiles.MinCardinality_Restrictions)) {
            if (isFeatureSupported(owlModel, OWLProfiles.MaxCardinality_Restrictions)) {
                result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.CARDINALITY_RESTRICTION));
            }
            result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION));
        }
        if (isFeatureSupported(owlModel, OWLProfiles.MaxCardinality_Restrictions)) {
            result.add(owlModel.getRDFSNamedClass(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION));
        }
        return (Cls[]) result.toArray(new Cls[0]);
    }


    public static Set getSelectedClasses(OntModel ontModel, OntClass featuresClass) {
        Set result = new HashSet();
        getSubclasses(result, ontModel, featuresClass, true);
        if (OWLProfiles.NS.equals(featuresClass.getNameSpace())) {
            result.add(featuresClass);
        }
        else {
            result.remove(result);
        }
        return result;
    }


    public static Iterator getSubclasses(OntModel ontModel, OntClass ontClass) {
        Set result = new HashSet();
        getSubclasses(result, ontModel, ontClass, false);
        result.remove(ontClass);
        return result.iterator();
    }


    private static void getSubclasses(Set result, OntModel ontModel, OntClass ontClass, boolean recursive) {

        for (Iterator it = ontClass.listSubClasses(true); it.hasNext();) {
            OntClass subClass = (OntClass) it.next();
            result.add(subClass);
            if (recursive) {
                getSubclasses(result, ontModel, subClass, recursive);
            }
        }

        for (Iterator it = ontModel.listNamedClasses(); it.hasNext();) {
            OntClass c = (OntClass) it.next();
            OntClass equi = c.getEquivalentClass();
            if (equi != null && equi.canAs(IntersectionClass.class)) {
                IntersectionClass in = (IntersectionClass) equi.as(IntersectionClass.class);
                for (Iterator oit = in.listOperands(); oit.hasNext();) {
                    Resource operand = (Resource) oit.next();
                    if (operand.canAs(OntClass.class)) {
                        OntClass operandClass = (OntClass) operand.as(OntClass.class);
                        if (operandClass.equals(ontClass) && !result.contains(c)) {
                            result.add(c);
                            if (recursive) {
                                getSubclasses(result, ontModel, c, recursive);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }


    public static boolean isFeatureSupported(OWLModel owlModel, OntClass ontClass) {
        String uri = getProfile(owlModel);
        Set set = getFeaturesSet(uri);
        if (set != null) {
            return set.contains(ontClass);
        }
        else {
            return true;
        }
    }


    public static OntModel loadOntModel(String uri) {
        OntModel ontModel = ModelFactory.createOntologyModel();
        addAltEntryForOWLProfiles(ontModel);
        ontModel.read(uri, FileUtils.langXMLAbbrev);
        return ontModel;
    }


    public static void saveOntModel(OntModel newOntModel, String fileName) throws IOException {
        String namespace = newOntModel.getNsPrefixURI("");
        RDFWriter writer = newOntModel.getWriter(FileUtils.langXMLAbbrev);
        Jena.prepareWriter(writer, FileUtils.langXMLAbbrev, namespace);
        OutputStream outputStream = new FileOutputStream(fileName);
        PrintStream ps = new PrintStream(outputStream);
        String encoding = SystemUtilities.getFileEncoding();
        Collection charsets = Charset.availableCharsets().keySet();
        if (!charsets.contains(encoding)) {
            encoding = "UTF-8";
        }
        writer.write(newOntModel.getBaseModel(), new OutputStreamWriter(ps, encoding), namespace);
        outputStream.close();
    }


    public static void setProfile(OWLModel owlModel, String uri) {
        setProfile(owlModel.getOWLProject(), uri);
    }


    public static void setProfile(PropertyList sources, String uri) {
        if (uri.startsWith(OWLProfiles.NS)) {
            sources.setString(PREDEFINED_PROFILE_URI_KEY, uri);
            sources.remove(CUSTOM_PROFILE_KEY);
        }
        else {
            sources.setString(CUSTOM_PROFILE_KEY, uri);
        }
    }


    public static void setProfile(OWLProject project, String uri) {
        SettingsMap settingsMap = project.getSettingsMap();
        if (uri.startsWith(OWLProfiles.NS)) {
            settingsMap.setString(PREDEFINED_PROFILE_URI_KEY, uri);
            settingsMap.remove(CUSTOM_PROFILE_KEY);
        }
        else {
            settingsMap.setString(CUSTOM_PROFILE_KEY, uri);
        }
    }
}
