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

package edu.stanford.smi.protegex.owl.swrl.model;

/**
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 * @deprecated Is this still needed?
 */
public interface SWRLConstants {

    public static interface BuiltIns {

        // Comparison Built-Ins
        public static final String EQUAL = "equal";

        public static final String NOT_EQUAL = "notEqual";

        public static final String LESS_THAN = "lessThan";

        public static final String LESS_THAN_OR_EQUAL = "lessThanOrEqual";

        public static final String GREATER_THAN = "greaterThan";

        public static final String GREATER_THAN_OR_EQUAL = "greaterThanOrEqual";

        // Math Built-Ins
        public static final String ADD = "add";

        public static final String SUBTRACT = "subtract";

        public static final String MULTIPLY = "multiply";

        public static final String DIVIDE = "divide";

        public static final String INTEGER_DIVIDE = "integerDivide";

        public static final String MOD = "mod";

        public static final String UNARY_PLUS = "unaryPlus";

        public static final String UNARY_MINUS = "unaryMinus";

        public static final String abs = "abs";

        public static final String CEILING = "ceiling";

        public static final String FLOOR = "floor";

        public static final String ROUND = "round";

        public static final String ROUND_HALF_TO_EVEN = "roundHalfToEven";

        public static final String SIN = "sin";

        public static final String COS = "cos";

        public static final String TAN = "tan";

        // Built-Ins for Boolean values
        public static final String BOOLEAN_NOT = "booleanNot";

        // Built-Ins for strings
        public static final String EQUAL_IGNORE_CASE = "equalIgnoreCase";

        public static final String STRING_CONCAT = "stringConcat";

        public static final String SUBSTRING = "substring";

        public static final String stringLength = "stringLength";

        public static final String NORMALIZE_SPACE = "normalizeSpace";

        public static final String UPPER_CASE = "upperCase";

        public static final String LOWER_CASE = "lowerCase";

        public static final String TRANSLATE = "translate";

        public static final String CONTAINS = "contains";

        public static final String CONTAINS_IGNORE_CASE = "containsIgnoreCase";

        public static final String STARTS_WITH = "startsWith";

        public static final String ENDS_WITH = "endsWith";

        public static final String SUBSTRING_BEFORE = "substringBefore";

        public static final String SUBSTRING_AFTER = "substringAfter";

        public static final String MATCHES = "matches";

        public static final String REPLACE = "replace";

        public static final String TOKENIZE = "tokenize";

        // Built-Ins for date, time, and duration
        public static final String YEAR_MONTH_DURATION = "yearMonthDuration";

        public static final String DAY_TIME_DURATION = "dateTimeDuration";

        public static final String DATE_TIME = "dateTime";

        public static final String TIME = "time";

        public static final String ADD_YEAR_MONTH_DURATIONS = "addYearMonthDurations";

        public static final String SUBTRACT_YEAR_MONTH_DURATIONS = "subtractYearMonthDurations";

        public static final String MULTIPLY_YEAR_MONTH_DURATIONS = "multiplyYearMonthDurations";

        public static final String DIVIDE_YEAR_MONTH_DURATIONS = "divideYearMonthDurations";

        public static final String ADD_DAY_TIME_DURATIONS = "addDayTimeDurations";

        public static final String SUBTRACT_DAY_TIME_DURATIONS = "subtractDayTimeDurations";

        public static final String MULTIPLY_DAY_TIME_DURATIONS = "multiplyDayTimeDurations";

        public static final String DIVIDE_DAY_TIME_DURATIONS = "divideDayTimeDurations";

        public static final String SUBTRACT_DATES = "subtractDates";

        public static final String SUBTRACT_TIMES = "subtractTimes";

        public static final String ADD_YEAR_MONTH_DURATION_TO_DATE_TIME = "addYearMonthDurationToDateTime";

        public static final String ADD_DAY_TIME_DURATION_TO_DATE_TIME = "addDayTimeDurationToDateTime";

        public static final String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE_TIME = "subtractYearMonthDurationFromDateTime";

        public static final String SUBTRACT_DAY_TIME_DURATION_FROM_DATE_TIME = "subtractDayTimeDurationFromDateTime";

        public static final String ADD_YEAR_MONTH_DURATION_TO_DATE = "addYearMonthDurationToDate";

        public static final String ADD_DAY_TIME_DURATION_TO_DATE = "addDayTimeDurationToDate";

        public static final String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE = "subtractYearMonthDurationFromDate";

        public static final String SUBTRACT_DAY_TIME_DURATION_FROM_DATE = "subtractDayTimeDurationFromDate";

        public static final String ADD_DATE_TIME_DURATION_TO_TIME = "addDateTimeDurationToTime";

        public static final String SUBTRACT_DATE_TIME_DURATION_FROM_TIME = "subtractDateTimeDurationFromTime";

        public static final String SUBTRACT_DATE_TIME_YEILDING_YEAR_MONTH_DURATION = "subtractDateTimeYieldingYearMonthDuration";

        public static final String SUBTRACT_DATE_TIME_YEILDING_DAY_TIME_DURATION = "subtractDateTimeYieldingDayTimeDuration";

        // Built-Ins for URIs
        public static final String RESOLVE_URI = "resolveURI";

        public static final String ANY_URI = "anyURI";

        // Built-Ins for lists
        public static final String LIST_CONCAT = "listConcat";

        public static final String LIST_INTERSECTION = "listIntersection";

        public static final String LIST_SUBTRACTION = "listSubtraction";

        public static final String MEMBER = "member";

        public static final String LENGTH = "length";

        public static final String FIRST = "first";

        public static final String REST = "rest";

        public static final String SUBLIST = "sublist";

        public static final String EMPTY = "empty";

    } // BuiltIns

} // SWRLConstants
