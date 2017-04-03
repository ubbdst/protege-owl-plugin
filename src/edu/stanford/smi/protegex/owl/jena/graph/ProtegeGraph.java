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

package edu.stanford.smi.protegex.owl.jena.graph;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.impl.GraphBase;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.graph.query.SimpleQueryHandler;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NullIterator;
import com.hp.hpl.jena.util.iterator.SingletonIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An experimental implementation of the Jena Graph interface so that
 * a Protege triple store is wrapped.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeGraph extends GraphBase implements Graph {

    private Capabilities capabilities = new ProtegeCapabilities();

    private OWLModel owlModel;

    private PrefixMapping prefixMapping;

    private TripleStore ts;


    public ProtegeGraph(OWLModel owlModel, TripleStore ts) {
        this.owlModel = owlModel;
        this.ts = ts;
        prefixMapping = new ProtegePrefixMapping(owlModel, ts);
    }


    public void add(Triple triple) throws AddDeniedException {
        throw new RuntimeException("Not supported yet");
    }


    public void close() {
    }


    private ExtendedIterator createExtendedIterator(Iterator protegeTriplesIterator) {
        List results = createJenaTriplesList(protegeTriplesIterator);
        return WrappedIterator.create(results.iterator());
    }


    private List createJenaTriplesList(Iterator protegeTriplesIterator) {
        List results = new ArrayList();
        while (protegeTriplesIterator.hasNext()) {
            edu.stanford.smi.protegex.owl.model.triplestore.Triple triple = (edu.stanford.smi.protegex.owl.model.triplestore.Triple) protegeTriplesIterator.next();
            Node subjectNode = createNode(triple.getSubject());
            Node predicateNode = createNode(triple.getPredicate());
            Node objectNode = createNode(triple.getObject());
            results.add(new Triple(subjectNode, predicateNode, objectNode));
        }
        return results;
    }


    private Node createNode(Object object) {
        if (object instanceof RDFResource) {
            RDFResource resource = (RDFResource) object;
            if (resource.isAnonymous()) {
                return Node.createAnon(new AnonId(resource.getName()));
            }
            else {
                return Node.createURI(resource.getURI());
            }
        }
        else if (!(object instanceof RDFSLiteral)) {
            object = owlModel.createRDFSLiteral(object);
        }
        RDFSLiteral literal = (RDFSLiteral) object;
        String lang = literal.getLanguage();
        String lex = literal.toString();
        if (lang != null) {
            RDFDatatype rdfDatatype = getRDFDatatype(owlModel.getXSDstring());
            return Node.createLiteral(lex, lang, rdfDatatype);
        }
        else {
            RDFDatatype datatype = getRDFDatatype(literal.getDatatype());
            return Node.createLiteral(lex, null, datatype);
        }
    }


    private RDFSLiteral createRDFSLiteral(LiteralLabel literalLabel) {
        String datatypeURI = literalLabel.getDatatypeURI();
        if (datatypeURI == null) {
            String value = literalLabel.getLexicalForm();
            return owlModel.createRDFSLiteral(value, owlModel.getXSDstring());
        }
        else {
            RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(datatypeURI);
            String value = literalLabel.getLexicalForm();
            return owlModel.createRDFSLiteral(value, datatype);
        }
    }


    public boolean dependsOn(Graph graph) {
        return false;
    }


    private ExtendedIterator findAllTriples() {
        Iterator it = ts.listTriples();
        return createExtendedIterator(it);
    }


    private ExtendedIterator findWithObjectOnly(Node objectNode) {
        RDFObject object = getRDFObject(objectNode);
        if (object == null) {
            return new NullIterator();
        }
        return createExtendedIterator(ts.listTriplesWithObject(object));
    }


    private ExtendedIterator findWithPredicateOnly(Node predicateNode) {
        RDFProperty property = getRDFProperty(predicateNode);
        if (property != null) {
            Collection results = new ArrayList();
            Iterator it = ts.listSubjects(property);
            while (it.hasNext()) {
                RDFResource subject = (RDFResource) it.next();
                Node subjectNode = createNode(subject);
                Iterator vit = subject.listPropertyValues(property);
                while (vit.hasNext()) {
                    Object value = vit.next();
                    Node objectNode = createNode(value);
                    results.add(new Triple(subjectNode, predicateNode, objectNode));
                }
            }
            return WrappedIterator.create(results.iterator());
        }
        else {
            return new NullIterator();
        }
    }


    private ExtendedIterator findWithPredicateAndObject(Node predicateNode, Node objectNode) {
        RDFProperty predicate = getRDFProperty(predicateNode);
        if (predicate != null) {
            RDFObject object = getRDFObject(objectNode);
            if (object != null) {
                Collection results = new ArrayList();
                Iterator it = ts.listSubjects(predicate, object);
                while (it.hasNext()) {
                    RDFResource subject = (RDFResource) it.next();
                    Node subjectNode = createNode(subject);
                    Triple triple = new Triple(subjectNode, predicateNode, objectNode);
                    results.add(triple);
                }
                return WrappedIterator.create(results.iterator());
            }
        }
        return new NullIterator();
    }


    private ExtendedIterator findWithSubject(Node subjectNode, RDFResource subject, Node predicateNode, Node objectNode) {
        if (predicateNode != null) {
            String predicateName = getResourceName(predicateNode);
            if (predicateName == null) {
                return new NullIterator();
            }
            RDFResource predicate = owlModel.getRDFResource(predicateName);
            if (predicate instanceof RDFProperty) {
                return findWithSubjectAndPredicate(subjectNode, subject,
                        predicateNode, (RDFProperty) predicate, objectNode);
            }
            else {
                return new NullIterator();
            }
        }
        else {  // Predicate is null
            if (objectNode != null) {
                return findWithSubjectAndObject(subjectNode, subject, objectNode);
            }
            else {
                return findWithSubjectOnly(subjectNode, subject);
            }
        }
    }


    private ExtendedIterator findWithSubjectOnly(Node subjectNode, RDFResource subject) {
        // Very inefficient loop over all triples
        List results = new ArrayList();
        Iterator it = ts.listTriples();
        while (it.hasNext()) {
            edu.stanford.smi.protegex.owl.model.triplestore.Triple triple = (edu.stanford.smi.protegex.owl.model.triplestore.Triple) it.next();
            if (triple.getSubject().equals(subject)) {
                Node predicateNode = createNode(triple.getPredicate());
                Node objectNode = createNode(triple.getObject());
                results.add(new Triple(subjectNode, predicateNode, objectNode));
            }
        }
        return WrappedIterator.create(results.iterator());
    }


    private ExtendedIterator findWithSubjectAndObject(Node subjectNode, RDFResource subject, Node objectNode) {
        // Very inefficient loop over all triples
        List results = new ArrayList();
        Iterator it = ts.listTriples();
        while (it.hasNext()) {
            edu.stanford.smi.protegex.owl.model.triplestore.Triple triple = (edu.stanford.smi.protegex.owl.model.triplestore.Triple) it.next();
            if (triple.getSubject().equals(subject)) {
                Node otherObjectNode = createNode(triple.getObject());
                if (otherObjectNode.equals(objectNode)) {
                    Node predicateNode = createNode(triple.getPredicate());
                    results.add(new Triple(subjectNode, predicateNode, objectNode));
                }
            }
        }
        return WrappedIterator.create(results.iterator());
    }


    private ExtendedIterator findWithSubjectAndPredicate(Node subjectNode, RDFResource subject, Node predicateNode, RDFProperty predicate, Node objectNode) {
        if (objectNode != null) {
            Object object = null;
            if (objectNode.isLiteral()) {
                object = createRDFSLiteral(objectNode.getLiteral());
            }
            else {
                object = getRDFResource(objectNode);
                if (object == null) {
                    return new NullIterator();
                }
            }
            if (ts.contains(subject, predicate, object)) {
                return new SingletonIterator(new Triple(subjectNode, predicateNode, objectNode));
            }
            else {
                return new NullIterator();
            }
        }
        else {
            Collection triples = new ArrayList();
            Iterator it = ts.listObjects(subject, predicate);
            while (it.hasNext()) {
                Object object = it.next();
                Node node = createNode(object);
                triples.add(new Triple(subjectNode, predicateNode, node));
            }
            return WrappedIterator.create(triples.iterator());
        }
    }


    private RDFDatatype getRDFDatatype(RDFSDatatype datatype) {
        return XMLSchemaDatatypes.getRDFDatatype(datatype);
    }


    private RDFObject getRDFObject(Node objectNode) {
        RDFObject object = null;
        if (objectNode.isLiteral()) {
            object = createRDFSLiteral(objectNode.getLiteral());
        }
        else {
            object = getRDFResource(objectNode);
        }
        return object;
    }


    private RDFProperty getRDFProperty(Node predicateNode) {
        RDFResource resource = getRDFResource(predicateNode);
        if (resource instanceof RDFProperty) {
            return (RDFProperty) resource;
        }
        else {
            return null;
        }
    }


    private RDFResource getRDFResource(Node node) {
        String name = getResourceName(node);
        if (name == null) {
            return null;
        }
        else {
            return owlModel.getRDFResource(name);
        }
    }


    public Capabilities getCapabilities() {
        return capabilities;
    }


    public GraphEventManager getEventManager() {
        return null;
    }


    public PrefixMapping getPrefixMapping() {
        return prefixMapping;
    }


    private String getResourceName(Node node) {
        if (node.isBlank()) {
            return node.getBlankNodeId().toString();
        }
        else {
            String uri = node.getURI();
            return owlModel.getResourceNameForURI(uri);
        }
    }

    //public TransactionHandler getTransactionHandler() {
    //    return null;
    //}


    protected ExtendedIterator graphBaseFind(TripleMatch tripleMatch) {
        Node subjectNode = tripleMatch.getMatchSubject();
        Node predicateNode = tripleMatch.getMatchPredicate();
        Node objectNode = tripleMatch.getMatchObject();
        if (subjectNode != null) {
            RDFResource subject = getRDFResource(subjectNode);
            if (subject == null) {
                return new NullIterator();
            }
            return findWithSubject(subjectNode, subject, predicateNode, objectNode);
        }
        else {
            if (predicateNode != null) {
                if (objectNode != null) {
                    return findWithPredicateAndObject(predicateNode, objectNode);
                }
                else {
                    return findWithPredicateOnly(predicateNode);
                }
            }
            else {
                if (objectNode != null) {
                    return findWithObjectOnly(objectNode);
                }
                else {
                    return findAllTriples();
                }
            }
        }
    }


    public boolean isEmpty() {
        return false;
    }


    public boolean isIsomorphicWith(Graph graph) {
        return false;
    }


    public QueryHandler queryHandler() {
        return new SimpleQueryHandler(this);
    }

    /*public int size() {
       int count = 0;
       Iterator it = ts.listTriples();
       while (it.hasNext()) {
           it.next();
           count++;
       }
       return count;
   } */
}
