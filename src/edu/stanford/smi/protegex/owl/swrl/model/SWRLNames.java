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

import edu.stanford.smi.protegex.owl.model.OWLNames;

/**
 * Defines the names of the SWRL system ontology classes and properties.  This corresponds to the Model interface in general Protege, and
 * the OWLNames interface in the OWL Plugin.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface SWRLNames extends OWLNames {

  public final static String SWRL_IMPORT = "http://www.w3.org/2003/11/swrl";
  public final static String SWRLB_IMPORT = "http://www.w3.org/2003/11/swrlb";
  public final static String SWRLA_IMPORT = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl";
  public final static String SWRLX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl";
  public final static String SWRLM_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl";
  public final static String SWRLTBOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl";
  public final static String SWRLABOX_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl";
  public final static String SWRLRDF_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/rdfb.owl";
  public final static String SWRLTEMPORAL_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl";
  public final static String SWRLXML_IMPORT = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl";
  public final static String SQWRL_IMPORT = "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl";
  
  public final static String SWRL_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrl.owl";
  public final static String SWRLB_ALT_IMPORT = "http://www.daml.org/rules/proposal/swrlb.owl";
  
  public final static String SWRL_NAMESPACE = "http://www.w3.org/2003/11/swrl#";
  public final static String SWRLB_NAMESPACE = "http://www.w3.org/2003/11/swrlb#";
  public final static String SWRLA_NAMESPACE = "http://swrl.stanford.edu/ontologies/3.3/swrla.owl#";
  public final static String SWRLX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl#";
  public final static String SWRLM_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl#";
  public final static String SWRLTBOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/tbox.owl#";
  public final static String SWRLABOX_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/abox.owl#";
  public final static String SWRLRDF_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/rdfb.owl#";
  public final static String SWRLTEMPORAL_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#";
  public final static String SWRLXML_NAMESPACE = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl#";
  public final static String SQWRL_NAMESPACE = "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#";
  
  public final static String SWRL_PREFIX = "swrl";
  public final static String SWRLB_PREFIX = "swrlb";
  public final static String SWRLA_PREFIX = "swrla";
  public final static String SWRLX_PREFIX = "swrlx";
  public final static String SWRLM_PREFIX = "swrlm";
  public final static String SWRLTBOX_PREFIX = "tbox";
  public final static String SWRLABOX_PREFIX = "abox";
  public final static String SWRLRDF = "rdfb";
  public final static String SWRLTEMPORAL_PREFIX = "temporal";
  public final static String SWRLXML_PREFIX = "swrlxml";
  public final static String SQWRL_PREFIX = "sqwrl";

  public final static String EXCLUDE_STANDARD_IMPORTS = "protege.owl.swrl.exclude_standard_imports";
  public final static String DEFAULT_RULE_ENGINE = "protege.owl.swrl.default_rule_engine";
  
  
  public static interface Cls {
    
      public final static String IMP = (SWRL_NAMESPACE  +"Imp").intern();
      public final static String ATOM = (SWRL_NAMESPACE  + "Atom").intern();
      public final static String CLASS_ATOM = (SWRL_NAMESPACE  + "ClassAtom").intern();
      public final static String INDIVIDUAL_PROPERTY_ATOM = (SWRL_NAMESPACE  + "IndividualPropertyAtom").intern();
      public final static String DATAVALUED_PROPERTY_ATOM = (SWRL_NAMESPACE  + "DatavaluedPropertyAtom").intern();
      public final static String DIFFERENT_INDIVIDUALS_ATOM = (SWRL_NAMESPACE  + "DifferentIndividualsAtom").intern();
      public final static String SAME_INDIVIDUAL_ATOM = (SWRL_NAMESPACE  + "SameIndividualAtom").intern();
      public final static String BUILTIN_ATOM = (SWRL_NAMESPACE  + "BuiltinAtom").intern();
      public final static String DATA_RANGE_ATOM = (SWRL_NAMESPACE  + "DataRangeAtom").intern();
      public final static String BUILTIN = (SWRL_NAMESPACE  + "Builtin").intern();
      public final static String VARIABLE = (SWRL_NAMESPACE  + "Variable").intern();
      public final static String ATOM_LIST = (SWRL_NAMESPACE  + "AtomList").intern();
  } // Cls
  
  public static interface Slot {
      public final static String BODY = (SWRL_NAMESPACE  + "body").intern();
      public final static String HEAD = (SWRL_NAMESPACE  + "head").intern();
      public final static String ARGUMENTS = (SWRL_NAMESPACE  + "arguments").intern();
      public final static String BUILTIN = (SWRL_NAMESPACE  + "builtin").intern();
      public final static String ARGUMENT1 = (SWRL_NAMESPACE  + "argument1").intern();
      public final static String ARGUMENT2 = (SWRL_NAMESPACE  + "argument2").intern();
      public final static String CLASS_PREDICATE = (SWRL_NAMESPACE  + "classPredicate").intern();
      public final static String PROPERTY_PREDICATE = (SWRL_NAMESPACE  + "propertyPredicate").intern();
      public final static String DATA_RANGE = (SWRL_NAMESPACE  + "dataRange").intern();
      public final static String ARGS = (SWRLB_NAMESPACE  + "args").intern();
      public final static String MIN_ARGS = (SWRLB_NAMESPACE  + "minArgs").intern();
      public final static String MAX_ARGS = (SWRLB_NAMESPACE  + "maxArgs").intern();    
  } // Slot

  public static interface CoreBuiltIns {
      public final static String EQUAL = (SWRLB_NAMESPACE  + "equal").intern();    
      public final static String NOT_EQUAL = (SWRLB_NAMESPACE  + "notEqual").intern();
      public final static String LESS_THAN = (SWRLB_NAMESPACE  + "lessThan").intern();
      public final static String LESS_THAN_OR_EQUAL = (SWRLB_NAMESPACE  + "lessThanOrEqual").intern();
      public final static String GREATER_THAN = (SWRLB_NAMESPACE  + "greaterThan").intern();
      public final static String GREATER_THAN_OR_EQUAL = (SWRLB_NAMESPACE  + "greaterThanOrEqual").intern();
      public final static String ADD = (SWRLB_NAMESPACE  + "add").intern();
      public final static String SUBTRACT = (SWRLB_NAMESPACE  + "subtract").intern();
      public final static String MULTIPLY = (SWRLB_NAMESPACE  + "multiply").intern();
      public final static String DIVIDE = (SWRLB_NAMESPACE  + "divide").intern();
      public final static String INTEGER_DIVIDE = (SWRLB_NAMESPACE  + "integerDivide").intern();
      public final static String MOD = (SWRLB_NAMESPACE  + "mode").intern();
      public final static String POW = (SWRLB_NAMESPACE  + "pow").intern();
      public final static String UNARY_PLUS = (SWRLB_NAMESPACE  + "unaryPlus").intern();
      public final static String UNARY_MINUS = (SWRLB_NAMESPACE  + "unaryMinus").intern();
      public final static String ABS = (SWRLB_NAMESPACE  + "abs").intern();
      public final static String CEILING = (SWRLB_NAMESPACE  + "ceiling").intern();
      public final static String FLOOR = (SWRLB_NAMESPACE  + "floor").intern();
      public final static String ROUND = (SWRLB_NAMESPACE  + "round").intern();
      public final static String ROUND_HALF_TO_EVEN = (SWRLB_NAMESPACE  + "roundHalfToEven").intern();
      public final static String SIN = (SWRLB_NAMESPACE  + "sin").intern();
      public final static String COS = (SWRLB_NAMESPACE  + "cos").intern();
      public final static String TAN = (SWRLB_NAMESPACE  + "tan").intern();
      public final static String BOOLEAN_NOT = (SWRLB_NAMESPACE  + "booleanNot").intern();
      public final static String STRING_EQUAL_IGNORE_CASE = (SWRLB_NAMESPACE  + "stringEqualIgnoreCase").intern();
      public final static String STRING_CONCAT = (SWRLB_NAMESPACE  + "stringConcat").intern();
      public final static String SUBSTRING = (SWRLB_NAMESPACE  + "substring").intern();
      public final static String STRING_LENGTH = (SWRLB_NAMESPACE  + "stringLength").intern();
      public final static String NORMALIZE_SPACE = (SWRLB_NAMESPACE  + "normalizeSpace").intern();
      public final static String UPPER_CASE = (SWRLB_NAMESPACE  + "upperCase").intern();
      public final static String LOWER_CASE = (SWRLB_NAMESPACE  + "lowerCase").intern();
      public final static String TRANSLATE = (SWRLB_NAMESPACE  + "translate").intern();
      public final static String CONTAINS = (SWRLB_NAMESPACE  + "contains").intern();
      public final static String CONTAINS_IGNORE_CASE = (SWRLB_NAMESPACE  + "containsIgnoreCase").intern();
      public final static String STARTS_WITH = (SWRLB_NAMESPACE  + "startsWith").intern();
      public final static String ENDS_WITH = (SWRLB_NAMESPACE  + "endsWith").intern();
      public final static String SUBSTRING_BEFORE = (SWRLB_NAMESPACE  + "substringBefore").intern();
      public final static String SUBSTRING_AFTER = (SWRLB_NAMESPACE  + "substringAfter").intern();
      public final static String MATCHES = (SWRLB_NAMESPACE  + "matches").intern();
      public final static String REPLACE = (SWRLB_NAMESPACE  + "replace").intern();
      public final static String TOKENIZE = (SWRLB_NAMESPACE  + "tokenize").intern();
      public final static String YEAR_MONTH_DURATION = (SWRLB_NAMESPACE  + "yearMonthDuration").intern();
      public final static String DAY_TIME_DURATION = (SWRLB_NAMESPACE  + "dayTimeDuration").intern();
      public final static String DATETIME = (SWRLB_NAMESPACE  + "dateTime").intern();
      public final static String DATE = (SWRLB_NAMESPACE  + "date").intern();
      public final static String TIME = (SWRLB_NAMESPACE  + "time").intern();
      public final static String ADD_YEAR_MONTH_DURATIONS = (SWRLB_NAMESPACE  + "addYearMonthDurations").intern();
      public final static String SUBTRACT_YEAR_MONTH_DURATIONS = (SWRLB_NAMESPACE  + "subtractYearMonthDurations").intern();
      public final static String MULTIPLY_YEAR_MONTH_DURATION = (SWRLB_NAMESPACE  + "multiplyYearMonthDuration").intern();
      public final static String DIVIDE_YEAR_MONTH_DURATIONS = (SWRLB_NAMESPACE  + "divideYearMonthDurations").intern();
      public final static String ADD_DAY_TIME_DURATIONS = (SWRLB_NAMESPACE  + "addDayTimeDurations").intern();
      public final static String SUBTRACT_DAY_TIME_DURATIONS = (SWRLB_NAMESPACE  + "subtractDayTimeDurations").intern();
      public final static String MULTIPLY_DAY_TIME_DURATIONS = (SWRLB_NAMESPACE  + "multiplyDayTimeDurations").intern();
      public final static String DIVIDE_DAY_TIME_DURATION = (SWRLB_NAMESPACE  + "divideDayTimeDuration").intern();
      public final static String SUBTRACT_DATES = (SWRLB_NAMESPACE  + "subtractDates").intern();
      public final static String SUBTRACT_TIMES = (SWRLB_NAMESPACE  + "subtractTimes").intern();
      public final static String ADD_YEAR_MONTH_DURATION_TO_DATETIME = (SWRLB_NAMESPACE  + "addYearMonthDurationToDateTime").intern();
      public final static String ADD_DAY_TIME_DURATION_TO_DATETIME = (SWRLB_NAMESPACE  + "addDayTimeDurationToDateTime").intern();
      public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATETIME = (SWRLB_NAMESPACE  + "subtractYearMonthDurationFromDateTime").intern();
      public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATETIME = (SWRLB_NAMESPACE  + "subtractDayTimeDurationFromDateTime").intern();
      public final static String ADD_YEAR_MONTH_DURATION_TO_DATE = (SWRLB_NAMESPACE  + "addYearMonthDurationToDate").intern();
      public final static String ADD_DAY_TIME_DURATION_TO_DATE = (SWRLB_NAMESPACE  + "addDayTimeDurationToDate").intern();
      public final static String SUBTRACT_YEAR_MONTH_DURATION_FROM_DATE = (SWRLB_NAMESPACE  + "subtractYearMonthDurationFromDate").intern();
      public final static String SUBTRACT_DAY_TIME_DURATION_FROM_DATE = (SWRLB_NAMESPACE  + "subtractDayTimeDurationFromDate").intern();
      public final static String ADD_DAY_TIME_DURATION_TO_TIME = (SWRLB_NAMESPACE  + "addDayTimeDurationToTime").intern();
      public final static String SUBTRACT_DAY_TIME_DURATION_FROM_TIME = (SWRLB_NAMESPACE  + "subtractDatTimeDurationFromTime").intern();
      public final static String SUBTRACT_DATETIMES_YIELDING_YEAR_MONTH_DURATION = (SWRLB_NAMESPACE  + "subtractDateTimesYieldingYearMonthDuration").intern();
      public final static String SUBTRACT_DATETIMES_YIELDING_DAY_TIME_DURATION = (SWRLB_NAMESPACE  + "subtractDateTimesYieldingDayTimeDuration").intern();
  }

  public static interface Annotations 
  {    
      public final static String IS_RULE_ENABLED = (SWRLA_NAMESPACE  + "isRuleEnabled").intern();
      public final static String IS_RULE_GROUP_ENABLED = (SWRLA_NAMESPACE  + "isRuleGroupEnabled").intern();
      public final static String RULE_GROUP = (SWRLA_NAMESPACE  + "RuleGroup").intern();
      public final static String HAS_RULE_GROUP = (SWRLA_NAMESPACE  + "hasRuleGroup").intern();
  } // Annotations

} // SWRLNames
