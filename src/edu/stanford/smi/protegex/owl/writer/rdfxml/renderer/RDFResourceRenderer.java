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

package edu.stanford.smi.protegex.owl.writer.rdfxml.renderer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.model.visitor.Visitable;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.Util;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: March 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A renderer that translates various OWL and RDF classes and resource
 * to RDF/XML.  Note that Anonymous resource have their properties
 * written out as well.
 */
public class RDFResourceRenderer extends OWLModelVisitorAdapter {
    private static boolean renderCardinalityAsInt = false;
    
    private Visitable resource;

    private XMLWriter writer;

    private TripleStore tripleStore;
    
    private boolean sort = false;


    public RDFResourceRenderer(Visitable rdfResource, TripleStore tripleStore, XMLWriter writer) {
        this.resource = rdfResource;
        this.tripleStore = tripleStore;
        this.writer = writer;
    }

    public RDFResourceRenderer(Visitable rdfResource, TripleStore tripleStore, XMLWriter writer, boolean sort) {
        this.resource = rdfResource;
        this.tripleStore = tripleStore;
        this.writer = writer;
        this.sort = sort;
    }
    
    public static void setRenderCardinalityAsInt(boolean renderCardinalityAsInt) {
        RDFResourceRenderer.renderCardinalityAsInt = renderCardinalityAsInt;
    }

    public void write() {
        resource.accept(this);
    }


    /**
     * Renders an allValuesFrom, or universal restriction and its various
     * properties such as owl:onProperty, owl:allValuesFrom
     */
    @Override
    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        renderQuantifierRestriction(owlAllValuesFrom, OWLNames.Slot.ALL_VALUES_FROM);
    }


    /**
     * Renders and instance of owl:AllDifferent, as an rdf collection
     * (rdf:parseType="Collection") of the distinct members.
     */
    @Override
    public void visitOWLAllDifferent(OWLAllDifferent owlAllDifferent) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.ALL_DIFFERENT, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.DISTINCT_MEMBERS, tripleStore));
            writer.writeAttribute(RDFNames.Slot.PARSE_TYPE, RDFNames.COLLECTION);
            for (Iterator it = owlAllDifferent.getDistinctMembers().iterator(); it.hasNext();) {
                RDFResource curRes = (RDFResource) it.next();
                curRes.accept(this);
            }
            writer.writeEndElement(); // end of owl:distinctMembers
            insertProperties(owlAllDifferent);
            writer.writeEndElement(); // end of owl:AllDifferent
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitOWLOntology(OWLOntology owlOntology) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.ONTOLOGY, tripleStore));
            writer.writeAttribute(RDFNames.Slot.ABOUT, Util.getResourceAttributeName(owlOntology, writer));
            insertProperties(owlOntology);
            // Imports
            for (Iterator it = owlOntology.getImports().iterator(); it.hasNext();) {
                String curImp = (String) it.next();
                writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.IMPORTS, tripleStore));
                writer.writeAttribute(RDFNames.Slot.RESOURCE, curImp);
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    /**
     * Renders an owl:Cardinality restriction using its associated
     * properties such as owl:onProperty.
     */
    @Override
    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        renderCardinalityRestriction(owlCardinality, OWLNames.Slot.CARDINALITY);
    }


    /**
     * Renders an owl:ComplementClass.  If the complemented class is a
     * named class then it is inserted as an attribute.
     */
    @Override
    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.NAMED_CLASS, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.COMPLEMENT_OF, tripleStore  ));
            RDFSClass complement = owlComplementClass.getComplement();
            Util.inlineObject(complement, tripleStore, writer);
            writer.writeEndElement(); // end of owl:complementOf
            insertProperties(owlComplementClass);
            writer.writeEndElement(); // end of owl:Class

        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    /**
     * Renders and owl:oneOf (enumerated) class.  The class is
     * rendered using an rdf collection.
     */
    @Override
    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.NAMED_CLASS, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.ONE_OF, tripleStore));
            writer.writeAttribute(Util.getPrefixedName(RDFNames.Slot.PARSE_TYPE, tripleStore), RDFNames.COLLECTION);
            TreeSet values = new TreeSet(new FrameComparator());
            values.addAll(owlEnumeratedClass.getOneOf());
            for (Iterator it = values.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                new RDFResourceRenderer(resource, tripleStore, writer).write();
            }
            writer.writeEndElement(); // end of owl:oneOf
            insertProperties(owlEnumeratedClass);
            writer.writeEndElement(); // end of owl:Class

        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        try {
            // Will have restriction element to close!!
            writeRestrictionStart(owlHasValue);
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.HAS_VALUE, tripleStore));
            Object value = owlHasValue.getHasValue();
            if (value instanceof RDFResource) {
                Util.inlineObject((RDFResource) value, tripleStore, writer);
            }
            else {
                RDFSLiteral litVal = owlHasValue.getOWLModel().asRDFSLiteral(value);
                if (litVal.getLanguage() == null) {
                    writer.writeAttribute(RDFNames.Slot.DATATYPE, litVal.getDatatype().getURI());
                }
                else {
                    writer.writeAttribute(Vocab.XML_LANG, litVal.getLanguage());
                }
                writer.writeTextContent(litVal.toString());

            }
            writer.writeEndElement(); // end of owl:hasValue
            insertProperties(owlHasValue);
            writer.writeEndElement(); // end of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        try {
            if (owlIndividual.isAnonymous() == false) {
                //Util.insertResourceAsElement(owlIndividual.getRDFType(), rdfwriter);
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Cls.DESCRIPTION, tripleStore));
                Util.insertAboutAttribute(owlIndividual, writer);
            }
            else {
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Cls.DESCRIPTION, tripleStore));
                Util.renderTypes(owlIndividual, tripleStore, owlIndividual.getOWLModel().getOWLThingClass(), writer);
                insertProperties(owlIndividual);
            }
            writer.writeEndElement(); // end of individual


        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        renderNAryLogicalClass(owlIntersectionClass, OWLNames.Slot.INTERSECTION_OF);
    }


    @Override
    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        renderCardinalityRestriction(owlMaxCardinality, OWLNames.Slot.MAX_CARDINALITY);
    }


    @Override
    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        renderCardinalityRestriction(owlMinCardinality, OWLNames.Slot.MIN_CARDINALITY);
    }


    @Override
    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.NAMED_CLASS, tripleStore));
            Util.insertAboutAttribute(owlNamedClass, writer);
            writer.writeEndElement(); // End of named class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
        renderQuantifierRestriction(owlSomeValuesFrom, OWLNames.Slot.SOME_VALUES_FROM);
    }


    @Override
    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        renderNAryLogicalClass(owlUnionClass, OWLNames.Slot.UNION_OF);
    }


    @Override
    public void visitOWLDataRange(OWLDataRange owlDataRange) {
        try {

            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.DATA_RANGE, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.ONE_OF, tripleStore));
            // Gets rendered as a list
            renderValuesAsRDFList(owlDataRange.getOneOfValueLiterals(), owlDataRange.getOWLModel());
            writer.writeEndElement(); // end of owl:oneOf
            insertProperties(owlDataRange);
            writer.writeEndElement(); // end of owl:DataRange
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void visitRDFExternalResource(RDFExternalResource rdfExternalResource) {
        try {
            writer.writeStartElement(RDFNames.Slot.RESOURCE);
            writer.writeAttribute(RDFNames.Slot.ABOUT, rdfExternalResource.getResourceURI());
            writer.writeEndElement(); // end of external resource
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitRDFDatatype(RDFSDatatype rdfsDatatype) {
        try {
            if (rdfsDatatype.isAnonymous()) {
                writer.writeStartElement(Util.getPrefixedName(RDFSNames.Cls.DATATYPE, tripleStore));
                Util.insertProperties(rdfsDatatype, tripleStore, writer, sort);
            }
            else {
                writer.writeAttribute(Util.getPrefixedName(RDFNames.Slot.RESOURCE, tripleStore), rdfsDatatype.getURI());
            }
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitRDFIndividual(RDFIndividual rdfIndividual) {
        try {
            Util.insertResourceAsElement(rdfIndividual.getRDFType(), writer);
            Util.insertAboutAttribute(rdfIndividual, writer);
            insertProperties(rdfIndividual);
            writer.writeEndElement(); // end of individual
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    @Override
    public void visitRDFList(RDFList rdfList) {
        renderValuesAsRDFList(rdfList.getValues(), rdfList.getOWLModel());
    }


    @Override
    public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        try {
            writer.writeStartElement(Util.getPrefixedName(RDFSNames.Cls.NAMED_CLASS, tripleStore));
            Util.insertAboutAttribute(rdfsNamedClass, writer);
            insertProperties(rdfsNamedClass);
            writer.writeEndElement(); // end of rdfs:Class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void writeRestrictionStart(OWLRestriction restriction) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.RESTRICTION, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.ON_PROPERTY, tripleStore));
            Util.insertResourceAttribute(restriction.getOnProperty(), writer);
            writer.writeEndElement(); // end of owl:onProperty
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderCardinalityRestriction(OWLCardinalityBase cardinalityBase, String keyWord) {
        try {
            writeRestrictionStart(cardinalityBase);
            writer.writeStartElement(Util.getPrefixedName(keyWord, tripleStore));
            writer.writeAttribute(RDFNames.Slot.DATATYPE, renderCardinalityAsInt ? XSDDatatype.XSDint.getURI() : XSDDatatype.XSDnonNegativeInteger.getURI());
            writer.writeTextContent(Integer.toString(cardinalityBase.getCardinality()));
            writer.writeEndElement(); // end of restriction type/filler
            insertProperties(cardinalityBase);
            writer.writeEndElement(); // end of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderNAryLogicalClass(OWLNAryLogicalClass logicalClass, String keyWord) {
        try {
            writer.writeStartElement(Util.getPrefixedName(OWLNames.Cls.NAMED_CLASS, tripleStore));
            writer.writeStartElement(Util.getPrefixedName(keyWord, tripleStore));
            writer.writeAttribute(RDFNames.Slot.PARSE_TYPE, RDFNames.COLLECTION);
            Collection ops = new TreeSet(new FrameComparator());
            ops.addAll(logicalClass.getOperands());
            for (Iterator it = ops.iterator(); it.hasNext();) {
                RDFResource curResource = (RDFResource) it.next();
                new RDFResourceRenderer(curResource, tripleStore, writer).write();
            }
            writer.writeEndElement(); // End of type element
            insertProperties(logicalClass);
            writer.writeEndElement(); // End of owl:Class
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderQuantifierRestriction(OWLQuantifierRestriction quantifierRestriction, String keyWord) {
        try {
            writeRestrictionStart(quantifierRestriction);
            RDFResource filler = quantifierRestriction.getFiller();
            writer.writeStartElement(Util.getPrefixedName(keyWord, tripleStore));
//            if (filler instanceof RDFSDatatype) {
//                writer.writeAttribute(RDFNames.Slot.RESOURCE, ((RDFSDatatype) filler).getURI());
//            }
//            else {
                Util.inlineObject(filler, tripleStore, writer);
            //}
            writer.writeEndElement(); // End of some or all element
            insertProperties(quantifierRestriction);
            writer.writeEndElement(); // End of owl:Restriction
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderValuesAsRDFList(Collection values, OWLModel model) {
    	renderValuesAsRDFList(values, model, RDFNames.Cls.LIST);
    }
    
    private void renderValuesAsSWRLAtomList(Collection values, OWLModel model) {
    	renderValuesAsRDFList(values, model, SWRLNames.Cls.ATOM_LIST);
    }
    
    
    private void renderValuesAsRDFList(Collection values, OWLModel model, String listElementTypes) {
        try {
            int counter = 0;
            for (Iterator it = values.iterator(); it.hasNext();) {
                writer.writeStartElement(Util.getPrefixedName(listElementTypes, tripleStore));
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Slot.FIRST, tripleStore));
                Object curVal = it.next();
                if (curVal instanceof RDFResource) {
                    Util.inlineObject((RDFResource) curVal, tripleStore, writer);
                }
                else {
                    RDFSLiteral curLiteral = model.asRDFSLiteral(curVal);
                    writer.writeAttribute(RDFNames.Slot.DATATYPE, curLiteral.getDatatype().getURI());
                    // TODO I don't know why we don't just use curLiteral.getString()  but I didn't want to break anything
                    writer.writeTextContent(curLiteral.getPlainValue() != null ? curLiteral.getPlainValue().toString() : curLiteral.getString());
                }
                writer.writeEndElement(); // End of rdf:first
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Slot.REST, tripleStore));
                if (it.hasNext() == false) {
                    writer.writeAttribute(RDFNames.Slot.RESOURCE, model.getRDFNil().getURI());
                }
                counter++;
            }
            for (int i = 0; i < counter; i++) {
                writer.writeEndElement(); // end rdf:rest
                writer.writeEndElement(); // end of rdf:List
            }
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void insertProperties(RDFResource resource) throws IOException {
        Util.insertProperties(resource, tripleStore, writer, sort);
    }

    @Override
    public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {
    	visitOWLIndividual(swrlIndividual);
    }
    
    @Override
    public void visitSWRLAtomListIndividual(SWRLAtomList swrlAtomList) {    	
    	renderValuesAsSWRLAtomList(swrlAtomList.getValues(), swrlAtomList.getOWLModel());
    }
}

