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

package edu.stanford.smi.protegex.owl.ui.forms;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Vocabulary definitions from /work/src/protege-owl/plugins/edu.stanford.smi.protegex.owl/forms/forms.owl 
 * @author Auto-generated by schemagen on 12 Dec 2006 11:28 
 */
public class FormsNames {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.owl-ontologies.com/forms/forms.owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final ObjectProperty layoutData = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#layoutData" );
    
    public static final ObjectProperty widgets = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#widgets" );
    
    public static final ObjectProperty forProperty = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#forProperty" );
    
    public static final ObjectProperty properties = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#properties" );
    
    public static final ObjectProperty property_list = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#property_list" );
    
    public static final ObjectProperty forClass = m_model.createObjectProperty( "http://www.owl-ontologies.com/forms/forms.owl#forClass" );
    
    public static final DatatypeProperty name = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#name" );
    
    public static final DatatypeProperty booleanValue = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#booleanValue" );
    
    public static final DatatypeProperty integer_value = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#integer_value" );
    
    public static final DatatypeProperty preferredHeight = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#preferredHeight" );
    
    public static final DatatypeProperty preferredWidth = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#preferredWidth" );
    
    public static final DatatypeProperty string_value = m_model.createDatatypeProperty( "http://www.owl-ontologies.com/forms/forms.owl#string_value" );
    
    public static final OntClass Integer = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#Integer" );
    
    public static final OntClass LayoutData = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#LayoutData" );
    
    public static final OntClass FormWidget = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#FormWidget" );
    
    public static final OntClass Widget = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#Widget" );
    
    public static final OntClass Boolean = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#Boolean" );
    
    public static final OntClass String = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#String" );
    
    public static final OntClass PropertyList = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#PropertyList" );
    
    public static final OntClass Property = m_model.createClass( "http://www.owl-ontologies.com/forms/forms.owl#Property" );
    
}
