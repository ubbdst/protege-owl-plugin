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

package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.RDFAxiomRenderer;
import edu.stanford.smi.protegex.owl.writer.rdfxml.renderer.RDFResourceRenderer;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: March 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RDFResourceCollectionContentWriter implements RDFXMLContentWriter {

    private Collection resources;

    private RDFAxiomRenderer.RenderableAxiomsChecker checker;

    private TripleStore tripleStore;


    /**
     * Renders a collection of RDFResources (the axioms about
     * the resources).  Note that this does not render the opening
     * and closing rdf:RDF document element and namespace declarations.
     *
     * @param resources A collection of RDFResources.
     */
    public RDFResourceCollectionContentWriter(Collection resources, TripleStore tripleStore) {
        this.resources = resources;
        this.tripleStore = tripleStore;
        checker = RDFAxiomRenderer.getChecker();
    }


    public void writeContent(XMLWriter writer) {
        // Render the axioms of the resources
        for (Iterator it = resources.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            if (checker.isRenderable(resource)) {
                RDFAxiomRenderer render = new RDFAxiomRenderer(resource, tripleStore, writer);
                render.write();
            }
            else {
                RDFResourceRenderer renderer = new RDFResourceRenderer(resource, tripleStore, writer);
                renderer.write();
            }
        }
    }

}

