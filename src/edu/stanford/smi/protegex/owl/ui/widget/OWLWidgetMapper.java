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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLWidgetMapper extends DefaultWidgetMapper {
  private static Logger log = Log.getLogger(OWLWidgetMapper.class);
  
    private static Map className2Metadata;


    private final static String[] incompatibleWidgetClassNames = {
            CheckBoxWidget.class.getName(),
            DirectInstancesWidget.class.getName(),
            FloatFieldWidget.class.getName(),
            FloatListWidget.class.getName(),
            InstanceFieldWidget.class.getName(),
            InstanceListWidget.class.getName(),
            IntegerFieldWidget.class.getName(),
            IntegerListWidget.class.getName(),
            ResourceListWidget.class.getName(),
            StringListWidget.class.getName(),
            TextAreaWidget.class.getName(),
            TextFieldWidget.class.getName(),
            UniqueStringWidget.class.getName(),
            YellowStickyWidget.class.getName(),
            "edu.stanford.smi.protegex.widget.contains.ContainsWidget",
            "edu.stanford.smi.protegex.widget.instancetable.InstanceRowWidget"
    };


    public OWLWidgetMapper(OWLModel owlModel) {
        super(owlModel.getProject().getInternalProjectKnowledgeBase());
    }


    public String getDefaultWidgetClassName(Cls cls, Slot slot, Facet facet) {
        return getDefaultWidgetClassName(cls, slot, facet, false);
    }


    public String getDefaultWidgetClassName(Cls cls, Slot slot, Facet facet, boolean forceNotNull) {
        if (slot instanceof RDFProperty && cls instanceof RDFSNamedClass) {

            if (!cls.hasTemplateSlot(slot)) {
                return null;
            }

            RDFSNamedClass namedClass = (RDFSNamedClass) cls;
            RDFProperty rdfProperty = (RDFProperty) slot;

            if (rdfProperty.isAnnotationProperty()) {
                return null;
            }
            else if (!rdfProperty.isDomainDefined(true)) {
                if (!OWLWidgetUtil.isRestrictedProperty(namedClass, rdfProperty) && !forceNotNull) {
                    return null;
                }
            }

            String defaultClassName = PluginUtilities.getDefaultWidgetClassName(rdfProperty);
            if (defaultClassName != null) {
                return defaultClassName;
            }

            // Suppress properties with maximum cardinality of 0
            if (namedClass instanceof OWLNamedClass) {
                OWLNamedClass owlNamedClass = (OWLNamedClass) namedClass;
                Collection ps = new ArrayList();
                ps.add(rdfProperty);
                ps.addAll(rdfProperty.getSuperproperties(true));
                for (Iterator it = ps.iterator(); it.hasNext();) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (owlNamedClass.getMaxCardinality(property) == 0) {
                        return null;
                    }
                }
            }

            initWidgetMetadata();
            int bestSuitability = OWLWidgetMetadata.NOT_SUITABLE;
            String bestClassName = null;
            for (Iterator it = className2Metadata.keySet().iterator(); it.hasNext();) {
                String className = (String) it.next();
                OWLWidgetMetadata metadata = (OWLWidgetMetadata) className2Metadata.get(className);
                if (metadata != null) {
                    int suitability = metadata.getSuitability(namedClass, rdfProperty);
                    if (suitability > bestSuitability) {
                        bestClassName = className;
                        bestSuitability = suitability;
                    }
                }
            }
            if (bestClassName != null && bestSuitability > OWLWidgetMetadata.SUITABLE) {
                return bestClassName;
            }

            Class[] widgetClasses = new Class[]{
                    DataRangeFieldWidget.class,
                    OWLDateWidget.class,
                    OWLTimeWidget.class,
                    OWLDateTimeWidget.class,
                    RDFListWidget.class,
                    MultiResourceWidget.class,
                    SingleResourceWidget.class,
                    BooleanListWidget.class,
                    MultiLiteralWidget.class,
                    SingleLiteralWidget.class
            };
            Object[] args = new Object[]{
                    cls, slot, facet
            };
            for (int i = 0; i < widgetClasses.length; i++) {
                Class widgetClass = widgetClasses[i];
                if (isSuitable(widgetClass, cls, slot)) {
                    return widgetClass.getName();
                }
                try {
                    Method method = widgetClass.getMethod("isSuitable", new Class[]{
                            Cls.class, Slot.class, Facet.class
                    });
                    if (Boolean.TRUE.equals(method.invoke(null, args))) {
                        return widgetClass.getName();
                    }
                }
                catch (Exception ex) {
                  // Empty catch blocks are really dangerous!
                  Log.emptyCatchBlock(ex);
                }
            }
        }
        return super.getDefaultWidgetClassName(cls, slot, facet);
    }


    public static OWLWidgetMetadata getOWLWidgetMetadata(String className) {
        initWidgetMetadata();
        return (OWLWidgetMetadata) className2Metadata.get(className);
    }


    public Collection getSuitableWidgetClassNames(Cls cls, Slot slot, Facet facet) {
        Collection ss = new ArrayList(super.getSuitableWidgetClassNames(cls, slot, facet));
        for (int i = 0; i < incompatibleWidgetClassNames.length; i++) {
            String className = incompatibleWidgetClassNames[i];
            ss.remove(className);
        }
        return ss;
    }


    private static void initWidgetMetadata() {
        if (className2Metadata == null) {
            className2Metadata = new HashMap();
            Collection classNames = PluginUtilities.getAvailableSlotWidgetClassNames();
            for (Iterator it = classNames.iterator(); it.hasNext();) {
                String clsName = (String) it.next();
                String metadataClassName = clsName + "Metadata";
                try {
                    Class metadataClass = PluginUtilities.forName(metadataClassName, true);
                    if (metadataClass != null) {
                        Object metadata = metadataClass.newInstance();
                        className2Metadata.put(clsName, metadata);
                    }
                }
                catch (Exception ex) {
                  Log.emptyCatchBlock(ex);
                }
            }
        }
    }


    public static boolean isIncompatibleWidgetName(String className) {
        for (int i = 0; i < incompatibleWidgetClassNames.length; i++) {
            String incompatiblewidgetclassname = incompatibleWidgetClassNames[i];
            if (className.equals(incompatiblewidgetclassname)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSuitable(Class clazz, Cls cls, Slot slot) {
        if (slot instanceof RDFProperty && cls instanceof RDFSNamedClass) {
            RDFProperty property = (RDFProperty) slot;
            String className = clazz.getName();
            OWLWidgetMetadata metadata = OWLWidgetMapper.getOWLWidgetMetadata(className);
            if (metadata != null) {
                RDFSNamedClass namedClass = (RDFSNamedClass) cls;
                return metadata.getSuitability(namedClass, property) > OWLWidgetMetadata.NOT_SUITABLE;
            }
        }
        return false;
    }
}
