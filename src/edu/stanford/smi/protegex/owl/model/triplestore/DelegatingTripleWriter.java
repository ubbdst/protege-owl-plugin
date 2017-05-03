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

package edu.stanford.smi.protegex.owl.model.triplestore;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * A TripleWriter that forwards all calls into a delegate TripleWriter.
 * This can be used to chain writers, e.g. to insert a filter in front of
 * a delegate.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DelegatingTripleWriter implements TripleWriter {

    private TripleWriter delegate;


    public DelegatingTripleWriter(TripleWriter delegate) {
        this.delegate = delegate;
    }


    public void addImport(String uri) {
        delegate.addImport(uri);
    }


    public void close() throws Exception {
        delegate.close();
    }


    public TripleWriter getDelegate() {
        return delegate;
    }


    public void init(String baseURI) {
        delegate.init(baseURI);
    }


    public void setDelegate(TripleWriter delegate) {
        this.delegate = delegate;
    }


    public void write(RDFResource resource, RDFProperty property, Object object) throws Exception {
        delegate.write(resource, property, object);
    }


    public void writePrefix(String prefix, String namespace) throws Exception {
        delegate.writePrefix(prefix, namespace);
    }
}
