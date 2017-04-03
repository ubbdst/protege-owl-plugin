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

package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 20, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ModelMetrics extends OWLModelVisitorAdapter {

    private OWLModel model;

    private int owlClassCount;

    private int namedClassCount = 0;

    private int primitiveClassCount = 0;

    private int definedClassCount = 0;

    private int objectPropertyCount = 0;

    private int datatypePropertyCount = 0;

    private int annotationPropertyCount = 0;

    private int owlIndividualCount = 0;

    private int restrictionCount;

    private int allValuesFromCount;

    private int someValuesFromCount;

    private int hasValueCount;

    private int cardinalityCount;

    private int maxCardinalityCount;

    private int minCardinalityCount;

    private int unionClassCount;

    private int propertDomainCount;

    private int propertyRangeCount;

    private int complementClassCount;

    private int enumeratedClassCount;

    private HashMap propertyUsageCountMap;

    private HashMap classInstantiationCountMap;

    private HashMap siblingClassCount;

    private HashMap parentClassCount;

    private HashMap inferredParentClassCount;

    private int inversePropertyCount = 0;


    private boolean stop;


    public ModelMetrics(OWLModel model) {
        this.model = model;
        propertyUsageCountMap = new HashMap();
        classInstantiationCountMap = new HashMap();
        siblingClassCount = new HashMap();
        parentClassCount = new HashMap();
        inferredParentClassCount = new HashMap();
        stop = false;
    }


    public void calculateMetrics() {
        stop = false;
        for (Iterator it = model.getRDFResources().iterator(); it.hasNext();) {
            RDFResource curResource = (RDFResource) it.next();
            if (curResource.isSystem() == false) {
                curResource.accept(this);
            }
            if (stop == true) {
                break;
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////


    public int getNamedClassCount() {
        return namedClassCount;
    }


    public int getOwlClassCount() {
        return owlClassCount;
    }


    public int getPrimitiveClassCount() {
        return primitiveClassCount;
    }


    public int getDefinedClassCount() {
        return definedClassCount;
    }


    public int getObjectPropertyCount() {
        return objectPropertyCount;
    }


    public int getDatatypePropertyCount() {
        return datatypePropertyCount;
    }


    public int getAnnotationPropertyCount() {
        return annotationPropertyCount;
    }


    public int getPropertyCount() {
        return objectPropertyCount +
                datatypePropertyCount +
                annotationPropertyCount;
    }


    public int getOwlIndividualCount() {
        return owlIndividualCount;
    }


    public int getRestrictionCount() {
        return restrictionCount;
    }


    public int getAllValuesFromCount() {
        return allValuesFromCount;
    }


    public int getSomeValuesFromCount() {
        return someValuesFromCount;
    }


    public int getHasValueCount() {
        return hasValueCount;
    }


    public int getCardinalityCount() {
        return cardinalityCount;
    }


    public int getMaxCardinalityCount() {
        return maxCardinalityCount;
    }


    public int getMinCardinalityCount() {
        return minCardinalityCount;
    }


    public int getUnionClassCount() {
        return unionClassCount;
    }


    public int getPropertDomainCount() {
        return propertDomainCount;
    }


    public int getPropertyRangeCount() {
        return propertyRangeCount;
    }


    public int getComplementClassCount() {
        return complementClassCount;
    }


    public int getEnumeratedClassCount() {
        return enumeratedClassCount;
    }


    private int getMean(HashMap map) {
        int sum = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            Integer curInt = (Integer) it.next();
            sum += curInt.intValue();
        }
        return (int) Math.round(((double) sum) / map.size());
    }


    private int getMax(HashMap map) {
        int count = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            Integer i = (Integer) it.next();
            if (i.intValue() > count) {
                count = i.intValue();
            }
        }
        return count;
    }


    private int getMode(HashMap map) {
        int max = 0;
        int mode = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            Integer sibCount = (Integer) it.next();
            int curCount = ((Integer) map.get(sibCount)).intValue();
            if (curCount > max) {
                max = curCount;
                mode = sibCount.intValue();
            }
        }
        return mode;
    }


    public int getMaxSiblings() {
        return getMax(siblingClassCount);
    }


    public int getMeanSiblings() {
        return getMean(siblingClassCount);
    }


    public int getModeSiblings() {
        return getMode(siblingClassCount);
    }


    public int getMaxParents() {
        return getMax(parentClassCount);
    }


    public int getMaxInferredParents() {
        return getMax(inferredParentClassCount);
    }


    public int getMeanParents() {
        return getMean(parentClassCount);
    }


    public int getModeParents() {
        return getMode(parentClassCount);
    }


    public int getMeanInferredParents() {
        return getMean(inferredParentClassCount);
    }


    public int getModeInferredParents() {
        return getMode(inferredParentClassCount);
    }


    public int getInversePropertyCount() {
        return inversePropertyCount;
    }

////////////////////////////////////////////////////////////////////////////////////////////////


    private void incrementPropertyUsage(RDFProperty property) {
        incrementCount(property, propertyUsageCountMap);
    }


    private void incrementClassInstantiation(RDFSClass cls) {
        incrementCount(cls, classInstantiationCountMap);
    }


    private void logSiblingClassCount(int count) {
        logCount(count, siblingClassCount);
    }


    private void logParentClassCount(int count) {
        logCount(count, parentClassCount);
    }


    private void logInferredParentClassCount(int count) {
        logCount(count, inferredParentClassCount);
    }


    private void logCount(int count, HashMap map) {
        Integer countInteger = new Integer(count);
        Integer countOfCount = (Integer) map.get(countInteger);
        if (countOfCount == null) {
            countOfCount = new Integer(0);
        }
        countOfCount = new Integer(countOfCount.intValue() + 1);
        map.put(countInteger, countOfCount);
    }


    private void incrementCount(RDFResource resource, Map map) {
        Integer count = (Integer) map.get(resource);
        if (count == null) {
            count = new Integer(0);
            map.put(resource, count);
        }
        map.put(resource, new Integer(count.intValue() + 1));
    }


    public void visitOWLAllValuesFrom(OWLAllValuesFrom owlAllValuesFrom) {
        allValuesFromCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLCardinality(OWLCardinality owlCardinality) {
        cardinalityCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLComplementClass(OWLComplementClass owlComplementClass) {
        complementClassCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLDatatypeProperty(OWLDatatypeProperty owlDatatypeProperty) {
        if (owlDatatypeProperty.isAnnotationProperty()) {
            annotationPropertyCount++;
        }
        else {
            datatypePropertyCount++;
            RDFSClass domain = owlDatatypeProperty.getDomain(false);
            if (domain != null &&
                    domain.equals(model.getOWLThingClass()) == false) {
                propertDomainCount++;
            }
            incrementPropertyUsage(owlDatatypeProperty);
        }
    }


    public void visitOWLEnumeratedClass(OWLEnumeratedClass owlEnumeratedClass) {
        enumeratedClassCount++;
        owlClassCount++;
    }


    public void visitOWLHasValue(OWLHasValue owlHasValue) {
        hasValueCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLIndividual(OWLIndividual owlIndividual) {
        owlIndividualCount++;
        for (Iterator it = owlIndividual.getRDFTypes().iterator(); it.hasNext();) {
            incrementClassInstantiation((RDFSClass) it.next());
        }
    }


    public void visitOWLIntersectionClass(OWLIntersectionClass owlIntersectionClass) {
        owlClassCount++;
    }


    public void visitOWLMaxCardinality(OWLMaxCardinality owlMaxCardinality) {
        maxCardinalityCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLMinCardinality(OWLMinCardinality owlMinCardinality) {
        minCardinalityCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLNamedClass(OWLNamedClass owlNamedClass) {
        namedClassCount++;
        owlClassCount++;

        Collection namedSupers = owlNamedClass.getNamedSuperclasses();
        if (namedSupers.size() > 2) {
            namedSupers.remove(model.getOWLThingClass());
        }
        logParentClassCount(namedSupers.size());
        logInferredParentClassCount(owlNamedClass.getInferredSuperclasses().size());
        int namedSubs = owlNamedClass.getNamedSubclasses().size();
        if (namedSubs > 0) {
            logSiblingClassCount(namedSubs);
        }
        if (owlNamedClass.getEquivalentClasses().size() > 0) {
            definedClassCount++;
        }
        else {
            primitiveClassCount++;
        }

    }


    public void visitOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
        if (owlObjectProperty.isAnnotationProperty()) {
            annotationPropertyCount++;
        }
        else {
            objectPropertyCount++;
            RDFSClass domain = owlObjectProperty.getDomain(false);
            if (domain != null &&
                    domain.equals(model.getOWLThingClass()) == false) {
                propertDomainCount++;
            }
            RDFResource resource = owlObjectProperty.getRange(false);
            if (resource != null &&
                    resource.equals(model.getOWLThingClass()) == false) {
                propertyRangeCount++;
            }
            incrementPropertyUsage(owlObjectProperty);
            if (owlObjectProperty.getInverseProperty() != null) {
                inversePropertyCount++;
            }
        }
    }


    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom owlSomeValuesFrom) {
        someValuesFromCount++;
        restrictionCount++;
        owlClassCount++;
    }


    public void visitOWLUnionClass(OWLUnionClass owlUnionClass) {
        unionClassCount++;
        owlClassCount++;
    }


    public void stopCalculating() {
        stop = true;
    }
}

