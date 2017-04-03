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

package edu.stanford.smi.protegex.owl.inference.dig.translator;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.util.HashMap;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 30, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DIGDataTypes {

    public static final int REAL_MULTIPLIER = 10000;

    private static DIGDataTypes instance;

    private HashMap map;

    private HashMap rangeMap;


    protected DIGDataTypes(final OWLModel owlModel) {
        // Create the map and put in the supported
        // datatypes
        map = new HashMap();

        // Integer
	    map.put(owlModel.getXSDint(), new IntDataTypeMapEntry());
	    map.put(owlModel.getXSDinteger(), new IntDataTypeMapEntry());
	    map.put(owlModel.getXSDlong(), new IntDataTypeMapEntry());
	    map.put(owlModel.getXSDshort(), new IntDataTypeMapEntry());
		map.put(owlModel.getXSDboolean(), new IntDataTypeMapEntry());

        // String
	    map.put(owlModel.getXSDstring(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDtime(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDanyURI(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDbase64Binary(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDdecimal(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDbyte(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDdouble(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDduration(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDdateTime(), new StringDataTypeMapEntry());
	    map.put(owlModel.getXSDdate(), new StringDataTypeMapEntry());
    }


    public static synchronized DIGDataTypes getInstance(OWLModel owlModel) {
        if (instance == null) {
            instance = new DIGDataTypes(owlModel);
        }

        return instance;
    }


    /**
     * Determines whether or not the datatype that
     * is represented by the Protege ValueType is
     * supported
     */
    public boolean isSupported(RDFSDatatype datatype) {
        return map.containsKey(datatype);
    }


    /**
     * Dermines if the object is of a supported
     * datatype.
     *
     * @param value The object to be tested.  This is typically
     *              an instance of a String, Number (Java Number), Boolean etc.
     */
    public boolean isSupported(RDFSLiteral value) {
        return map.containsKey(value.getDatatype());
    }


    /**
     * Gets the XML DIG Document tag name for the
     * specified specified object (e.g. ival for integers,
     * sval for strings).  This is the tag name that is used
     * to specify that an individual is in an attribute (datatype
     * property) relationship with a value.
     *
     * @param value The value that is the filler for the relationship.
     * @return The tag name, or <code>null</code> if the datatype
     *         that the value belongs to is not supported.
     */
    public String getIndividualAxiomValueTagName(RDFSLiteral value) {
        String ret = null;

        DataTypeMapEntry dataTypeMapEntry = (DataTypeMapEntry) map.get(value.getDatatype());

        if (dataTypeMapEntry != null) {
            ret = dataTypeMapEntry.getValueTag();
        }

        return ret;
    }


    /**
     * Gets the XML DIG Document element tag name that
     * is used to specify the range of a datatype property
     * for the specified value type.  e.g. rangeint if the
     * range of a datatype property is an integer.
     *
     * @param datatype The datatype type who's range tag we want
     * @return The tag name, or <code>null</code> is the valuetype
     *         is not supported.
     */
    public String getPropertyRangeTagName(RDFSDatatype datatype) {
        String ret = null;

        DataTypeMapEntry dataTypeMapEntry = (DataTypeMapEntry) map.get(datatype);

        if (dataTypeMapEntry != null) {
            ret = dataTypeMapEntry.getRangeTag();
        }

        return ret;
    }


    public String getConcreteDomainExpressionTagName(RDFSLiteral value) {
        String ret = null;

        DataTypeMapEntry dataTypeMapEntry = (DataTypeMapEntry) map.get(value.getDatatype());

        if (dataTypeMapEntry != null) {
            ret = dataTypeMapEntry.getValueRestrictionTag();
        }

        return ret;
    }


    /**
     * Gets the lexical rendering for the specified
     * value
     *
     * @param value The object whose lexical rendering
     *              is required.
     */
    public String getDataTypeRendering(RDFSLiteral value) {
       DataTypeMapEntry dataTypeMapEntry = (DataTypeMapEntry) map.get(value.getDatatype());
       if (dataTypeMapEntry != null) {
			return dataTypeMapEntry.getRenderering(value);
        }
		return null;
    }


    private abstract class DataTypeMapEntry {

        private String valueTag;

        private String rangeTag;

        private String valueRestrictionTag;


        public DataTypeMapEntry(String valueTag, String rangeTag, String valueRestrictionTag) {
            this.valueTag = valueTag;

            this.rangeTag = rangeTag;

            this.valueRestrictionTag = valueRestrictionTag;
        }


        public String getValueTag() {
            return valueTag;
        }


        public String getRangeTag() {
            return rangeTag;
        }


        public String getValueRestrictionTag() {
            return valueRestrictionTag;
        }


        public abstract String getRenderering(RDFSLiteral value);
    }

	private class IntDataTypeMapEntry extends DataTypeMapEntry {

		public IntDataTypeMapEntry() {
			super(DIGVocabulary.Language.IVAL,
	              DIGVocabulary.Tell.RANGE_INT,
	              DIGVocabulary.Language.INT_EQUALS);
		}

		public String getRenderering(RDFSLiteral value) {
			String rendering = value.getString();
			if (rendering.equalsIgnoreCase("true")) {
				return "1";
			} else if (rendering.equalsIgnoreCase("false")) {
				return "0";
			} else {
				return rendering;
			}
		}
	}

	private class StringDataTypeMapEntry extends DataTypeMapEntry {

		public StringDataTypeMapEntry() {
			super(DIGVocabulary.Language.SVAL,
			      DIGVocabulary.Tell.RANGE_STRING,
			      DIGVocabulary.Language.STRING_EQUALS);
		}


		public String getRenderering(RDFSLiteral value) {
			return value.getString();
		}
	}


}

