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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.Set;
import java.util.HashSet;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Feb 5, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ExistentialFillerProvider extends OWLModelVisitorAdapter {

    private Set fillers;

    private Set visitedResources;

    //private OWLObjectProperty property;

    private Set properties;

    public ExistentialFillerProvider(OWLObjectProperty property) {
        fillers = new HashSet();
        visitedResources = new HashSet();
        properties = new HashSet();
        properties.add(property);
        properties.addAll(property.getSubproperties(true));
    }

    public void reset() {
        fillers.clear();
        visitedResources.clear();
    }

    public Set getFillers() {
        return fillers;
    }

    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        if(visitedResources.contains(owlNamedClass)) {
            return;
        }
        visitedResources.add(owlNamedClass);
        for (Object o : owlNamedClass.getSuperclasses(false)) {
            if (o instanceof OWLClass) {
                ((OWLClass) o).accept(this);
            }
        }
        for (Object o : owlNamedClass.getEquivalentClasses()) {
            if (o instanceof OWLClass) {
                ((OWLClass) o).accept(this);
            }
        }
    }

    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        // Intersection classes are a special case, because of the semantics
        // of intersection, we can simply process each operand of the intersection
        // rather than the intersection itself.
        for (Object o : owlIntersectionClass.getOperands()) {
            if (o instanceof OWLClass) {
                ((OWLClass) o).accept(this);
            }
        }
    }


    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom someValuesFrom) {
        if (properties.contains(someValuesFrom.getOnProperty())) {
            processFiller((OWLObjectProperty) someValuesFrom.getOnProperty(), (OWLClass) someValuesFrom.getFiller());
        }
    }

    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        processCardinality(owlMinCardinality);
    }

    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        processCardinality(owlCardinality);
    }

    /**
     * Processes different types of cardinality restrictions (really min and
     * exact) where the cardinality is greater than zero.
     * @param cardinalityBase - should be a min or max cardinality restriction
     */
    private void processCardinality(OWLCardinalityBase cardinalityBase) {
        if (cardinalityBase.getCardinality() > 0 && properties.contains(cardinalityBase.getOnProperty())) {
            if (cardinalityBase.getValuesFrom() != null) {
                if (cardinalityBase.getValuesFrom() instanceof OWLClass) {
                    processFiller((OWLObjectProperty) cardinalityBase.getOnProperty(),  (OWLClass) cardinalityBase.getValuesFrom());
                }
            }
        }
    }

    private void processFiller(OWLObjectProperty property, OWLClass cls) {
        if (cls instanceof OWLIntersectionClass) {
            OWLIntersectionClass fillerCls = (OWLIntersectionClass) cls;
            for (Object o : fillerCls.getOperands()) {
                if (o instanceof OWLClass) {
                    OWLClass curOp = (OWLClass) o;
                    fillers.add(curOp);
                    if (property.isTransitive()) {
                        curOp.accept(this);
                    }
                }
            }
        } else {
            fillers.add(cls);
            if (property.isTransitive()) {
                cls.accept(this);
            }
        }
    }
}
