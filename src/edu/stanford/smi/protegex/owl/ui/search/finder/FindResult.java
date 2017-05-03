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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class FindResult {

    public static final int RESOURCE_NAME = 0;
    public static final int PROPERTY_NAME = 1;
    public static final int MATCH_VALUE = 2;
    public static final int LANG = 3;
    public static final int NUM_COLUMNS = 4;

    private RDFResource res;
    private Slot prop;
    private String matchValue;
    private String lang;
    private String searchTerm;

    public static FindResult createFindResult(RDFResource res, Slot prop, String searchTerm){
        try {
            return new FindResult(res, prop, searchTerm);
        }
        catch (Exception e) {
            return null;
        }
    }

    private FindResult(RDFResource res, Slot prop, String searchTerm) throws Exception {
        this.res = res;
        this.prop = prop;
        this.searchTerm = searchTerm;

        if (prop instanceof RDFProperty) {
            Collection values = res.getPropertyValues((RDFProperty) prop);
            for (Iterator i = values.iterator(); this.matchValue == null && i.hasNext();) {
                Object v = i.next();
                if (v instanceof RDFSLiteral) {
                    RDFSLiteral lit = (RDFSLiteral) v;
                    if (matches(lit.getString(), searchTerm)) {
                        this.matchValue = lit.getString();
                        this.lang = lit.getLanguage();
                    }
                    else{
                        throw new Exception("Cannot find search term [" + searchTerm +
                                            "] in literal [" + lit + "]");
                    }
                }
                else {
                    if (matches((String) v, searchTerm)) {
                        matchValue = (String)v;
                    }
                    else{
                        throw new Exception("Cannot find search term [" + searchTerm +
                        "] in string [" + v + "]");
                    }
                }
            }
        }
        else {
            matchValue = (String) res.getOwnSlotValue(prop);
        }
    }

    public Object get(int columnIndex) {
        switch (columnIndex) {
            case RESOURCE_NAME:
                return res;
            case PROPERTY_NAME:
                return prop;
            case MATCH_VALUE:
                return render((String) matchValue);
            case LANG:
                return lang;
        }
        return null;
    }

    public String getMatchValue(){
        return matchValue;
    }


    public RDFResource getMatchingResource() {
        return res;
    }


    private String render(String value) {
        try {
            String s = searchTerm.replaceAll("\\*", "");
            String searchStrLC = s.toLowerCase();
            String valueLC = value.toLowerCase();
            int startInd = valueLC.indexOf(searchStrLC);
            int endInd = startInd + searchStrLC.length();
            StringBuffer buf = new StringBuffer(value);
            buf = buf.insert(endInd, "</b>");
            buf = buf.insert(startInd, "<b>");
            value = "<html>" + buf.toString() + "</html>";
        }
        catch (Exception e) {
            value = "<html><font color=RED>" + value + "</font></html>";
        }
        return value;
    }

    public RDFResource getHost() {
        return (RDFResource) get(RESOURCE_NAME);
    }

    public static String getColumnName(int column) {
        switch (column) {
            case RESOURCE_NAME:
                return "Resource";
            case PROPERTY_NAME:
                return "Matching Property";
            case MATCH_VALUE:
                return "Match Value";
            case LANG:
                return "Language";
        }
        return "Unnamed";
    }

    private boolean matches(String value, String searchTerm) {
        String s = searchTerm.replaceAll("\\*", "");
        String searchStrLC = s.toLowerCase();
        String valueLC = value.toLowerCase();
        return valueLC.indexOf(searchStrLC) >= 0;
    }

    public String toString() {
        return "Result [" + res + ", " + prop + ", " + matchValue + ", " + lang + "]";
    }
}
