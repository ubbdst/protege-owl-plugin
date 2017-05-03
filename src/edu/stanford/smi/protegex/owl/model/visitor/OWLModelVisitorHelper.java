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

package edu.stanford.smi.protegex.owl.model.visitor;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 8, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLModelVisitorHelper {


    private OWLModel model;

    private OWLModelVisitor visitor;


    /**
     * Constructs a visitor helper, whose methods will visit the specified
     * <code>OWLModel</code> with the specified <code>OWLModelVisitor</code>
     *
     * @param model   The model whose elements will be visited by the methods
     *                on this helper.
     * @param visitor The visitor that will visit the elements.
     */
    public OWLModelVisitorHelper(OWLModel model, OWLModelVisitor visitor) {
        this.model = model;
        this.visitor = visitor;
    }


    public void visitUserDefinedOWLNamedClasses() {
        VisitorUtil.visitRDFResources(model.getUserDefinedOWLNamedClasses(), visitor);
    }


    public void visitUserDefinedOWLProperties() {
        VisitorUtil.visitRDFResources(model.getUserDefinedOWLProperties(), visitor);
    }


    public void visitUserDefinedRDFProperties() {
        VisitorUtil.visitRDFResources(model.getUserDefinedRDFProperties(), visitor);
    }


    public void visitUserDefinedRDFSNamedClasses() {
        VisitorUtil.visitRDFResources(model.getUserDefinedRDFSNamedClasses(), visitor);
    }
}

