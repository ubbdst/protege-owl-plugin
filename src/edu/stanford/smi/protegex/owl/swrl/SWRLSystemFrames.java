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

package edu.stanford.smi.protegex.owl.swrl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;

public class SWRLSystemFrames extends OWLSystemFrames {
    private static final Logger log = Log.getLogger(SWRLSystemFrames.class);
    
    
    private OWLNamedClass atomListCls, builtinAtomCls, classAtomCls, dataRangeAtomCls, 
                          dataValuedPropertyAtomCls, differentIndividualsAtomCls, impCls, 
                          individualPropertyAtomCls, sameIndividualAtomCls,
                          atomCls, variableCls, builtInCls;
    private OWLObjectProperty bodyProperty, headProperty, argumentsProperty, builtInProperty, 
                              argument1Property, classPredicateProperty, 
                              propertyPredicateProperty, dataRangeProperty;
    private OWLDatatypeProperty argsProperty, minArgsProperty, maxArgsProperty; 
    
    private RDFProperty argument2Property;
    
    private List<SWRLBuiltin> coreSWRLBuiltIns;
    
    public SWRLSystemFrames(OWLModel owlModel) {
        super(owlModel);
        createSWRLMetaModel();
    }
    
    private void createSWRLMetaModel() {
        createSWRLClasses();
        createSWRLObjectProperties();
        createSWRLDatatypeProperties();
        createSWRLBuiltIns();
        
        argument2Property = createRDFProperty(SWRLNames.Slot.ARGUMENT2);
    }
    
    private void createSWRLClasses() {
        impCls = createOWLNamedClass(SWRLNames.Cls.IMP);
        variableCls = createOWLNamedClass(SWRLNames.Cls.VARIABLE);
        builtInCls = createOWLNamedClass(SWRLNames.Cls.BUILTIN);

        atomListCls = createOWLNamedClass(SWRLNames.Cls.ATOM_LIST);

        atomCls = createOWLNamedClass(SWRLNames.Cls.ATOM);
        classAtomCls = createOWLNamedClass(SWRLNames.Cls.CLASS_ATOM);
        builtinAtomCls = createOWLNamedClass(SWRLNames.Cls.BUILTIN_ATOM);
        dataRangeAtomCls = createOWLNamedClass(SWRLNames.Cls.DATA_RANGE_ATOM);
        dataValuedPropertyAtomCls = createOWLNamedClass(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM);
        differentIndividualsAtomCls = createOWLNamedClass(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM);
        individualPropertyAtomCls = createOWLNamedClass(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM);
        sameIndividualAtomCls =createOWLNamedClass(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM);
    }
    
    private void createSWRLObjectProperties() {
        bodyProperty = createOWLObjectProperty(SWRLNames.Slot.BODY);
        headProperty = createOWLObjectProperty(SWRLNames.Slot.HEAD);
        argumentsProperty = createOWLObjectProperty(SWRLNames.Slot.ARGUMENTS);
        builtInProperty = createOWLObjectProperty(SWRLNames.Slot.BUILTIN);
        argument1Property = createOWLObjectProperty(SWRLNames.Slot.ARGUMENT1);

        classPredicateProperty = createOWLObjectProperty(SWRLNames.Slot.CLASS_PREDICATE);
        propertyPredicateProperty = createOWLObjectProperty(SWRLNames.Slot.PROPERTY_PREDICATE);
        dataRangeProperty = createOWLObjectProperty(SWRLNames.Slot.DATA_RANGE);
    }
    
    private void createSWRLDatatypeProperties() {
        argsProperty = createOWLDatatypeProperty(SWRLNames.Slot.ARGS);
        minArgsProperty = createOWLDatatypeProperty(SWRLNames.Slot.MIN_ARGS);
        maxArgsProperty = createOWLDatatypeProperty(SWRLNames.Slot.MAX_ARGS);
    }
    
    private void createSWRLBuiltIns() {
        coreSWRLBuiltIns = new ArrayList<SWRLBuiltin>();

        createSWRLBuiltin(SWRLNames.CoreBuiltIns.EQUAL);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.NOT_EQUAL);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.LESS_THAN);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.LESS_THAN_OR_EQUAL);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.GREATER_THAN);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.GREATER_THAN_OR_EQUAL);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.MULTIPLY);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DIVIDE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.INTEGER_DIVIDE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.MOD);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.POW);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.UNARY_PLUS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.UNARY_MINUS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ABS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.CEILING);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.FLOOR);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ROUND);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ROUND_HALF_TO_EVEN);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SIN);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.COS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.TAN);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.BOOLEAN_NOT);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.STRING_EQUAL_IGNORE_CASE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.STRING_CONCAT);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBSTRING);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.STRING_LENGTH);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.NORMALIZE_SPACE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.UPPER_CASE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.LOWER_CASE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.TRANSLATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.CONTAINS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.CONTAINS_IGNORE_CASE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.STARTS_WITH);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ENDS_WITH);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBSTRING_BEFORE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBSTRING_AFTER);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.MATCHES);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.REPLACE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.TOKENIZE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.YEAR_MONTH_DURATION);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DAY_TIME_DURATION);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DATETIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.TIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.MULTIPLY_YEAR_MONTH_DURATION);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DIVIDE_YEAR_MONTH_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.MULTIPLY_DAY_TIME_DURATIONS);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.DIVIDE_DAY_TIME_DURATION);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DATES);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_TIMES);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATETIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATETIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_YEAR_MONTH_DURATION_TO_DATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_DATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_DATE);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.ADD_DAY_TIME_DURATION_TO_TIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DAY_TIME_DURATION_FROM_TIME);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION);
        createSWRLBuiltin(SWRLNames.CoreBuiltIns.SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION);
    }
    
    private SWRLBuiltin createSWRLBuiltin(String name) {
    	FrameID id = new FrameID(name);
    	SWRLBuiltin swrlBuiltin = new DefaultSWRLBuiltin(owlModel, id);
    	
    	coreSWRLBuiltIns.add(swrlBuiltin);
    	addFrame(id, swrlBuiltin);
    	
    	return swrlBuiltin;
    }
    
    @Override
    public void addSystemFrames(FrameStore fs) {
        long start = System.currentTimeMillis();
        super.addSystemFrames(fs);
        SWRLSystemFramesAssertions assertions = new SWRLSystemFramesAssertions(fs);
        assertions.addSWRLHierarchy();
        assertions.addPropertyTypes();
        assertions.addBuiltInTypes();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Adding SWRL + OWL system frames took " + (System.currentTimeMillis() - start) + "ms.");
        }
    }
    
    protected class SWRLSystemFramesAssertions extends SystemFramesAsserter {
        
        public SWRLSystemFramesAssertions(FrameStore fs) {
            super(fs);
        }
        
        public void addSWRLHierarchy() {
            fs.addDirectSuperclass(impCls, getRootCls());
            assertTypeAndSubclasses(impCls, getOwlNamedClassClass(), new Cls[] {});
            fs.addDirectSuperclass(variableCls, getRootCls());
            assertTypeAndSubclasses(variableCls, getOwlNamedClassClass(), new Cls[] {});
            fs.addDirectSuperclass(builtInCls, getRootCls());
            assertTypeAndSubclasses(builtInCls, getOwlNamedClassClass(), new Cls[] {}); 
            fs.addDirectSuperclass(atomListCls, getRdfListClass());
            assertTypeAndSubclasses(atomListCls, getOwlNamedClassClass(), new Cls[] {});
            
            fs.addDirectSuperclass(atomCls, getRootCls());
            assertTypeAndSubclasses(atomCls, getOwlNamedClassClass(), new Cls[] {
                assertTypeAndSubclasses(classAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(builtinAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(dataRangeAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(dataValuedPropertyAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(differentIndividualsAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(individualPropertyAtomCls, getOwlNamedClassClass(), new Cls[] {}),
                assertTypeAndSubclasses(sameIndividualAtomCls, getOwlNamedClassClass(), new Cls[] {})
            });
        }

        public void addPropertyTypes() {
            assertTypeAndName(bodyProperty, getOwlObjectPropertyClass());
            assertDomain(bodyProperty);
            
            assertTypeAndName(headProperty, getOwlObjectPropertyClass());
            assertDomain(headProperty);
            
            assertTypeAndName(argumentsProperty, getOwlObjectPropertyClass());
            assertDomain(argumentsProperty);
            
            assertTypeAndName(builtInProperty, getOwlObjectPropertyClass());
            assertDomain(builtInProperty);
            
            assertTypeAndName(argument1Property, getOwlObjectPropertyClass());
            assertDomain(argument1Property);
            
            assertTypeAndName(classPredicateProperty, getOwlObjectPropertyClass());
            assertDomain(classPredicateProperty);
            
            assertTypeAndName(propertyPredicateProperty, getOwlObjectPropertyClass());
            assertDomain(propertyPredicateProperty);
            
            assertTypeAndName(dataRangeProperty, getOwlObjectPropertyClass());
            assertDomain(dataRangeProperty);
            
            assertTypeAndName(argsProperty, getOwlDatatypePropertyClass());
            assertDomain(argsProperty);
            
            assertTypeAndName(minArgsProperty, getOwlDatatypePropertyClass());
            assertDomain(minArgsProperty);
            
            assertTypeAndName(maxArgsProperty, getOwlDatatypePropertyClass());
            assertDomain(maxArgsProperty);
            
            assertTypeAndName(argument2Property, getRdfPropertyClass());
            assertDomain(argument2Property);
        }
        
        public void addBuiltInTypes() {
            for (SWRLBuiltin builtIn : coreSWRLBuiltIns) {
                assertTypeAndName(builtIn, getBuiltInCls());
            }
        }
    }
    
    
    /*
     * ----------------------------------------------------------------------------
     * The obvious getters.
     */
    /**
     * @return the atomListCls
     */
    public OWLNamedClass getAtomListCls() {
        return atomListCls;
    }

    /**
     * @return the builtinAtomCls
     */
    public OWLNamedClass getBuiltinAtomCls() {
        return builtinAtomCls;
    }

    /**
     * @return the classAtomCls
     */
    public OWLNamedClass getClassAtomCls() {
        return classAtomCls;
    }

    /**
     * @return the dataRangeAtomCls
     */
    public OWLNamedClass getDataRangeAtomCls() {
        return dataRangeAtomCls;
    }

    /**
     * @return the dataValuedPropertyAtomCls
     */
    public OWLNamedClass getDataValuedPropertyAtomCls() {
        return dataValuedPropertyAtomCls;
    }

    /**
     * @return the differentIndividualsAtomCls
     */
    public OWLNamedClass getDifferentIndividualsAtomCls() {
        return differentIndividualsAtomCls;
    }

    /**
     * @return the impCls
     */
    public OWLNamedClass getImpCls() {
        return impCls;
    }

    /**
     * @return the individualPropertyAtomCls
     */
    public OWLNamedClass getIndividualPropertyAtomCls() {
        return individualPropertyAtomCls;
    }

    /**
     * @return the sameIndividualAtomCls
     */
    public OWLNamedClass getSameIndividualAtomCls() {
        return sameIndividualAtomCls;
    }

    /**
     * @return the atomCls
     */
    public OWLNamedClass getAtomCls() {
        return atomCls;
    }

    /**
     * @return the variableCls
     */
    public OWLNamedClass getVariableCls() {
        return variableCls;
    }

    /**
     * @return the builtInCls
     */
    public OWLNamedClass getBuiltInCls() {
        return builtInCls;
    }

    /**
     * @return the bodyProperty
     */
    public OWLObjectProperty getBodyProperty() {
        return bodyProperty;
    }

    /**
     * @return the headProperty
     */
    public OWLObjectProperty getHeadProperty() {
        return headProperty;
    }

    /**
     * @return the argumentsProperty
     */
    public OWLObjectProperty getArgumentsProperty() {
        return argumentsProperty;
    }

    /**
     * @return the builtInProperty
     */
    public OWLObjectProperty getBuiltInProperty() {
        return builtInProperty;
    }

    /**
     * @return the argument1Property
     */
    public OWLObjectProperty getArgument1Property() {
        return argument1Property;
    }

    /**
     * @return the classPredicateProperty
     */
    public OWLObjectProperty getClassPredicateProperty() {
        return classPredicateProperty;
    }

    /**
     * @return the propertyPredicateProperty
     */
    public OWLObjectProperty getPropertyPredicateProperty() {
        return propertyPredicateProperty;
    }

    /**
     * @return the dataRangeProperty
     */
    public OWLObjectProperty getDataRangeProperty() {
        return dataRangeProperty;
    }

    /**
     * @return the argsProperty
     */
    public OWLDatatypeProperty getArgsProperty() {
        return argsProperty;
    }

    /**
     * @return the minArgsProperty
     */
    public OWLDatatypeProperty getMinArgsProperty() {
        return minArgsProperty;
    }

    /**
     * @return the maxArgsProperty
     */
    public OWLDatatypeProperty getMaxArgsProperty() {
        return maxArgsProperty;
    }

    /**
     * @return the coreSWRLBuiltIns
     */
    public List<SWRLBuiltin> getCoreSWRLBuiltIns() {
        return coreSWRLBuiltIns;
    }

    /**
     * @return the argument2Property
     */
    public RDFProperty getArgument2Property() {
        return argument2Property;
    }
    
    
    

}
