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

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFSDatatype extends DefaultRDFIndividual implements RDFSDatatype {


    public DefaultRDFSDatatype(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultRDFSDatatype() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitRDFDatatype(this);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) object;
            return datatype.getURI() == getURI() &&
                   datatype.getMaxExclusive().equalsStructurally(getMaxExclusive()) &&
                   datatype.getMinExclusive().equalsStructurally(getMinExclusive()) &&
                   datatype.getMaxInclusive().equalsStructurally(getMaxInclusive()) &&
                   datatype.getMinInclusive().equalsStructurally(getMinInclusive());
        }
        return false;
    }


    public RDFSDatatype getBaseDatatype() {
        RDFProperty property = XSPNames.getRDFProperty(getOWLModel(), XSPNames.XSP_BASE);
        if (property != null) {
            return (RDFSDatatype) getPropertyValue(property);
        }
        else {
            return null; // getBaseDatatypeXSD();
        }
    }


    public String getBrowserText() {
        if (isAnonymous()) {
            RDFSDatatype baseDatatype = getBaseDatatype();
            if (baseDatatype != null) {
                String str = baseDatatype.getBrowserText();
                String openingBracket = "[";
                String closingBracket = "]";
                RDFSLiteral min = getMinInclusive();
                RDFSLiteral max = getMaxInclusive();
                if (min == null) {
                    min = getMinExclusive();
                    if (min != null) {
                        openingBracket = "(";
                        if (max == null) {
                            closingBracket = ")";
                        }
                    }
                }
                if (max == null) {
                    max = getMaxExclusive();
                    if (max != null) {
                        closingBracket = ")";
                    }
                }
                if (max != null || min != null) {
                    str += openingBracket;
                    if (min != null) {
                        str += min.toString();
                    }
                    else {
                        str += "..";
                    }
                    str += ",";
                    if (max != null) {
                        str += max.toString();
                    }
                    else {
                        str += "..";
                    }
                    return str + closingBracket;
                }
                else {
                    return "variant of " + baseDatatype.getBrowserText();
                }
            }
        }
        String bt = super.getBrowserText();
        if (bt.startsWith(XSDNames.PREFIX)) {
            return bt.substring(XSDNames.PREFIX.length());
        }
        else {
            return bt;
        }
    }


    public Object getDefaultValue() {
        if (equals(getOWLModel().getXSDboolean())) {
            return Boolean.FALSE;
        }
        else if (equals(getOWLModel().getXSDint())) {
            return new Integer(0);
        }
        else if (equals(getOWLModel().getXSDfloat())) {
            return new Float(0);
        }
        else if (equals(getOWLModel().getXSDstring())) {
            return "";
        }
        else {
            String literal = "";
            if (isNumericDatatype()) {
                literal = "0";
            }
            return getOWLModel().createRDFSLiteral(literal, this);
        }
    }


    public Icon getIcon() {
        return isEditable() ?
                OWLIcons.getImageIcon(OWLIcons.RDF_DATATYPE) :
                OWLIcons.getReadOnlyIndividualIcon(OWLIcons.getImageIcon(OWLIcons.RDF_DATATYPE));
    }


    private int getIntPropertyValue(String propertyName) {
        RDFProperty property = getOWLModel().getRDFProperty(propertyName);
        if (property != null) {
            Object value = getPropertyValue(property);
            if (value instanceof Integer) {
                return ((Integer) value).intValue();
            }
        }
        return -1;
    }


    public int getLength() {
        return getIntPropertyValue(XSPNames.getName(getOWLModel(), XSPNames.XSP_LENGTH));
    }


    public RDFSLiteral getMaxExclusive() {
        return getPropertyValueLiteral(XSPNames.getName(getOWLModel(), XSPNames.XSP_MAX_EXCLUSIVE));
    }


    public RDFSLiteral getMaxInclusive() {
        return getPropertyValueLiteral(XSPNames.getName(getOWLModel(), XSPNames.XSP_MAX_INCLUSIVE));
    }


    public int getMaxLength() {
        return getIntPropertyValue(XSPNames.getName(getOWLModel(), XSPNames.XSP_MAX_LENGTH));
    }


    public RDFSLiteral getMinExclusive() {
        return getPropertyValueLiteral(XSPNames.getName(getOWLModel(), XSPNames.XSP_MIN_EXCLUSIVE));
    }


    public RDFSLiteral getMinInclusive() {
        return getPropertyValueLiteral(XSPNames.getName(getOWLModel(), XSPNames.XSP_MIN_INCLUSIVE));
    }


    public int getMinLength() {
        return getIntPropertyValue(XSPNames.getName(getOWLModel(), XSPNames.XSP_MIN_LENGTH));
    }


    public String getPattern() {
        RDFProperty property = XSPNames.getRDFProperty(getOWLModel(), XSPNames.XSP_PATTERN);
        if (property != null) {
            Object value = getPropertyValue(property);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }


    private RDFSLiteral getPropertyValueLiteral(String propertyName) {
        RDFProperty property = getOWLModel().getRDFProperty(propertyName);
        if (property != null) {
            return getPropertyValueLiteral(property);
        }
        else {
            return null;
        }
    }

//    private RDFProperty getSimpleTypeLiteralProperty() {
//        return getOWLModel().getRDFSLabelProperty(); //
//    }

//    private XSSimpleType getXSSimpleType() {
//        RDFProperty stlProperty = getSimpleTypeLiteralProperty();
//        Object value = getPropertyValue(stlProperty);
//        if (value != null) {
//            String str = null;
//            if (value instanceof RDFSLiteral) {
//                str = ((RDFSLiteral) value).getString();
//            }
//            else {
//                str = value.toString();
//            }
//            str = "<?xml version='1.0'?>\n" +
//                    "<xs:schema " +
//                    "           xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
//                    "           xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
//                    str +
//                    "</xs:schema>";
//            XMLGrammarPreparser parser = new XMLGrammarPreparser();
//            parser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
//            try {
//                String uri = "http://dummy." + Math.random();
//                XMLInputSource source = new XMLInputSource(null, uri, uri, new StringReader(str), null);
//                XSGrammar xsg = (XSGrammar) parser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, source);
//                org.apache.xerces.xs.XSModel xsm = xsg.toXSModel();
//                XSNamedMap map = xsm.getComponents(XSTypeDefinition.SIMPLE_TYPE);
//                int numDefs = map.getLength();
//                if (numDefs > 0) {
//                    XSSimpleType xstype = (XSSimpleType) map.item(0);
//                    return xstype;
//                }
//            }
//            catch (Exception ex) {
//            }
//        }
//        return null;
//    }


    public boolean isNumericDatatype() {
        String uri = getURI();
        RDFSDatatype base = getBaseDatatype();
        if (base != null) {
            uri = base.getURI();
        }
        return XMLSchemaDatatypes.isNumericDatatype(uri);
    }


    public boolean isValidValue(RDFSLiteral object) {
        RDFSDatatype d = this;
        RDFSDatatype baseDatatype = getBaseDatatype();
        if (baseDatatype != null) {
            d = baseDatatype;
        }
        if (!object.getDatatype().equals(d)) {
            return false;
        }
        if (getOWLModel().getXSDstring().equals(d)) {
            return isValidValueString(object);
        }
        else {
            return isValidValueNumeric(object);
        }
    }


    private boolean isValidValueNumeric(RDFSLiteral object) {
        RDFSLiteral minInclusive = getMinInclusive();
        if (minInclusive != null) {
            if (object.compareTo(minInclusive) < 0) {
                return false;
            }
        }
        RDFSLiteral minExclusive = getMinExclusive();
        if (minExclusive != null) {
            if (object.compareTo(minExclusive) <= 0) {
                return false;
            }
        }
        RDFSLiteral maxInclusive = getMaxInclusive();
        if (maxInclusive != null) {
            if (object.compareTo(maxInclusive) > 0) {
                return false;
            }
        }
        RDFSLiteral maxExclusive = getMaxExclusive();
        if (maxExclusive != null) {
            if (object.compareTo(maxExclusive) >= 0) {
                return false;
            }
        }
        return true;
    }


    private boolean isValidValueString(RDFSLiteral object) {
        String s = object.toString();
        int l = s.length();

        int length = getLength();
        if (length >= 0 && length != l) {
            return false;
        }
        int minLength = getMinLength();
        if (minLength >= 0 && l < minLength) {
            return false;
        }
        int maxLength = getMaxLength();
        if (maxLength >= 0 && l > maxLength) {
            return false;
        }
        String pattern = getPattern();
        if (pattern != null) {
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(s);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }


    public static Map parse(OWLModel owlModel, String expression) {
        Map map = new HashMap();
        if (owlModel.isProtegeMetaOntologyImported()) {
            int index = 3;
            while (index < expression.length() &&
                   expression.charAt(index) != '(' &&
                   expression.charAt(index) != '[') {
                index++;
            }
            if (index < expression.length()) {
                String baseTypeName = expression.substring(0, index).trim();
                RDFSDatatype datatype = owlModel.getRDFSDatatypeByName(baseTypeName);
                if (datatype != null) {
                	RDFProperty subDatatypeOfProperty = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_BASE);
                    map.put(subDatatypeOfProperty, datatype);
                    RDFProperty minProperty = null;
                    if (expression.charAt(index) == '(') {
                        minProperty = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MIN_EXCLUSIVE);
                    }
                    else {
                        minProperty = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MIN_INCLUSIVE);
                    }
                    String rest = expression.substring(index + 1).trim();
                    int minEnd = 0;
                    while (minEnd < rest.length() &&
                           (Character.isDigit(rest.charAt(minEnd)) || rest.charAt(minEnd) == '.')) {
                        minEnd++;
                    }
                    String min = rest.substring(0, minEnd);
                    if (min.length() > 0 && Character.isDigit(min.charAt(0))) {
                        RDFSLiteral minLiteral = owlModel.createRDFSLiteral(min, datatype);
                        map.put(minProperty, minLiteral);
                    }
                    int maxStart = minEnd;
                    while (maxStart < rest.length() && !Character.isDigit(rest.charAt(maxStart))) {
                        maxStart++;
                    }
                    if (maxStart < rest.length()) {
                        rest = rest.substring(maxStart);
                        int maxEnd = 0;
                        while (maxEnd < rest.length() &&
                               (Character.isDigit(rest.charAt(maxEnd)) || rest.charAt(maxEnd) == '.')) {
                            maxEnd++;
                        }
                        RDFProperty maxProperty = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MAX_INCLUSIVE);
                        if (rest.endsWith(")")) {
                            maxProperty = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MAX_EXCLUSIVE);
                        }
                        String max = rest.substring(0, maxEnd);
                        if (max.length() > 0 && Character.isDigit(max.charAt(0))) {
                            RDFSLiteral maxLiteral = owlModel.createRDFSLiteral(max, datatype);
                            map.put(maxProperty, maxLiteral);
                        }
                    }
                    if (map.size() == 1) {
                        return new HashMap();
                    }
                }
            }
        }
        return map;
    }
}
