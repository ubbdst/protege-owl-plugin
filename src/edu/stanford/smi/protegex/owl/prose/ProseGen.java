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

package edu.stanford.smi.protegex.owl.prose;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: prashr
 * Date: Oct 6, 2003
 * Time: 2:08:41 PM
 * To change this template use Options | File Templates.
 */
public class ProseGen {

    ProseGen() {
        indentLevel = 0;
    }


    private static String insertTabSpaces() {
        if (!setTags) {
            return "";
        }
        String retString = "";
        for (int i = 0; i < indentLevel; i++) {
            retString += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        if (indentLevel > 0) {
            retString += "<b><FONT size=4>-&nbsp;</FONT></b>";
        }
        return retString;
    }


    private static String makePluralIfNecessary(String inputStr) {
        if (!inputStr.toLowerCase().endsWith("s"))
            return inputStr + "s";
        return inputStr;
    }


    private static boolean isDemarcatingCharacter(char ch) {
        if (!Character.isLetterOrDigit(ch) && ch != '<' && ch != '>' && ch != '/' && ch != '&' && ch != ';' && ch != '-')
            return true;
        return false;
    }


    private static boolean isDemarcatingCharacter(char prev, char ch, char next) {
        if (isDemarcatingCharacter(ch))
            return true;
        if (Character.isUpperCase(ch) && Character.isLowerCase(prev) && Character.isLowerCase(next))
            return true;
        return false;
    }


    private static boolean hasVowelStartChar(String word) {
        String tempWordInLower = word.toLowerCase().trim();
        if (tempWordInLower.length() > 1) {
            if (tempWordInLower.startsWith("a") || tempWordInLower.startsWith("e") || tempWordInLower.startsWith("i") || tempWordInLower.startsWith("o") || tempWordInLower.startsWith("u"))
                return true;
        }
        return false;
    }


    private static String splitWordOnCapitals(String inputStr) {
        inputStr = inputStr.trim();
        if (inputStr.length() > 0) {
            char[] chrArray = inputStr.toCharArray();
            String resultStr = "";
            String BufferStr = "";
            if (!isDemarcatingCharacter(chrArray[0])) {
                BufferStr += chrArray[0];
            }
            for (int index = 1; index < chrArray.length - 1; index++) {
                if (isDemarcatingCharacter(chrArray[index - 1], chrArray[index], chrArray[index + 1])) {
                    if (BufferStr != "") {
                        resultStr += " " + BufferStr;
                        BufferStr = "";
                    }
                }
                char ch = chrArray[index];
                if (!isDemarcatingCharacter(ch))
                    BufferStr += ch;
            }
            char ch = chrArray[chrArray.length - 1];
            if (!isDemarcatingCharacter(ch) && chrArray.length > 1)
                BufferStr += ch;
            if (resultStr != "")
                resultStr += " ";
            resultStr += BufferStr;
            return resultStr;
        }
        return "";
    }


    private static String getProseFromIntersectionCls(OWLIntersectionClass interCls) {
        final Collection operands = interCls.getOperands();
        String retValString = "";
        if (insertNoTabs) {
            retValString += "The intersection of";
        }
        else if (!includePreText && indentLevel != 0) {
            retValString += "<br>" + insertTabSpaces() + "The intersection of</br>";
            includePreText = true;
        }
        else if (indentLevel > 0 && includePreText) {
            retValString += "<br>" + insertTabSpaces() + "The intersection of</br>";
        }
        else if (includePreText) {
            retValString += "The intersection of";
        }
        indentLevel++;
        if (operands.size() == 1) {
            Iterator it = operands.iterator();
            retValString += "<br>" + insertTabSpaces() + ((RDFSClass) it.next()).getBrowserText() + "</br>";
        }
        for (Iterator it = operands.iterator(); it.hasNext();) {
            RDFSClass inputcls = (RDFSClass) it.next();
            if (!(inputcls instanceof OWLNamedClass) && !(inputcls instanceof OWLIntersectionClass) && !(inputcls instanceof OWLUnionClass)) {
                String proseStr = getProseFromCls(inputcls);
                if (proseStr.toLowerCase().startsWith("has")) {
                    retValString += "<br>" + insertTabSpaces() + "Any object that " + proseStr + "</br>";
                }
                else if (proseStr.toLowerCase().startsWith("is")) {

                    retValString += "<br>" + insertTabSpaces() + proseStr + "</br>";

                }
                else {
                    if (proseStr != "") {
                        retValString += "<br>" + insertTabSpaces() + "Any object where " + proseStr + "</br>";
                    }
                }
            }
            else {

                String proseFromCls = splitWordOnCapitals(getProseFromCls(inputcls)).trim();
                if (proseFromCls != "") {
                    retValString += "<br>" + insertTabSpaces() + proseFromCls + "</br>";
                }
            }

        }
        //  retValString += "</p>";
        indentLevel--;
        return retValString;
    }


    private static String getProseFromUnionCls(OWLUnionClass interCls) {
        final Collection operands = interCls.getOperands();
        String retValString = "";
        if (insertNoTabs) {
            retValString += "The union of";
        }
        else if (!includePreText && indentLevel != 0) {
            retValString += "<br>" + insertTabSpaces() + "The union of</br>";
            includePreText = true;
        }
        else if (indentLevel > 0 && includePreText) {
            retValString += "<br>" + insertTabSpaces() + "The intersection of</br>";
        }
        else if (includePreText) {
            retValString += "The union of";
        }

        indentLevel++;
        if (operands.size() == 1) {
            Iterator it = operands.iterator();

            retValString += "<br>" + insertTabSpaces() + ((RDFSClass) it.next()).getBrowserText() + "</br>";
        }
        for (Iterator it = operands.iterator(); it.hasNext();) {
            RDFSClass inputcls = (RDFSClass) it.next();
            if (!(inputcls instanceof OWLNamedClass) && !(inputcls instanceof OWLIntersectionClass) && !(inputcls instanceof OWLUnionClass)) {
                String proseStr = getProseFromCls(inputcls);
                if (proseStr.toLowerCase().startsWith("has")) {
                    retValString += "<br>" + insertTabSpaces() + "Any object that " + proseStr + "</br>";
                }
                else if (proseStr.toLowerCase().startsWith("is")) {
                    retValString += "<br>" + insertTabSpaces() + proseStr + "</br>";
                }
                else {
                    if (proseStr != "") {
                        retValString += "<br>" + insertTabSpaces() + "Any object where " + proseStr + "</br>";
                    }
                }
            }
            else {
                String proseFromCls = splitWordOnCapitals(getProseFromCls(inputcls)).trim();
                if (proseFromCls != "") {
                    retValString += "<br>" + insertTabSpaces() + proseFromCls + "</br>";
                }
            }

        }
        indentLevel--;
        return retValString;
    }


    private static String getProseFromComplementCls(OWLComplementClass complementClass) {
        RDFSClass operand = complementClass.getComplement();

        String retValString = "";
        //   if (includePreText) {
        String proseFromCls = getProseFromCls(operand);
        if (proseFromCls.toLowerCase().startsWith("has")) {
            proseFromCls = proseFromCls.replaceFirst("has", "have");
            retValString += "Does not " + proseFromCls;
        }
        else if (proseFromCls.toLowerCase().indexOf(" is ") > 0) {
            retValString += proseFromCls.replaceAll(" is ", " is not ");
        }

        else if (!hasVowelStartChar(proseFromCls)) {
            retValString += "Is not a " + splitWordOnCapitals(proseFromCls);
        }
        else {
            retValString += "Is not an " + splitWordOnCapitals(proseFromCls);
        }
        return retValString;
    }


    private static String getProseFromMaxCardiRestriction(OWLMaxCardinality restrCls) {
        int value = restrCls.getCardinality();
        String restSlot = restrCls.getOnProperty().getName();
        String retValString = "";
        if (includePreText) {
            retValString += "the property ";
            // indentLevel--;
        }


        if (restSlot.toLowerCase().startsWith("has")) {
            String strArray = restSlot.substring(3);
            if (value == 1 || strArray.endsWith("s")) {
                retValString = "has at most " + value + " " + splitWordOnCapitals(strArray);
            }
            else {
                retValString = "has at most " + value + " " + splitWordOnCapitals(strArray) + "s";
            }
        }
        else if (restSlot.toLowerCase().startsWith("is")) {
            String strArray = restSlot.toLowerCase().substring(2);
            retValString = "is " + splitWordOnCapitals(strArray) + " " + value;

        }
        else {
            if (value == 1) {
                retValString += splitWordOnCapitals(restSlot) + " has at most " + value + " value";
            }
            else {
                retValString += splitWordOnCapitals(restSlot) + " has at most " + value + " values";
            }
        }
        return retValString;
    }


    private static String getProseFromMinCardiRestriction(OWLMinCardinality restrCls) {
        int value = restrCls.getCardinality();
        String restSlot = restrCls.getOnProperty().getName();
        String retValString = "";
        if (includePreText) {
            retValString += "the property ";
            //  indentLevel--;
        }
        if (restSlot.toLowerCase().startsWith("has")) {
            String strArray = restSlot.substring(3);
            if (value == 1 || strArray.endsWith("s")) {
                retValString = "has at least " + value + " " + splitWordOnCapitals(strArray);
            }
            else {
                retValString = "has at least " + value + " " + splitWordOnCapitals(strArray) + "s";
            }
        }
        else {
            if (value == 1) {
                retValString += splitWordOnCapitals(restSlot) + " has at least " + value + " value";
            }
            else {
                retValString += splitWordOnCapitals(restSlot) + " has at least " + value + " values";
            }
        }
        return retValString;
    }


    private static String getProseFromCardiRestriction(OWLCardinality restrCls) {
        int value = restrCls.getCardinality();
        String restSlot = restrCls.getOnProperty().getName();
        String retValString = "";
        if (includePreText) {
            retValString += "the property ";
            // indentLevel--;
        }
        if (restSlot.toLowerCase().startsWith("has")) {
            String strArray = restSlot.substring(3);
            if (value == 1 || strArray.endsWith("s")) {
                retValString = "has exactly " + value + " " + splitWordOnCapitals(strArray);
            }
            else {
                retValString = "has exactly " + value + " " + splitWordOnCapitals(strArray) + "s";
            }
        }
        else {
            if (value == 1) {
                retValString += splitWordOnCapitals(restSlot) + " has exactly " + value + " value";
            }
            else {
                retValString += splitWordOnCapitals(restSlot) + " has exactly " + value + " values";
            }
        }
        return retValString;
    }


    private static String getProseFromHasRestriction(OWLHasValue restrCls) {
        Object hasValue = restrCls.getHasValue();
        String browserText;
        String retValString = "";

        if (hasValue instanceof RDFResource) {
            browserText = ((RDFResource) hasValue).getBrowserText();
        }
        else {
            browserText = hasValue.toString();
        }
        Slot restSlot = restrCls.getOnProperty();
        String slotName = restSlot.getName();
        String testVar = slotName;
        if (slotName.toLowerCase().startsWith("has")) {
            String strArray = slotName.substring(3);
            if (browserText.equalsIgnoreCase("true")) {
                retValString = "has " + splitWordOnCapitals(strArray);
            }
            else if (browserText.equalsIgnoreCase("false")) {
                retValString = "has no " + splitWordOnCapitals(strArray);
            }
            else {
                retValString = "has " + splitWordOnCapitals(browserText) + " " + splitWordOnCapitals(strArray);
            }
        }
        else if (slotName.toLowerCase().startsWith("is")) {
            String strArray = slotName.substring(2);
            if (browserText.equalsIgnoreCase("false"))
                retValString = "is not " + splitWordOnCapitals(strArray);
            else if (browserText.equalsIgnoreCase("true"))
                retValString = "is " + splitWordOnCapitals(strArray);
            else
                retValString = "is " + splitWordOnCapitals(strArray) + " " + splitWordOnCapitals(browserText);

        }
        else {
            retValString = "the property " + splitWordOnCapitals(testVar) + " has value " + splitWordOnCapitals(browserText);
        }
        return retValString;
    }


    private static String generatePreText(RDFSClass inputClass, RDFSClass callingClass) {
        if (inputClass instanceof OWLIntersectionClass) {
            // return "must be an intersection of";
            return "are";
        }

        else if (inputClass instanceof OWLNamedClass) {
            if (callingClass instanceof OWLSomeValuesFrom) {
                return "is of type";
            }
            else {
                return "are of type";
            }
        }
        else if (inputClass instanceof OWLMaxCardinality) {
            return "must have at most";
        }
        else if ((inputClass instanceof OWLCardinality)) {
            return "must be an object where";
        }
        else if ((inputClass instanceof OWLMinCardinality)) {
            return "must be an object where";
        }
        else if (inputClass instanceof OWLHasValue) {
            return "must be an object where";
        }
        else if ((inputClass instanceof OWLSomeValuesFrom)) {
            return "must be an object where";
        }
        else if ((inputClass instanceof OWLAllValuesFrom)) {
            return "must be an object where";
        }
        else if ((inputClass instanceof OWLEnumeratedClass)) {
            return "are either";
        }
        else if ((inputClass instanceof OWLComplementClass)) {
            return "must be an object that";
        }
        else if ((inputClass instanceof OWLUnionClass)) {
            //return "must be the union of";
            return "are";
        }

        return "";
    }


    private static String getProseFromSomeRestriction(OWLSomeValuesFrom restrCls) {
        String retValString = "";
        String browserText = "";
        String slotName = restrCls.getOnProperty().getName();
        RDFResource filler = restrCls.getFiller();
        if (!(filler instanceof RDFSClass)) {
            if (filler instanceof OWLDataRange) {
                OWLDataRange dataRange = (OWLDataRange) filler;
                RDFList oneOf = dataRange.getOneOf();
                if (oneOf != null) {
                    Collection values = oneOf.getValues();
                    for (Iterator it = values.iterator(); it.hasNext();) {
                        browserText += "\"" + it.next() + "\"";
                        if (it.hasNext()) {
                            browserText += " or ";
                        }
                    }
                    if (values.size() == 0) {
                        retValString = "at least one of the values of the " + splitWordOnCapitals(slotName) + " property has no type";
                    }
                    else {
                        if (slotName.toLowerCase().startsWith("has")) {
                            String strArray = slotName.substring(3);
                            retValString = "at least one of the values has either " + splitWordOnCapitals(browserText) + " " + splitWordOnCapitals(strArray);

                        }
                        else if (slotName.toLowerCase().startsWith("is")) {
                            String strArray = slotName.substring(2);
                            retValString = "some of the values are " + splitWordOnCapitals(strArray) + " " + splitWordOnCapitals(browserText);

                        }
                        else {
                            retValString = "at least one of the values of the " + splitWordOnCapitals(slotName) + " property is either " + splitWordOnCapitals(browserText);
                        }
                    }
                }
                else {
                    retValString = "no value";
                }
            }
            else {
                browserText = filler.getName();
                if (slotName.toLowerCase().startsWith("has")) {
                    String strArray = slotName.substring(3);
                    retValString = "at least one value has " + splitWordOnCapitals(browserText) + " " + splitWordOnCapitals(strArray);

                }
                else if (slotName.toLowerCase().startsWith("is")) {
                    String strArray = slotName.substring(2);

                    retValString = "some of the values are " + splitWordOnCapitals(strArray) + " " + splitWordOnCapitals(browserText);

                }
                else {
                    retValString = "at least one of the values of the " + splitWordOnCapitals(slotName) + " property is of type " + browserText;
                }
            }

        }
        else {
            RDFSClass someClass = (RDFSClass) filler;
            insertNoTabs = true;
            browserText = getProseFromCls((RDFSClass) someClass);
            insertNoTabs = false;
            if (slotName.toLowerCase().startsWith("has")) {
                String strArray = slotName.substring(3);
                String tempString = splitWordOnCapitals(browserText);
                if (tempString.toLowerCase().endsWith("s")) {
                    retValString = "has " + tempString + " as its " + splitWordOnCapitals(strArray);
                }
                else {
                    retValString = "has " + (hasVowelStartChar(tempString) ? "an " : "a ") + tempString + " as its " + splitWordOnCapitals(strArray);
                }
            }
            else if (slotName.toLowerCase().startsWith("is")) {
                String strArray = slotName.substring(2);
                retValString = "Some instances are " + splitWordOnCapitals(strArray) + " " + (browserText);
            }
            else {
                retValString = "at least one of the values of the " + splitWordOnCapitals(slotName) + " property " + generatePreText(someClass, restrCls) + " " + splitWordOnCapitals(browserText);
            }
        }


        return retValString;
    }


    private static String getProseFromAllRestriction(OWLAllValuesFrom restrCls) {
        String browserText = "";
        String retValString = "";
        String slotName = restrCls.getOnProperty().getName();
        RDFResource filler = restrCls.getFiller();
        if (!(filler instanceof RDFSClass)) {
            if (filler instanceof OWLDataRange) {
                RDFList oneOf = ((OWLDataRange) filler).getOneOf();
                Collection values = oneOf == null ? Collections.EMPTY_LIST : oneOf.getValues();
                for (Iterator it = values.iterator(); it.hasNext();) {
                    browserText += "\"" + it.next() + "\"";
                    if (it.hasNext()) {
                        browserText += " or ";
                    }
                }
                if (slotName.toLowerCase().startsWith("has")) {
                    String[] strArray = slotName.toLowerCase().split("has");
                    if (strArray.length > 1) {
                        retValString = "has " + browserText + " " + strArray[1];
                    }
                }
                else if (slotName.toLowerCase().startsWith("is")) {
                    String[] strArray = slotName.toLowerCase().split("is");
                    if (strArray.length > 1) {
                        retValString = "is " + strArray[1] + " " + browserText;
                    }
                }
                else {
                    if (values.isEmpty()) {
                        retValString = "all values of the " + splitWordOnCapitals(slotName) + " property have no type";
                    }
                    else {
                        if (slotName.toLowerCase().startsWith("has")) {
                            String strArray = slotName.substring(3);
                            retValString = "all values have either " + splitWordOnCapitals(browserText) + " " + splitWordOnCapitals(strArray);

                        }
                        else if (slotName.toLowerCase().startsWith("is")) {
                            String strArray = slotName.substring(2);

                            retValString = "all instances are " + splitWordOnCapitals(strArray) + " " + splitWordOnCapitals(browserText);

                        }
                        else {
                            retValString = "all values of the " + splitWordOnCapitals(slotName) + " property are either " + splitWordOnCapitals(browserText);
                        }
                    }
                }
            }
            else {
                browserText = filler.getName();
                if (slotName.toLowerCase().startsWith("has")) {
                    String strArray = slotName.substring(3);
                    retValString = "all values have " + splitWordOnCapitals(browserText) + " " + splitWordOnCapitals(strArray);

                }
                else {
                    retValString = "all values of the " + splitWordOnCapitals(slotName) + " property are of type " + splitWordOnCapitals(browserText);
                }
            }

        }
        else {
            RDFSClass allClass = (RDFSClass) filler;
            insertNoTabs = true;
            browserText = getProseFromCls((RDFSClass) allClass);
            insertNoTabs = false;
            if (slotName.toLowerCase().startsWith("has")) {
                String strArray = slotName.substring(3);
                retValString = "all " + makePluralIfNecessary(splitWordOnCapitals(strArray)) + " are " + splitWordOnCapitals(browserText);

            }
            else if (slotName.toLowerCase().startsWith("is")) {
                String strArray = slotName.substring(2);
                retValString = "All instances are " + splitWordOnCapitals(strArray) + " " + splitWordOnCapitals(browserText);

            }
            else {
                retValString = "all values of the " + splitWordOnCapitals(slotName) + " property " + generatePreText(allClass, restrCls) + " " + browserText;
            }
        }
        return retValString;
    }


    private static String getProseFromEnumerationCls
            (OWLEnumeratedClass
                    enumCls) {

        Collection enumColl = enumCls.getOneOf();
        String retvalString = "";
        for (Iterator it = enumColl.iterator(); it.hasNext();) {
            Instance inputcls = (Instance) it.next();
            retvalString += inputcls.getName();
            if (it.hasNext()) {
                retvalString += " or ";
            }
        }
        retvalString += "";
        return retvalString;
    }


    private static boolean checked = false;


    private static String getProseFromNamedCls
            (OWLNamedClass
                    inputCls) {
        String browserText = inputCls.getBrowserText();
        if (browserText.split(":").length > 1) {
            browserText = browserText.split(":")[1];
        }

        String retvalString = "";
        if (!checked) {
            checked = true;
            retvalString += browserText;
        }
        else if (browserText != null) {
            retvalString += browserText;
        }
        return splitWordOnCapitals(retvalString).trim();

    }


    private static int indentLevel = 0;

    private static boolean includePreText = true;

    private static boolean insertNoTabs = false;


    public static String getProseFromCls
            (RDFSClass
                    inputClass) {
        String retval = "";
        if (inputClass instanceof OWLIntersectionClass)
            retval = getProseFromIntersectionCls((OWLIntersectionClass) inputClass);
        else if (inputClass instanceof OWLNamedClass)
            retval = getProseFromNamedCls((OWLNamedClass) inputClass);
        else if (inputClass instanceof OWLMaxCardinality)
            retval = getProseFromMaxCardiRestriction((OWLMaxCardinality) inputClass);
        else if ((inputClass instanceof OWLCardinality))
            retval = getProseFromCardiRestriction((OWLCardinality) inputClass);
        else if ((inputClass instanceof OWLMinCardinality))
            retval = getProseFromMinCardiRestriction((OWLMinCardinality) inputClass);
        else if (inputClass instanceof OWLHasValue)
            retval = getProseFromHasRestriction((OWLHasValue) inputClass);
        else if ((inputClass instanceof OWLSomeValuesFrom))
            retval = getProseFromSomeRestriction((OWLSomeValuesFrom) inputClass);
        else if ((inputClass instanceof OWLAllValuesFrom))
            retval = getProseFromAllRestriction((OWLAllValuesFrom) inputClass);
        else if ((inputClass instanceof OWLEnumeratedClass))
            retval = getProseFromEnumerationCls((OWLEnumeratedClass) inputClass);
        else if ((inputClass instanceof OWLComplementClass))
            retval = getProseFromComplementCls((OWLComplementClass) inputClass);
        else if ((inputClass instanceof OWLUnionClass))
            retval = getProseFromUnionCls((OWLUnionClass) inputClass);
        return retval;
    }


    private static boolean setTags = false;


    public static String getProseAsString
            (RDFSClass
                    inputClass) {
        return getProseAsString(inputClass, true);
    }


    private static boolean isCapsBlock(char prev, char ch, char next) {

        if ((Character.isUpperCase(ch) && Character.isUpperCase(prev)) || (Character.isUpperCase(ch) && Character.isUpperCase(next)))
            return true;
        return false;
    }


    private static String alterStringForNewLine(String inputStr) {
        int startindex = 0;
        int newIndex = 0;
        char[] tempArray = inputStr.toCharArray();
        while (true) {
            newIndex = inputStr.indexOf("<br>", startindex);
            if (newIndex >= 0 && newIndex + 4 < inputStr.length()) {
                tempArray[newIndex + 4] = Character.toUpperCase(inputStr.charAt(newIndex + 4));
                startindex = newIndex + 1;
            }
            else {
                break;
            }
        }
        return new String(tempArray);
    }


    private static String changeCase(String inputString) {
        if (inputString.length() == 0) {
            return "";
        }
        else {
            char[] chrArray = inputString.toCharArray();
            String outputBuffer = "" + Character.toLowerCase(chrArray[0]);
            for (int index = 1; index < chrArray.length - 1; index++) {
                if (isCapsBlock(chrArray[index - 1], chrArray[index], chrArray[index + 1])) {
                    outputBuffer += chrArray[index];
                }
                else {
                    outputBuffer += Character.toLowerCase(chrArray[index]);
                }
            }
            if (chrArray.length > 1) {
                outputBuffer += chrArray[chrArray.length - 1];
            }
            return alterStringForNewLine(outputBuffer);
        }
    }


    public static String getProseAsString
            (RDFSClass
                    inputClass, boolean sethtmlTags) {
        setTags = sethtmlTags;
        String result = "";
        indentLevel = 0;
        includePreText = true;
        if (inputClass instanceof OWLNamedClass) {
            if (inputClass.isSystem())
                return "";
            Collection necessaryAndSufficientCol = inputClass.getEquivalentClasses();

            includePreText = false;
            int index = 1;
            for (Iterator iterator = necessaryAndSufficientCol.iterator(); iterator.hasNext();) {
                Cls cls = (Cls) iterator.next();
                if (index == 1) {
                    result = "<html><b>Necessary and Sufficient Conditions:</b>";
                }
                if (index == 1 && iterator.hasNext() || index > 1) {
                    result += "<br><b>Condition Set: " + index + "</b></br>";
                }
                if (cls instanceof OWLIntersectionClass || cls instanceof OWLUnionClass || cls instanceof OWLComplementClass) {
                    if (iterator.hasNext() || index > 1) {
                        indentLevel++;
                        result += changeCase(getProseFromCls((RDFSClass) cls));
                        indentLevel--;
                    }
                    else {
                        result += changeCase(getProseFromCls((RDFSClass) cls));
                    }
                }
                else {
                    indentLevel++;
                    result += "<br>" + insertTabSpaces() + changeCase(getProseFromCls((RDFSClass) cls)) + "</br>";
                    indentLevel--;
                }
                index++;
            }
            if (result == "")
                result = "<html>";//No <b>Necessary and Sufficient</b> conditions defined";
            Collection necessaryCol = inputClass.getPureSuperclasses();
            index = 1;
            for (Iterator iterator = necessaryCol.iterator(); iterator.hasNext();) {
                Cls cls = (Cls) iterator.next();
                if (index == 1) {
                    if (result.equalsIgnoreCase("<html>")) {
                        result += "<b>Necessary Conditions:</b>";
                    }
                    else {
                        result += "<br><b>Necessary Conditions:</b></br>";
                    }
                }
                if (cls instanceof OWLIntersectionClass || cls instanceof OWLUnionClass || cls instanceof OWLComplementClass) {
                    if (iterator.hasNext() || index > 1) {
                        indentLevel++;
                        result += changeCase(getProseFromCls((RDFSClass) cls));
                        indentLevel--;
                    }
                    else {
                        result += changeCase(getProseFromCls((RDFSClass) cls));
                    }

                }
                else if (cls instanceof RDFSClass) {
                    indentLevel++;
                    String dataBuffer = changeCase(getProseFromCls((RDFSClass) cls));
                    if (dataBuffer != "") {
                        result += "<br>" + insertTabSpaces() + dataBuffer + "</br>";
                    }
                    indentLevel--;
                }
                index++;
            }
            if (index == 1)
                result += "";//"<br>No <b>Necessary conditions</b> defined</br>";
            if (result.equalsIgnoreCase("<html>")) {
                result = "";
            }
            else {
                result += "</html>";
            }
        }
        else {
            result = getProseFromCls(inputClass);
            result = changeCase(result);
            if (result.length() > 0) {
                String subStr = result.substring(0, 1).toUpperCase();
                result = subStr + result.substring(1) + " ";
                if (setTags) {
                    result = "<HTML><p>" + result + "</p></HTML>";
                }
                else {
                    result = result.replaceAll("<br>", " ");
                    result = result.replaceAll("</br>", " ");
                    result = result.replaceAll("<b>", " ");
                    result = result.replaceAll("</b>", " ");
                }
            }
        }
        return result;
    }
}