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

package edu.stanford.smi.protegex.owl.jena.rdf2owl;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;

/**
 * A class that can convert pure RDF statements from a Model into corresponding
 * OWL statements.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDF2OWL {
    private static transient final Logger log  = Log.getLogger(RDF2OWL.class);

    private Model model;


    public RDF2OWL(Model model) {
        this.model = model;
    }


    /**
     * Converts all rdfs:Classes into owl:Classes
     */
    private void convertRDFSClasses() {
        for (StmtIterator it = model.listStatements(null, RDF.type, RDFS.Class); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource clazz = s.getSubject();
            s.getModel().add(clazz, RDF.type, OWL.Class);
            it.remove();
            if (log.isLoggable(Level.FINE)) {
                log.fine("Converted rdfs:Class " + clazz + " into owl:Class");
            }
        }
    }


    /**
     * Converts the properties to either owl:DatatypeProperties or owl:ObjectProperties.
     */
    private void convertProperties() {
        for (StmtIterator it = model.listStatements(null, RDF.type, RDF.Property); it.hasNext();) {
            Statement s = it.nextStatement();
            Resource property = s.getSubject();
            Resource type = getPropertyType(property);
            s.getModel().add(property, RDF.type, type);
            it.remove();
            if (log.isLoggable(Level.FINE)) {
                log.fine("Converted rdf:Property " + property + " into " + type);
            }
        }
    }


    /**
     * Converts all object occurances of rdfs:Resource with owl:Thing
     */
    private void convertRDFSResource() {
        for (StmtIterator it = model.listStatements(null, null, RDFS.Resource); it.hasNext();) {
            Statement s = it.nextStatement();
            s.getModel().add(s.getSubject(), s.getPredicate(), OWL.Thing);
            if (log.isLoggable(Level.FINE)) {
                log.fine("Replaced triple " + s + " with (x, x, owl:Thing)");
            }
            it.remove();
        }
    }


    /**
     * Attempts to find the most plausible RDF type for a given property.
     *
     * @param property the property to get the type of
     * @return either owl:DatatypeProperty or owl:ObjectProperty
     */
    private Resource getPropertyType(Resource property) {
        StmtIterator it = model.listStatements(property, RDFS.range, (RDFNode) null);
        if (it.hasNext()) {
            while (it.hasNext()) {
                Statement s = it.nextStatement();
                RDFNode n = s.getObject();
                if (n.canAs(Resource.class) &&
                        model.contains((Resource) n.as(Resource.class), RDF.type, OWL.Class)) {
                    return OWL.ObjectProperty;
                }
            }
        }
        return OWL.DatatypeProperty;
    }


    public void run() {
        unifyRDFSVersion();
        convertRDFSResource();
        // convertRDFSClasses();
        // convertProperties();
    }


    /**
     * Converts any statements containing old RDFS vocabulary to the official ones.
     */
    private void unifyRDFSVersion() {
        String[] oldRDFSNSs = {
                //"http://www.w3.org/2000/01/rdf-schema#",
                "http://www.w3.org/TR/1999/PR-rdf-schema-19990303#"  // Any others???
        };
        for (int i = 0; i < oldRDFSNSs.length; i++) {
            String oldRDFSNS = oldRDFSNSs[i];
            unifyRDFSVersion(oldRDFSNS);
        }
    }


    private void unifyRDFSVersion(String ns) {
        for (Iterator it = Jena.cloneIt(model.listStatements()); it.hasNext();) {
            Statement s = (Statement) it.next();
            Resource newSubject = s.getSubject();
            Property newPredicate = s.getPredicate();
            RDFNode newObject = s.getObject();
            boolean changed = false;
            if (ns.equals(newSubject.getNameSpace())) {
                changed = true;
                newSubject = model.getResource(RDFS.getURI() + newSubject.getLocalName());
            }
            if (ns.equals(newPredicate.getNameSpace())) {
                changed = true;
                newPredicate = model.getProperty(RDFS.getURI() + newPredicate.getLocalName());
            }
            if (newObject.canAs(Resource.class)) {
                Resource oldResource = (Resource) newObject.as(Resource.class);
                if (ns.equals(oldResource.getNameSpace())) {
                    changed = true;
                    newObject = model.getResource(RDFS.getURI() + oldResource.getLocalName());
                }
            }
            if (changed) {
                model.add(newSubject, newPredicate, newObject);
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Replaced deprecated triple " + s + " with " + newSubject + ", " + newPredicate + ", " + newObject);
                }
                it.remove();
            }
        }
    }
}
