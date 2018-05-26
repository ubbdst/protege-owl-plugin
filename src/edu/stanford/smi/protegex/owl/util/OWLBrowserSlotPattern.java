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
import java.util.List;

/**
 * The OWL browser slot pattern used to display the name of OWL elements. It treats the RDFSLiterals
 * in a special manner based on the default language.
 * <p>
 * Additional logic from UBB:
 * <p>
 * - If only one slot value exists, take it regardless of language
 * - If default lang is set, and there are more than one values, take the one with default lang (work out of box)
 * - If default lang not set, and we have more one values, do language priority.
 *
 * @author Tania
 * @author Hemed Ali
 */
public class OWLBrowserSlotPattern extends BrowserSlotPattern {
    /**
     * Flag to check whether a property has single value
     */
    private boolean hasSingleValue;

    /**
     * List of values when no default language set
     */
    private List<RDFSLiteral> otherValues;

    public OWLBrowserSlotPattern(BrowserSlotPattern pattern) {
        super(pattern.getElements());
    }

    public OWLBrowserSlotPattern(List elements) {
        super(elements);
    }

    public OWLBrowserSlotPattern(Slot slot) {
        super(slot);
    }

    /**
     * Inserts first and last bracket for the given String buffer object
     */
    private static void insertBrackets(StringBuffer buffer) {
        buffer.insert(0, "[");
        buffer.insert(buffer.length(), "]");
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

    /**
     * Gets browser text for the given slot of the given instance
     *
     * @param slot     a slot where we need to get the browser text
     * @param instance an instance where that slot belongs to
     * @return a browser text
     */
    private String getText(Slot slot, Instance instance) {
        String text;
        Collection values;
        String defaultLang = null;

        if (slot instanceof RDFProperty) {
            values = ((RDFResource) instance).getPropertyValues((RDFProperty) slot);
            defaultLang = getDefaultLanguage(instance.getKnowledgeBase());
        } else {
            values = instance.getDirectOwnSlotValues(slot);
        }

        if (values.size() > 1) { // multiple values
            hasSingleValue = false;
            // TODO: find a more efficient implementation of this!!
            Collection rdfLabelsWithNullLang = new ArrayList();
            Collection rdfLabelsWithNonNullLang = new ArrayList();
            otherValues = new ArrayList<RDFSLiteral>();

            StringBuffer buffer = new StringBuffer();
            int valuesNo = 0;

            if (defaultLang == null || slot.getValueType() != ValueType.STRING) { //no default language
                valuesNo = getBrowserTextFromValues(instance, values, null, buffer);
            } else {//default language set

                for (Object o : values) {
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
                insertBrackets(buffer);
            }

            if (valuesNo > 0) {
                text = buffer.toString();
            } else if (otherValues.size() > 0) { //do language priority thing
                text = getBrowserTextForPriorityLang(otherValues);
                if (text.isEmpty()) {
                    //If no priority list, return 2 values
                    text = getBrowserTextFromLiterals(otherValues, 2);
                }
            } else {
                //Fallback is the instance URI prefix
                text = NamespaceUtil.getPrefixedName((OWLModel) instance.getKnowledgeBase(), instance.getName());
            }
        } else { // single value
            hasSingleValue = true;
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
            text = ParserUtils.quoteIfNeeded(o.toString());
        }
        return text;
    }

    private String getDefaultLanguage(KnowledgeBase kb) {
        if (!(kb instanceof OWLModel)) {
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
     * If property has only one value, return that value regardless of the language
     */
    private String getBrowserText(Object value, Instance instance, String defaultLanguage) {
        if (!(value instanceof RDFSLiteral)) {
            return null;
        }

        RDFSLiteral rdfsLiteral = (RDFSLiteral) value;

        // SPECIAL CASE FOR SINGLE VALUE PROPERTIES
        // If slot has only one value, then do not care about the language used, just return that value
        // This came as a request from UBB, such that, slot values can be seen and searched
        // within the Protege browser (since search depends on browser texts).
        // Hemed, 23.05.2018
        if (hasSingleValue) {
            return ParserUtils.quoteIfNeeded(rdfsLiteral.getString());
        }

        //Get display value based on language
        String displayText = getLangBrowserText(rdfsLiteral, defaultLanguage);

        // SPECIAL CASE FOR INDIVIDUALS
        // This stage will be reached when literal contains non-null language and the language
        // is not the same as the default language
        if (displayText == null) {
            if (instance instanceof RDFIndividual) {
                otherValues.add(rdfsLiteral);
            }
        }
        return displayText;
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
        return null;
    }

    @Override
    public String toString() {
        return "OWLBrowserSlotPattern(" + getSerialization() + ")";
    }

    /**
     * Gets text for literals that contain priority language
     *
     * @param literals a list of literals
     * @return a browser text if one or more literals contain priority language
     */
    private String getBrowserTextForPriorityLang(List<RDFSLiteral> literals) {
        List<RDFSLiteral> matches = new ArrayList<RDFSLiteral>();
        boolean isMatchFound = false;
        for (String lang : AbstractOWLModel.DEFAULT_USED_LANGUAGES) {
            for (RDFSLiteral literal : literals) {
                if (literal.getLanguage().equalsIgnoreCase(lang)) {
                    matches.add(literal);
                    isMatchFound = true;
                }
            }
            if (isMatchFound) {
                break;
            }
        }
        if (matches.size() > 0) {
            return getBrowserTextFromLiterals(matches, matches.size());
        }
        return "";
    }

    /**
     * Constructs the browser texts from the list of literals
     *
     * @param literals list of literals
     * @param limit    maximum number of texts to be returned
     * @return a string buffer where string literal is appended
     */
    private String getBrowserTextFromLiterals(List<RDFSLiteral> literals, int limit) {
        StringBuffer buffer = new StringBuffer();
        boolean isFirst = true;
        int size = literals.size();

        for (int i = 0; i < size && i < limit; i++) {
            String partialText = ParserUtils.quoteIfNeeded(literals.get(i).getString());
            if (partialText != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buffer.append(", ");
                }
                buffer.append(partialText);
            }
        }
        if (literals.size() > 1) {
            if (limit < size) {
                buffer.append(", ...");
            }
            insertBrackets(buffer);
        }
        return buffer.toString();
    }


}