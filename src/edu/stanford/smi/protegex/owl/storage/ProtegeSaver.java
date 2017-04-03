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

package edu.stanford.smi.protegex.owl.storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.ProtegeWriterSettings;

/**
 * This class can be used to convert a legacy Protege ontology (in e.g. CLIPS format) into
 * an OWLModel that can be saved into a file.  The basic idea is to copy the contents of
 * the existing ontology into an OWLModel that is automatically mapped into a set of
 * OWL objects (like done in the JenaOWLModel).  Thus, the specific logic that generates
 * the corresponding OWL objects can be reused and no additiona implementation of a Protege-to-OWL
 * mapping is required.  During that conversation all normal classes (:STANDARD-CLASS) are
 * made named OWL classes (:OWL-NAMED-CLASS) and thus "shifted up" one level in the metamodel. <BR>
 * <p/>
 * This class is supported by the RestrictionUpdater class, which generates Restrictions from
 * facet overrides while the new OWLModel is populated.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeSaver extends KnowledgeBaseCopier {
    public static transient Logger log = Log.getLogger(ProtegeSaver.class);

    private OWLModel owlModel;


    public ProtegeSaver(KnowledgeBase source, OWLModel target) {
    	this(source, target, false);
    }


    public ProtegeSaver(KnowledgeBase source, OWLModel target, boolean useNativeWriter) {
        super(source, target);
        this.owlModel = target;
        
        if (useNativeWriter) {
        	(owlModel).setWriterSettings(new ProtegeWriterSettings(owlModel));
        }
	}


	protected void addExtraDirectTypes(Instance oldInstance, Instance newInstance) {
        super.addExtraDirectTypes(oldInstance, newInstance);
        if (oldInstance instanceof Slot &&
                !((Slot) oldInstance).getAllowsMultipleValues() &&
                newInstance instanceof RDFProperty) {
            ((RDFProperty) newInstance).setFunctional(true);
        }
    }


    protected Collection cloneValues(Collection oldValues) {
        Collection newValues = super.cloneValues(oldValues);
        return new HashSet(newValues);  // Prevent duplicates
    }


    protected Cls createCls(String clsName, Cls metaCls) {
        String validName = getValidName(clsName);
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Creating named class " + validName + " for " + clsName + " with type " + metaCls.getName());
        }

        OWLNamedClass cls = owlModel.createOWLNamedClass(validName, (OWLNamedClass) metaCls);
        String realName = cls.getName();
        if (!realName.equals(clsName)) {
            cls.addLabel(clsName, null);
        }
        return cls;
    }


    protected Instance createInstance(String name, Cls newType) {
        String validName = getValidName(name);
        Instance instance = super.createInstance(validName, newType);
        if (instance instanceof RDFIndividual) {
            RDFIndividual soi = (RDFIndividual) instance;
            String realName = soi.getName();
            if (!realName.equals(name)) {
                soi.addLabel(name, null);
            }
        }
        return instance;
    }


    protected Slot createSlot(String slotName, ValueType valueType) {
        String validName = getValidName(slotName);
        if (log.isLoggable(Level.FINE)) {
            log.fine("+ Creating slot " + validName);
        }

        RDFProperty property = null;
        if (valueType == ValueType.INSTANCE || valueType == ValueType.CLS) {
            property = owlModel.createOWLObjectProperty(validName);
        }
        else if (valueType == ValueType.ANY) {
            property = owlModel.createRDFProperty(validName);
        }
        else {
            property = owlModel.createOWLDatatypeProperty(validName);
        }
        property.removeUnionDomainClass(owlModel.getOWLThingClass());
        String realName = property.getName();
        if (!realName.equals(slotName)) {
            property.addLabel(slotName, null);
        }
        return property;
    }


    protected Cls getNewCls(Cls oldCls) {
        if (oldCls.getName().equals(Model.Cls.STANDARD_CLASS)) {
            return owlModel.getOWLNamedClassClass();
        }
        else if (oldCls.getName().equals(Model.Cls.STANDARD_SLOT)) {
            return owlModel.getRDFPropertyClass();
        }
        else if (oldCls.getName().equals(Model.Cls.THING)) {
            return owlModel.getOWLThingClass();
        }
        else {
            return super.getNewCls(oldCls);
        }
    }


    protected Slot getNewSlot(Slot oldSlot) {
        if (oldSlot.getName().equals(Model.Slot.DOCUMENTATION)) {
            return owlModel.getRDFSCommentProperty();
        }
        else if (oldSlot.getName().equals(Model.Slot.DIRECT_SUPERSLOTS)) {
            return owlModel.getRDFSSubPropertyOfProperty();
        }
        else if (oldSlot.getName().equals(Model.Slot.INVERSE)) {
            return owlModel.getRDFProperty(OWLNames.Slot.INVERSE_OF);
        }
        else {
            return super.getNewSlot(oldSlot);
        }
    }


    private String getValidName(String clsName) {
        clsName = clsName.replace(':', '_');
        String suggested = AbstractOWLModel.getValidOWLFrameName((AbstractOWLModel) owlModel, clsName);
        for (int i = 1; ((KnowledgeBase) owlModel).getFrame(suggested) != null; i++) {
            suggested = AbstractOWLModel.getValidOWLFrameName((AbstractOWLModel) owlModel, clsName + i);
        }
        return suggested;
    }


    protected void setDirectType(Instance oldFrame, Instance newFrame) {
        if (oldFrame instanceof Slot && newFrame instanceof OWLProperty) {
            return;  // Catch illegal conversions
        }
        else {
            super.setDirectType(oldFrame, newFrame);
        }
    }


    protected void setInitialOwnSlotValues(Instance instance) {
        super.setInitialOwnSlotValues(instance);
        if (instance instanceof Slot) {
            Slot oldSlot = (Slot) instance;
            Slot oldInverseSlot = oldSlot.getInverseSlot();
            Slot newSlot = (Slot) getNewInstance(oldSlot);
            if (oldInverseSlot != null && newSlot != null) {
                Slot newInverseSlot = (Slot) getNewInstance(oldInverseSlot);
                newSlot.setInverseSlot(newInverseSlot);
            }
        }
    }


    protected void setValueType(Slot oldSlot, Slot newSlot) {
        final ValueType valueType = oldSlot.getValueType();
        if (valueType != ValueType.ANY) {
            if (newSlot instanceof RDFProperty &&
                    (valueType == ValueType.BOOLEAN ||
                            valueType == ValueType.FLOAT ||
                            valueType == ValueType.INTEGER ||
                            valueType == ValueType.STRING)) {
                String uri = XMLSchemaDatatypes.getDefaultXSDDatatype(valueType).getURI();
                RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(uri);
                ((RDFProperty) newSlot).setRange(datatype);
            }
           
           //treat rdf:range for owl object properties
           if (newSlot instanceof OWLObjectProperty && valueType == ValueType.INSTANCE) {
        	   Collection newAllowedClses = cloneValues(oldSlot.getAllowedClses());
               ((OWLObjectProperty)newSlot).setRanges(newAllowedClses);        
           } else {
        	   super.setValueType(oldSlot, newSlot);
           }
        }
        else {
            System.err.println("[ProtegeSaver] Warning: Slot " + oldSlot + " has value type ANY");
        }
    }
  
    
}
