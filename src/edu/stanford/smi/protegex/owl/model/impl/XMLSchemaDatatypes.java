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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDDateTimeType;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * A utility class to manage the mapping of XML datatypes and the OWL classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class XMLSchemaDatatypes {


    public final static String XML_LITERAL = "XMLLiteral";

    public final static XSDDatatype[] floatTypes = new XSDDatatype[]{
            XSDDatatype.XSDfloat,
            XSDDatatype.XSDdecimal,
            XSDDatatype.XSDdouble
    };

    public final static XSDDatatype[] integerTypes = new XSDDatatype[]{
            XSDDatatype.XSDint,
            XSDDatatype.XSDpositiveInteger,
            XSDDatatype.XSDnegativeInteger,
            XSDDatatype.XSDnonPositiveInteger,
            XSDDatatype.XSDnonNegativeInteger,
            XSDDatatype.XSDlong,
            XSDDatatype.XSDinteger,
            XSDDatatype.XSDshort,
            XSDDatatype.XSDbyte,
            XSDDatatype.XSDunsignedLong,
            XSDDatatype.XSDunsignedInt,
            XSDDatatype.XSDunsignedShort,
            XSDDatatype.XSDunsignedByte
    };

    public final static XSDDatatype[] stringTypes = new XSDDatatype[]{
            XSDDatatype.XSDstring,
            XSDDatatype.XSDanyURI,
            XSDDatatype.XSDdate,
            XSDDatatype.XSDdateTime,
            XSDDatatype.XSDduration,
            XSDDatatype.XSDgDay,
            XSDDatatype.XSDgMonth,
            XSDDatatype.XSDgMonthDay,
            XSDDatatype.XSDgYear,
            XSDDatatype.XSDgYearMonth,
            XSDDatatype.XSDtime,
            XSDDatatype.XSDnormalizedString,
            XSDDatatype.XSDtoken,
            XSDDatatype.XSDlanguage,
            XSDDatatype.XSDName,
            XSDDatatype.XSDNCName,
            XSDDatatype.XSDNMTOKEN,
            XSDDatatype.XSDID,
            XSDDatatype.XSDIDREF,
            XSDDatatype.XSDENTITY
    };

    /**
     * Short form (e.g. "string") to Jena XSDDatatype object
     */
    private final static Hashtable<String, XSDDatatype> alias2XSDDatatype = new Hashtable<String, XSDDatatype>();

    private final static Hashtable<String, ValueType> uri2ValueTypeHashtable = new Hashtable<String, ValueType>();

    private final static Hashtable<ValueType, String> valueType2URIHashtable = new Hashtable<ValueType,String>();


    static {
        initHashtables(ValueType.BOOLEAN, XSDDatatype.XSDboolean);
        initHashtables(ValueType.FLOAT, floatTypes);
        initHashtables(ValueType.INTEGER, integerTypes);
        initHashtables(ValueType.STRING, stringTypes);
        initHashtables(ValueType.SYMBOL, XSDDatatype.XSDstring);
        initHashtables(null, XSDDatatype.XSDbase64Binary);
        initHashtables(null, XSDDatatype.XSDhexBinary);
        // initHashtables(ValueType.STRING,  XML_LITERAL);
    }


    public static XSDDatatype[] getAllTypes() {
        List<XSDDatatype> list = new ArrayList<XSDDatatype>();
        list.addAll(Arrays.asList(stringTypes));
        list.add(XSDDatatype.XSDboolean);
        list.addAll(Arrays.asList(floatTypes));
        list.addAll(Arrays.asList(integerTypes));
        return list.toArray(new XSDDatatype[0]);
    }


    public static Date getDate(String value) {
        XSDDateTime dt = (XSDDateTime) new XSDDateTimeType("dateTime").parse(value);
        Calendar dateCal = dt.asCalendar();
        dateCal.setTimeZone(new GregorianCalendar().getTimeZone());
        return dateCal.getTime();
    }


    public static String getDateString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, offset);
        XSDDateTime dt = new DateXSDDateTime(cal);
        
        // TODO: hack! Ignoring the timezone explicitly. 
        // This code has to be rewritten to work with the time zone, too.
        
        String dateStr = dt.toString();
        
        int index = dateStr.indexOf("Z");
        
        if (index >= 0) {
        	dateStr = dateStr.substring(0, index);
    }

        return dateStr;
    }


    public static String getDateTimeString(Date date) {
        return getDateString(date) + "T" + getTimeString(date);
    }


    public static String getDefaultAlias(ValueType valueType) {
        return getXSDDatatypeAlias(getValueTypeURI(valueType));
    }


    public static String getDefaultDateValue() {
        return getDateString(new Date());
    }


    public static String getDefaultDateTimeValue() {
        return getDateTimeString(new Date());
    }


    public static String getDefaultTimeValue() {
        return getTimeString(new Date());
    }


    public static XSDDatatype getDefaultXSDDatatype(ValueType valueType) {
        String alias = getDefaultAlias(valueType);
        return getXSDDatatype(alias);
    }


    public static Set<String> getSlotSymbols() {
        Set<String> values = new HashSet<String>();
        for (Enumeration<String> enu = uri2ValueTypeHashtable.keys(); enu.hasMoreElements();) {
            String str = enu.nextElement();
            values.add(str);
        }
        return values;
    }


    public static String getTimeString(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }


    public static String getTimeString(int hours, int minutes, int seconds) {
        String str = "";
        str += (hours < 10) ? "0" + hours : "" + hours;
        str += ":";
        str += (minutes < 10) ? "0" + minutes : "" + minutes;
        str += ":";
        str += (seconds < 10) ? "0" + seconds : "" + seconds;
        return str;
    }


    public static ValueType getValueType(String uri) {
        if (uri.equals(OWL.Class.getURI())) {
            return ValueType.CLS;
        }
        else if (uri.equals(RDFS.Literal.getURI())) {
            return ValueType.ANY;
        }
        else if (uri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral")) {
            return ValueType.STRING;
        }
        ValueType result = uri2ValueTypeHashtable.get(uri); // ???.toLowerCase());
        if (result == null) {
            return ValueType.ANY;
        }
        return result;
    }


    public static String getValueTypeURI(ValueType valueType) {
        Assert.assertTrue(valueType != null && valueType != ValueType.INSTANCE && valueType != ValueType.CLS);
        if (valueType == ValueType.ANY) {
            System.err.println("Warning: Replaced illegal value type \"Any\" with \"String\"");
            valueType = ValueType.STRING;
        }
        return valueType2URIHashtable.get(valueType);
    }


    public static XSDDatatype getXSDDatatype(String alias) {
        return alias2XSDDatatype.get(alias);
    }


    public static RDFDatatype getRDFDatatype(edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype) {
        if (datatype.equals(datatype.getOWLModel().getRDFXMLLiteralType())) {
            return XMLLiteralType.theXMLLiteralType;
        }
        else {
            return getXSDDatatype(datatype);
        }
    }


    public static XSDDatatype getXSDDatatype(edu.stanford.smi.protegex.owl.model.RDFSDatatype datatype) {
        if (datatype.isSystem()) {
            return alias2XSDDatatype.get(datatype.getName());
        }
        else {
            return getXSDDatatype(datatype.getBaseDatatype());
        }
    }


    public static String getXSDDatatypeAlias(XSDDatatype datatype) {
        String uri = datatype.getURI();
        return getXSDDatatypeAlias(uri);
    }


    public static String getXSDDatatypeAlias(String uri) {
    	return uri;
    }


    private static void initHashtables(ValueType valueType, String uri) {
        if (valueType2URIHashtable.get(valueType) == null) {
            valueType2URIHashtable.put(valueType, uri); // Only put first value into hashtable
        }
        // ??? uri = uri.toLowerCase();
        if (uri2ValueTypeHashtable.get(uri) == null) {
            uri2ValueTypeHashtable.put(uri, valueType);
        }
    }


    private static void initHashtables(ValueType valueType, XSDDatatype xsd) {
        String uri = xsd.getURI();
        if (valueType != null) {
            initHashtables(valueType, uri);
        }
        alias2XSDDatatype.put(uri, xsd);
    }


    private static void initHashtables(ValueType valueType, XSDDatatype[] xsds) {
        for (int i = 0; i < xsds.length; i++) {
            XSDDatatype xsd = xsds[i];
            initHashtables(valueType, xsd);
        }
    }


    public static boolean isDateSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDdate);
    }


    public static boolean isDateTimeSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDdateTime);
    }


    public static boolean isTimeSlot(RDFProperty property) {
        return isXMLSchemaSlot(property, XSDDatatype.XSDtime);
    }


    public static boolean isXMLLiteralSlot(Slot slot) {
        if (slot instanceof RDFProperty) {
            RDFProperty rdfProperty = (RDFProperty) slot;
            RDFResource range = rdfProperty.getRange();
            return rdfProperty.getOWLModel().getRDFXMLLiteralType().equals(range);
        }
        else {
            return false;
        }
    }


    public static boolean isValidAlias(String alias, ValueType valueType) {
        XSDDatatype xsd = getXSDDatatype(alias);
        if (xsd != null) {
            if (valueType == ValueType.INTEGER) {
                return Arrays.asList(integerTypes).contains(xsd);
            }
            else if (valueType == ValueType.STRING) {
                return Arrays.asList(stringTypes).contains(xsd);
            }
            else if (valueType == ValueType.FLOAT) {
                return Arrays.asList(floatTypes).contains(xsd);
            }
            else if (valueType == ValueType.BOOLEAN) {
                return xsd.equals(XSDDatatype.XSDboolean);
            }
        }
        return false;
    }


    public static boolean isStringAlias(String alias) {
        return Arrays.asList(stringTypes).contains(getXSDDatatype(alias));
    }


    public static boolean isXMLSchemaSlot(RDFProperty property, XSDDatatype datatype) {
        RDFResource range = property.getRange(true);
        return property.getOWLModel().getRDFSDatatypeByURI(datatype.getURI()).equals(range);
    }


    public static boolean isNumericDatatype(String uri) {
        for (int i = 0; i < floatTypes.length; i++) {
            XSDDatatype datatype = floatTypes[i];
            if (datatype.getURI().equals(uri)) {
                return true;
            }
        }
        for (int i = 0; i < integerTypes.length; i++) {
            XSDDatatype datatype = integerTypes[i];
            if (datatype.getURI().equals(uri)) {
                return true;
            }
        }
        return false;
    }


    static class DateXSDDateTime extends XSDDateTime {

        DateXSDDateTime(Calendar cal) {
            super(cal);
            mask = YEAR_MASK | MONTH_MASK | DAY_MASK;
        }
    }
}
