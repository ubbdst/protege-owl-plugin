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

package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The OWL browser slot pattern used to display the name of OWL elements. It treats the RDFSLiterals
 * in a special manner based on the default language.
 *
 * @author Tania
 */
public class OWLBrowserSlotPattern extends BrowserSlotPattern {
    private boolean slotHasSingleValue = false;

    public OWLBrowserSlotPattern(BrowserSlotPattern pattern) {
        super(pattern.getElements());
    }

    public OWLBrowserSlotPattern(List elements) {
        super(elements);
    }

    public OWLBrowserSlotPattern(Slot slot) {
        super(slot);
    }


    @Override
    public String getBrowserText(Instance instance) {
        KnowledgeBase kb = instance.getKnowledgeBase();
        StringBuffer buffer = new StringBuffer();

        for (Object o : getElements()) {
            if (kb instanceof OWLModel && o.equals(kb.getSystemFrames().getNameSlot())) {
                buffer.append(NamespaceUtil.getPrefixedName((OWLModel) kb, instance.getName()));
            } else if (o instanceof Slot) {
                buffer.append(getText((Slot) o, instance));
            } else {
                buffer.append(o);
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    private String getText(Slot slot, Instance instance) {
        String text = null;
        String defaultLang = null;
        Collection values = null;

        if (slot instanceof RDFProperty) {
            values = ((RDFResource) instance).getPropertyValues((RDFProperty) slot);
            defaultLang = getDefaultLanguage(instance.getKnowledgeBase());
        } else {
            values = instance.getDirectOwnSlotValues(slot);
        }

        if (values.size() > 1) { // multiple values
            slotHasSingleValue = false;
            // TODO: find a more efficient implementation of this!!
            Collection rdfLabelsWithNullLang = new ArrayList();
            Collection rdfLabelsWithNonNullLang = new ArrayList();

            StringBuffer buffer = new StringBuffer();
            int valuesNo = 0;

            if (defaultLang == null || slot.getValueType() != ValueType.STRING) { //no default language
                valuesNo = getBrowserTextFromValues(instance, values, null, buffer);
            } else {//default language set

                for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                    Object o = iter.next();
                    if (o instanceof RDFSLiteral && ((RDFSLiteral) o).getLanguage() == null || o instanceof String) {
                        rdfLabelsWithNullLang.add(o);
                    } else {
                        rdfLabelsWithNonNullLang.add(o);
                    }
                }

                valuesNo = getBrowserTextFromValues(instance, rdfLabelsWithNonNullLang, defaultLang, buffer);

                if (valuesNo == 0) {
                    valuesNo = getBrowserTextFromValues(instance, rdfLabelsWithNullLang, defaultLang, buffer);
                }
            }

            if (valuesNo > 1) {
                buffer.insert(0, "[");
                buffer.insert(buffer.length(), "]");
            }

            if (valuesNo > 0) {
                text = buffer.toString();
            } else {
                text = NamespaceUtil.getPrefixedName((OWLModel) instance.getKnowledgeBase(), instance.getName());
            }
        } else { // single value
            slotHasSingleValue = true;
            Object o = CollectionUtilities.getFirstItem(values);
            text = getText(o, instance, defaultLang);
            if (text == null) {
                //text = instance.getName();
                text = "";
            }
        }

        return text;
    }

    private int getBrowserTextFromValues(Instance instance, Collection values, String lang, StringBuffer buffer) {
        boolean isFirst = true;
        int valuesNo = 0;

        for (Object o : values) {
            String partialText = getText(o, instance, lang);
            if (partialText != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buffer.append(", ");
                }
                buffer.append(partialText);
                valuesNo++;
            }
        }
        return valuesNo;
    }

    private String getText(Object o, Instance instance, String lang) {
        String text;
        if (o == null) {
            text = "";
        } else if (o instanceof Frame) {
            if (o.equals(instance)) {
                text = "<recursive call>";
            } else {
                text = ((Frame) o).getBrowserText();
            }
        } else if (o instanceof RDFSLiteral) {
            //text = getLangBrowserText(o, lang);
            text = getBrowserText(o, instance, lang);
        } else {
            text = o.toString();
            text = ParserUtils.quoteIfNeeded(text);
        }
        return text;
    }


    private String getDefaultLanguage(KnowledgeBase kb) {
        if ( !(kb instanceof OWLModel)) {
            return null;
        }
        return ((OWLModel) kb).getDefaultLanguage();
    }


    /**
     * Original method from Tania
     */
    @Deprecated
    private String getLangBrowserText(Object value, String defaultLanguage) {
        if (!(value instanceof RDFSLiteral)) {
            return null;
        }
        RDFSLiteral rdfsValue = (RDFSLiteral) value;

        if (defaultLanguage == null) {
            if (rdfsValue.getLanguage() == null) {
                String text = rdfsValue.getString();
                text = ParserUtils.quoteIfNeeded(text);
                return text;
            }
        } else { //default language is not null
            String lang = rdfsValue.getLanguage();
            if (lang != null && lang.equals(defaultLanguage)) {
                String text = ((RDFSLiteral) value).getString();
                text = ParserUtils.quoteIfNeeded(text);
                return text;
            }
        }
        return null;
    }

    /**
     * Gets browser text and treat RDFIndividual with only one property value differently
     */
    private String getBrowserText(Object value, Instance instance, String defaultLanguage) {
        if (!(value instanceof RDFSLiteral)) {
            return null;
        }

        RDFSLiteral rdfsLiteral = (RDFSLiteral) value;

        // SPECIAL CASE FOR INDIVIDUALS
        // If instance is of type individual and slot has only one value, then do not care about the language,
        // just return the value
        // This came as a request from UBB, such that, slot values can be seen and searched
        // within the Protege browser (since search depends on browser texts).
        // Hemed, 23.05.2018
        if (instance instanceof RDFIndividual && slotHasSingleValue) {
            return ParserUtils.quoteIfNeeded(rdfsLiteral.getString());
        }
        // Otherwise, decide display value based on literal language and default language
        return getLangBrowserText(rdfsLiteral, defaultLanguage);
    }

    /**
     * Gets browser text based on the default language specified
     */
    private String getLangBrowserText(RDFSLiteral literal, String defaultLanguage) {
        if (defaultLanguage == null) {// no default language
            if (literal.getLanguage() == null) {
                return ParserUtils.quoteIfNeeded(literal.getString());
            }
        } else { //default language exists
            String lang = literal.getLanguage();
            if (lang != null && lang.equals(defaultLanguage)) {
                return ParserUtils.quoteIfNeeded(literal.getString());
            }
        }

        //Deal with priorities (not working as expected)
       /*if(hasPriorityLang(literal)) {
            return ParserUtils.quoteIfNeeded(getTextForPriorityLang(literal));
        }*/
        return null;
    }

    @Override
    public String toString() {
        return "OWLBrowserSlotPattern(" + getSerialization() + ")";
    }

    //TODO:  Hemed, 23.05.2018
    // If only one value exists, take it regardless of language
    // If default lang is set, and we have more than one values, take the one with default lang
    // If default lang not set, an we have more than two values, do language priority.
    // ( or just take the random 2 values)


    /**
     * Checks if this literal contains priority language
     */
    private boolean hasPriorityLang(RDFSLiteral literal) {
        for (String lang : AbstractOWLModel.DEFAULT_USED_LANGUAGES) {
            if (lang.equals(literal.getLanguage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets text for a literal which has priority language
     */
    private String getTextForPriorityLang(RDFSLiteral literal) {
        for (String lang : AbstractOWLModel.DEFAULT_USED_LANGUAGES) {
            if (lang.equals(literal.getLanguage())) {
                return literal.getString();
            }
        }
        return null;
    }
}