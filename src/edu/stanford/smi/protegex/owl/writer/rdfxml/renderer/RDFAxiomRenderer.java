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
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
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
 * An RDF/XML renderer that renders axioms for classes,
 * properties and individuals.  For example, axioms for
 * classes include subclass axioms, equivalent class axioms,
 * disjoint axioms etc.
 */
public class RDFAxiomRenderer extends OWLModelVisitorAdapter {

    private XMLWriter writer;

    private RDFResource resource;

    private TripleStore tripleStore;
    
    private boolean sort = false;


    public RDFAxiomRenderer(RDFResource resource,
                            TripleStore tripleStore,
                            XMLWriter writer) {
        this.writer = writer;
        this.tripleStore = tripleStore;
        this.resource = resource;
    }

    public RDFAxiomRenderer(RDFResource resource,
                            TripleStore tripleStore,
                            XMLWriter writer,
                            boolean sort) {
        this.writer = writer;
        this.tripleStore = tripleStore;
        this.resource = resource;
        this.sort = sort;
    }
    protected RDFAxiomRenderer() {

    }


    public static RenderableAxiomsChecker getChecker() {
        return new RDFAxiomRenderer().new RenderableAxiomsChecker();
    }


    public void write() {
        resource.accept(this);
    }


    @Override
	public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        renderClassAxioms(owlAllValuesFrom);
    }


    @Override
	public void visitOWLCardinality(OWLCardinality owlCardinality) {
        renderClassAxioms(owlCardinality);
    }


    @Override
	public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        renderClassAxioms(owlComplementClass);
    }


    @Override
	public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        renderClassAxioms(owlEnumeratedClass);
    }


    @Override
	public void visitOWLHasValue(OWLHasValue owlHasValue) {
        renderClassAxioms(owlHasValue);
    }


    @Override
	public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        renderClassAxioms(owlIntersectionClass);
    }


    @Override
	public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        renderClassAxioms(owlMaxCardinality);
    }


    @Override
	public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        renderClassAxioms(owlMinCardinality);
    }


    @Override
	public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
        renderClassAxioms(owlSomeValuesFrom);
    }


    @Override
	public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        renderClassAxioms(owlUnionClass);
    }


    @Override
	public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        renderPropertyAxioms(owlDatatypeProperty);
    }


    @Override
	public void visitOWLIndividual(OWLIndividual owlIndividual) {
        renderIndividualAxioms(owlIndividual);
    }


    @Override
	public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        renderClassAxioms(owlNamedClass);
    }


    @Override
	public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        renderPropertyAxioms(owlObjectProperty);
    }

    @Override
    public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {    
    	renderIndividualAxioms(swrlIndividual);
    }
    
    
    @Override
	@SuppressWarnings("deprecation")
	public void visitRDFIndividual(RDFIndividual rdfIndividual) {
    	//filter out the annotations and PAL constraints
    	Cls annotationCls = rdfIndividual.getKnowledgeBase().getCls(Model.Cls.ANNOTATION);
    	Cls palCls = rdfIndividual.getKnowledgeBase().getSystemFrames().getPalConstraintCls();
    	
    	if (rdfIndividual.hasType(annotationCls) || rdfIndividual.hasType(palCls)) {
    		return;
    	}
    	
    	renderIndividualAxioms(rdfIndividual);
    }


    @Override
	public void visitRDFProperty(RDFProperty rdfProperty) {
        renderPropertyAxioms(rdfProperty);
    }


    @Override
	public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
        renderClassAxioms(rdfsNamedClass);
    }


    private void renderClassAxioms(RDFSClass cls) {
        try {
            // Attempt to abreviate the RDF/XML slightly by
            // rendering the first type triple in the specified
            // triplestore as the name of the element.
            RDFResource type = Util.getType(cls, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Cls.DESCRIPTION, tripleStore));
            }
            Util.insertIDOrAboutAttribute(cls, tripleStore, writer);

            // Render types
            renderTypes(cls, type);

            HashSet renderedEquivalentClasses = new HashSet();
            HashSet renderedNamedClassOperands = new HashSet();

            // Render equivalent classes
            RDFProperty equivClassProp = cls.getOWLModel().getRDFProperty(OWLNames.Slot.EQUIVALENT_CLASS);
            for (Iterator it = tripleStore.listObjects(cls, equivClassProp); it.hasNext();) {
                RDFResource curSuper = (RDFResource) it.next();
                if (curSuper instanceof OWLNamedClass) {
                    renderedNamedClassOperands.add(curSuper);
                }
                else if (curSuper instanceof OWLIntersectionClass) {
                    for (Iterator opIt = ((OWLIntersectionClass) curSuper).getOperands().iterator(); opIt.hasNext();) {
                        Object curOp = opIt.next();
                        if (curOp instanceof OWLNamedClass) {
                            renderedNamedClassOperands.add(curOp);
                        }
                    }
                }
                // Equivalent classes
                renderedEquivalentClasses.add(curSuper);
                writer.writeStartElement(Util.getPrefixedName(OWLNames.Slot.EQUIVALENT_CLASS, tripleStore));
                Util.inlineObject(curSuper, tripleStore, writer);
                writer.writeEndElement(); // end of owl:equivalentClass
            }

            // Render the subclass triples
            int processedSupers = 0;
            RDFProperty subClassOfProp = cls.getOWLModel().getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
            for (Iterator it = tripleStore.listObjects(cls, subClassOfProp); it.hasNext();) {
                RDFSClass curSupCls = (RDFSClass) it.next();
                // Check that the current super hasn't already been rendered as
                // an equivalent class - if not, render it.
                if (renderedEquivalentClasses.contains(curSupCls) == false &&
                        renderedNamedClassOperands.contains(curSupCls) == false) {
                    // If the class only has one subClassOf triple that has an
                    // object of owl:Thing then don't render this triple - this
                    // is consistent with the Protege2Jena converter.
                    boolean isOWLThing = curSupCls.equals(curSupCls.getOWLModel().getOWLThingClass());
                    if (isOWLThing == false ||
                            isOWLThing && it.hasNext() ||
                            isOWLThing && processedSupers > 0) {
                        writer.writeStartElement(Util.getPrefixedName(RDFSNames.Slot.SUB_CLASS_OF, tripleStore));
                        Util.inlineObject(curSupCls, tripleStore, writer);
                        writer.writeEndElement(); // end of rdfs:ubClassOf
                    }
                }
                processedSupers++;
            }

            // Annotations, sameAs, differentFrom etc. and other misc properties
            Util.insertProperties(cls, tripleStore, writer, sort);
            writer.writeEndElement(); // end of getRDFType element
        }
        catch (Exception e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderPropertyAxioms(RDFProperty property) {
        try {
            RDFResource type = Util.getType(property, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Cls.DESCRIPTION, tripleStore));
            }

            Util.insertIDOrAboutAttribute(property, tripleStore, writer);
            renderTypes(property, type);
            // Super properties rendered as property values
            // Domain - special handling to filter out owl:Thing
            Collection domains = property.getDomains(false);
            if (domains != null) {
                for (Object o : domains) {
                    if (o instanceof RDFSClass) {
                        RDFSClass domain = (RDFSClass) o;
                        if (domain.equals(property.getOWLModel().getOWLThingClass()) == false) {
                            writer.writeStartElement(Util.getPrefixedName(RDFSNames.Slot.DOMAIN, tripleStore));
                            Util.inlineObject(domain, tripleStore, writer);
                            writer.writeEndElement();
                        }
                    }
                }
            }
            // Range
            // Rendered with property values
            Util.insertProperties(property, tripleStore, writer, sort);
            writer.writeEndElement(); // End of property
        }
        catch (Exception e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderIndividualAxioms(RDFIndividual individual) {
        try {
            RDFResource type = Util.getType(individual, tripleStore);
            if (type != null) {
                Util.insertResourceAsElement(type, writer);
            }
            else {
                writer.writeStartElement(Util.getPrefixedName(RDFNames.Cls.DESCRIPTION, tripleStore));
            }
            Util.insertIDOrAboutAttribute(individual, tripleStore, writer);
            renderTypes(individual, type);
            Util.insertProperties(individual, tripleStore, writer, sort);
            writer.writeEndElement(); // end of owl:Class
        }
        catch (Exception e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    private void renderTypes(RDFResource resource,
                             RDFResource excludeType)
            throws IOException {
        Util.renderTypes(resource, tripleStore, excludeType, writer);
    }


    public class RenderableAxiomsChecker extends OWLModelVisitorAdapter {

        private boolean renderable;


        public RenderableAxiomsChecker() {

        }


        public boolean isRenderable(RDFResource resource) {
            renderable = false;
            resource.accept(this);
            return renderable;
        }


        @Override
		public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
            renderable = true;
        }


        @Override
		public void visitOWLCardinality(OWLCardinality owlCardinality) {
            renderable = true;
        }


        @Override
		public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
            renderable = true;
        }


        @Override
		public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
            renderable = true;
        }


        @Override
		public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
            renderable = true;
        }


        @Override
		public void visitOWLHasValue(OWLHasValue owlHasValue) {
            renderable = true;
        }


        @Override
		public void visitOWLIndividual(OWLIndividual owlIndividual) {
            renderable = true;
        }


        @Override
		public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
            renderable = true;
        }


        @Override
		public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
            renderable = true;
        }


        @Override
		public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
            renderable = true;
        }


        @Override
		public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
            renderable = true;
        }


        @Override
		public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
            renderable = true;
        }


        @Override
		public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
            renderable = true;
        }


        @Override
		public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
            renderable = true;
        }


        @Override
		public void visitRDFIndividual(RDFIndividual rdfIndividual) {
            renderable = true;
        }


        @Override
		public void visitRDFProperty(RDFProperty rdfProperty) {
            renderable = true;
        }


        @Override
		public void visitRDFSNamedClass(RDFSNamedClass rdfsNamedClass) {
            renderable = true;
        }
    }

}

