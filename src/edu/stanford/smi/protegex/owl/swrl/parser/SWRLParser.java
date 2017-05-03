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

// TODO: need to rewrite this using JavaCC to make it more extensible.

package edu.stanford.smi.protegex.owl.swrl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class SWRLParser
{
	public final static char AND_CHAR = '\u2227'; // ^
	public final static char IMP_CHAR = '\u2192'; // >
	public final static char RING_CHAR = '\u02da'; // .
	public final static String delimiters = " ?\n\t()[],\"'" + AND_CHAR + IMP_CHAR + RING_CHAR; // Note space.

	private final OWLModel owlModel;
	private final SWRLFactory swrlFactory;
	private final Set<String> xmlSchemaSymbols = XMLSchemaDatatypes.getSlotSymbols();
	private final Set<String> variables;
	private boolean parseOnly;
	private Tokenizer tokenizer;
	private boolean inHead = false;

	private final Map<String, RDFResource> cachedRDFResources;

	public SWRLParser(OWLModel owlModel)
	{
		this.owlModel = owlModel;
		this.swrlFactory = new SWRLFactory(owlModel);
		this.parseOnly = true;
		this.variables = new HashSet<String>();
		this.cachedRDFResources = new HashMap<String, RDFResource>();
	}

	public void setParseOnly(boolean parseOnly)
	{
		this.parseOnly = parseOnly;
	}

	/**
	 * This parser will throw a {@link SWRLParseException} if it finds errors in the supplied rule. If the rule is correct but incomplete, a
	 * {@link SWRLIncompleteRuleExceptionP (which is a subclass of {@link SWRLParseException}) will be thrown.
	 * 
	 * If {@link #parseOnly} is true, only checking is performed - no SWRL rules are created; if it is false, individuals are created.
	 */
	public SWRLImp parse(String rule) throws SWRLParseException
	{
		return parse(rule, null);
	}

	public SWRLImp parse(String rule, SWRLImp imp) throws SWRLParseException
	{
		String token, message;
		SWRLAtomList head = null, body = null;
		SWRLAtom atom = null;
		boolean atLeastOneAtom = false, justProcessedAtom = true;

		this.inHead = false;

		this.variables.clear();
		this.tokenizer = new Tokenizer(rule.trim());

		if (!this.parseOnly) {
			head = this.swrlFactory.createAtomList();
			head.setInHead(true);
			body = this.swrlFactory.createAtomList();
			head.setInHead(false);
		} 

		if (!this.parseOnly && !this.tokenizer.hasMoreTokens())
			throw new SWRLParseException("Empty rule.");

		do {
			if (justProcessedAtom) {
				if (this.inHead)
					message = "Expecting " + AND_CHAR;
				else
					message = "Expecting " + IMP_CHAR + " or " + AND_CHAR + " or " + RING_CHAR + ".";
			} else {
				if (this.inHead)
					message = "Expecting atom.";
				else
					message = "Expecting atom or " + IMP_CHAR + ".";
			} 

			token = getNextNonSpaceToken(message);

			if (token.equals("" + IMP_CHAR) || token.equals("->")) { // A rule can have an empty body.
				if (this.inHead)
					throw new SWRLParseException("Second occurence of " + IMP_CHAR + ".");
				this.inHead = true;
				justProcessedAtom = false;
			} else if (token.equals("-")) {
				continue; // Ignore "->" while we build up IMP_CHAR.
			} else if (token.equals("" + AND_CHAR) || token.equals("^")) {
				if (!justProcessedAtom)
					throw new SWRLParseException(AND_CHAR + " may occur only after an atom.");
				justProcessedAtom = false;
			} else if (token.equals("" + RING_CHAR) || token.equals(".")) {
				if (this.inHead || !justProcessedAtom)
					throw new SWRLParseException(RING_CHAR + " may occur only in query body.");
				justProcessedAtom = false;
			} else {
				atom = parseAtom(token);
				atLeastOneAtom = true;
				if (!this.parseOnly) {
					if (this.inHead)
						head.append(atom);
					else
						body.append(atom);
				} 
				justProcessedAtom = true;
			} 
		} while (this.tokenizer.hasMoreTokens());

		if (!this.parseOnly) {
			if (!atLeastOneAtom)
				throw new SWRLParseException("Incomplete rule - no antecedent or consequent.");
			if (imp == null)
				imp = this.swrlFactory.createImp(head, body);
			else {
				imp.setHead(head);
				imp.setBody(body);
			} 
		} else
			imp = null;

		return imp;
	}

	/**
	 * If the rule is correct and incomplete return 'true'; if the rule has errors or is correct and complete, return 'false'.
	 */
	public boolean isCorrectAndIncomplete(String rule)
	{
		boolean oldParseOnly = this.parseOnly;
		boolean result = false;

		setParseOnly(true);

		try {
			parse(rule);
		} catch (SWRLParseException e) {
			if (e instanceof SWRLIncompleteRuleException)
				result = true;
		} // catch

		setParseOnly(oldParseOnly);

		return result;
	}

	// public static String getParsableRuleString(String unicodeRule) { return unicodeRule.replace(AND_CHAR, '^').replace(IMP_CHAR, '>'); }

	private SWRLAtom parseAtom(String identifier) throws SWRLParseException
	{
		SWRLAtom atom = null;
		List<RDFObject> enumeratedList = null;
		boolean isEnumeratedList = false;

		if (identifier.startsWith("[")) { // A data range with an enumerated literal list
			enumeratedList = parseDObjectList();
			isEnumeratedList = true;
		} 

		if (isEnumeratedList)
			checkAndSkipToken("(", "Expecting parameters enclosed in parentheses for data range atom.");
		else
			checkAndSkipToken("(", "Expecting parameters enclosed in parentheses for atom " + identifier + ".");

		if (isEnumeratedList)
			atom = parseEnumeratedListParameters(enumeratedList);
		else if (isSameAs(identifier))
			atom = parseSameAsAtomParameters();
		else if (isDifferentFrom(identifier))
			atom = parseDifferentFromAtomParameters();
		else if (isOWLClassName(identifier))
			atom = parseClassAtomParameters(identifier);
		else if (isOWLObjectPropertyName(identifier))
			atom = parseIndividualPropertyAtomParameters(identifier);
		else if (isOWLDatatypePropertyName(identifier))
			atom = parseDatavaluedPropertyAtomParameters(identifier);
		else if (isSWRLBuiltinName(identifier))
			atom = parseBuiltinParameters(identifier);
		else
			throw new SWRLParseException("Invalid atom name " + identifier + ".");

		return atom;
	}

	private void checkAndSkipToken(String skipToken, String unexpectedTokenMessage) throws SWRLParseException
	{
		String token = getNextNonSpaceToken(unexpectedTokenMessage);

		if (!token.equalsIgnoreCase(skipToken))
			throw new SWRLParseException("Expecting " + skipToken + ", got " + token + "; " + unexpectedTokenMessage);
	}

	// TODO: Does not deal with escaped quotation characters.
	private String getNextStringToken(String noTokenMessage) throws SWRLParseException
	{
		String token = "";
		String errorMessage = "Incomplete rule. " + noTokenMessage;

		if (!this.tokenizer.hasMoreTokens()) {
			if (this.parseOnly)
				throw new SWRLIncompleteRuleException(errorMessage);
			else
				throw new SWRLParseException(errorMessage);
		}

		while (this.tokenizer.hasMoreTokens()) {
			token = this.tokenizer.nextToken("\"");
			if (token.equals("\""))
				token = ""; // Empty string
			else
				checkAndSkipToken("\"", "Expected \" to close string.");
			return token;
		}

		if (this.parseOnly)
			throw new SWRLIncompleteRuleException(errorMessage);
		else
			throw new SWRLParseException(errorMessage); // Should not get here
	}

	private String getNextNonSpaceToken(String noTokenMessage) throws SWRLParseException
	{
		String token = "";
		String errorMessage = "Incomplete rule. " + noTokenMessage;

		if (!this.tokenizer.hasMoreTokens()) {
			if (this.parseOnly)
				throw new SWRLIncompleteRuleException(errorMessage);
			else
				throw new SWRLParseException(errorMessage);
		}

		while (this.tokenizer.hasMoreTokens()) {
			token = this.tokenizer.nextToken();
			if (!(token.equals(" ") || token.equals("\n") || token.equals("\t")))
				return token;
		}

		if (this.parseOnly)
			throw new SWRLIncompleteRuleException(errorMessage);
		else
			throw new SWRLParseException(errorMessage); // Should not get here
	}

	private SWRLAtom parseSameAsAtomParameters() throws SWRLParseException
	{
		RDFResource iObject1, iObject2;
		SWRLAtom atom = null;

		iObject1 = parseIObject();
		checkAndSkipToken(",", "Expecting comma-separated second parameter for SameAsAtom.");
		iObject2 = parseIObject();

		if (!this.parseOnly)
			atom = this.swrlFactory.createSameIndividualAtom(iObject1, iObject2);

		checkAndSkipToken(")", "Expecting closing parenthesis after second parameters in SameAsAtom");

		return atom;
	}

	private SWRLAtom parseDifferentFromAtomParameters() throws SWRLParseException
	{
		RDFResource iObject1, iObject2;
		SWRLAtom atom = null;

		iObject1 = parseIObject();
		checkAndSkipToken(",", "Expecting comma-separated second parameters for DifferentFromAtom");
		iObject2 = parseIObject();

		if (!this.parseOnly)
			atom = this.swrlFactory.createDifferentIndividualsAtom(iObject1, iObject2);

		checkAndSkipToken(")", "Only two parameters allowed for DifferentFromAtom");

		return atom;
	}

	private SWRLAtom parseClassAtomParameters(String identifier) throws SWRLParseException
	{
		RDFResource iObject;
		SWRLAtom atom = null;

		iObject = parseIObject();

		if (!this.parseOnly) {
			OWLNamedClass aClass = ParserUtils.getOWLClassFromName(this.owlModel, identifier);
			atom = this.swrlFactory.createClassAtom(aClass, iObject);
		} 

		checkAndSkipToken(")", "Expecting closing parenthesis for parameter for ClassAtom '" + identifier + "'.");

		return atom;
	}

	private SWRLAtom parseIndividualPropertyAtomParameters(String identifier) throws SWRLParseException
	{
		RDFResource iObject1, iObject2;
		SWRLAtom atom = null;

		iObject1 = parseIObject();
		checkAndSkipToken(",", "Expecting comma-separated second parameter for IndividualPropertyAtom '" + identifier + "'");
		iObject2 = parseIObject();

		if (!this.parseOnly) {
			OWLObjectProperty objectProperty = ParserUtils.getOWLObjectPropertyFromName(this.owlModel, identifier);
			if (objectProperty == null)
				throw new SWRLParseException("no datatype property found for IndividualPropertyAtom: " + identifier);
			atom = this.swrlFactory.createIndividualPropertyAtom(objectProperty, iObject1, iObject2);
		} 

		checkAndSkipToken(")", "Expecting closing parenthesis after second parameter of IndividualPropertyAtom '" + identifier + "'.");

		return atom;
	}

	private SWRLAtom parseDatavaluedPropertyAtomParameters(String identifier) throws SWRLParseException
	{
		RDFResource iObject;
		RDFObject dObject;
		SWRLAtom atom = null;
		OWLDatatypeProperty datatypeProperty;
		String token, errorMessage = "Expecting literal qualification symbol '#' or closing parenthesis after second parameter of DatavaluedPropertyAtom' ";

		iObject = parseIObject();
		checkAndSkipToken(",", "Expecting comma-separated second parameter for DatavaluedPropertyAtom '" + identifier + "'.");
		dObject = parseDObject();

		token = getNextNonSpaceToken(errorMessage + identifier + "'.");

		if (token.equals("#")) { // Literal qualification.
			token = getNextNonSpaceToken("Expecting XML Schema datatype.");
			if (this.tokenizer.hasMoreTokens() && !isXSDDatatype(token))
				throw new SWRLParseException("Invalid XML Schema datatype name: '" + token + "'.");
			if (!this.parseOnly)
				dObject = this.owlModel.createRDFSLiteral(dObject.getBrowserText(), token);
			checkAndSkipToken(")", "Expecting closing parenthesis after second parameter of DatavaluedPropertyAtom");
		} else if (!token.equals(")"))
			throw new SWRLParseException(errorMessage + identifier + "'.");

		if (!this.parseOnly) {
			datatypeProperty = ParserUtils.getOWLDatatypePropertyFromName(this.owlModel, identifier);
			atom = this.swrlFactory.createDatavaluedPropertyAtom(datatypeProperty, iObject, dObject);
		} 

		return atom;
	}

	private SWRLAtom parseBuiltinParameters(String identifier) throws SWRLParseException
	{
		SWRLBuiltin builtin;
		SWRLAtom atom = null;
		List<RDFObject> objects = parseObjectList(); // Swallows ')'

		if (!this.parseOnly) {
			builtin = this.swrlFactory.getBuiltin(NamespaceUtil.getFullName(this.owlModel, identifier));
			atom = this.swrlFactory.createBuiltinAtom(builtin, objects.iterator());
		} 

		return atom;
	}

	private SWRLAtom parseEnumeratedListParameters(List<RDFObject> enumeratedList) throws SWRLParseException
	{
		RDFObject dObject;
		SWRLAtom atom = null;
		Object literalValue;
		Iterator<RDFObject> iterator;

		dObject = parseDObject();

		if (!this.parseOnly) {
			OWLDataRange dataRange = this.owlModel.createOWLDataRange();
			RDFProperty oneOfProperty = this.owlModel.getOWLOneOfProperty();

			iterator = enumeratedList.iterator();
			while (iterator.hasNext()) {
				literalValue = iterator.next();
				dataRange.addPropertyValue(oneOfProperty, literalValue);
			} // while
			atom = this.swrlFactory.createDataRangeAtom(dataRange, dObject);
		} 
		checkAndSkipToken(")", "Expecting closing parenthesis after parameter in DataRangeAtom.");

		return atom;
	}

	// Parse a list of variables and literals.
	private List<RDFObject> parseDObjectList() throws SWRLParseException
	{
		RDFObject dObject;
		List<RDFObject> dObjects = null;

		if (!this.parseOnly)
			dObjects = new ArrayList<RDFObject>();

		dObject = parseDObject();
		if (!this.parseOnly)
			dObjects.add(dObject);

		String token = getNextNonSpaceToken("Expecting additional comma-separated variables or literals or closing parenthesis.");
		while (token.equals(",")) {
			dObject = parseDObject();
			if (!this.parseOnly)
				dObjects.add(dObject);
			token = getNextNonSpaceToken("Expecting ',' or ')'.");
			if (!(token.equals(",") || token.equals(")")))
				throw new SWRLParseException("Expecting ',' or ')', got '" + token + "'.");
		} 
		return dObjects;
	}

	// Parse a list of variables, literals and individual names.
	private List<RDFObject> parseObjectList() throws SWRLParseException
	{
		RDFObject object;
		List<RDFObject> objects = null;

		if (!this.parseOnly)
			objects = new ArrayList<RDFObject>();

		object = parseObject();
		if (!this.parseOnly)
			objects.add(object);

		String token = getNextNonSpaceToken("Expecting additional comma-separated variables, literals or individual names or closing parenthesis.");
		while (token.equals(",")) {
			object = parseObject();
			if (!this.parseOnly)
				objects.add(object);
			token = getNextNonSpaceToken("Expecting ',' or ')'.");
			if (!(token.equals(",") || token.equals(")")))
				throw new SWRLParseException("Expecting ',' or ')', got '" + token + "'.");
		}
		return objects;
	}

	// Parse a variable, literal or an individual name.
	private RDFObject parseObject() throws SWRLParseException
	{
		RDFObject parsedEntity = null;
		String parsedString = getNextNonSpaceToken("Expecting variable or individual name or literal.");

		if (parsedString.equals("?"))
			parsedEntity = parseVariable();
		else { // The entity is an individual name or literal
			if (isValidIndividualName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLIndividual(parsedString);
			} else if (isValidOWLNamedClassName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLNamedClass(parsedString);
			} else if (isValidOWLPropertyName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLProperty(parsedString);
			} else
				parsedEntity = parseLiteral(parsedString);
		}

		return parsedEntity;
	}

	// Parse a variable or an individual name. For SWRL Full, also allow class and property names.
	private RDFResource parseIObject() throws SWRLParseException
	{
		RDFResource parsedEntity = null;
		String parsedString = getNextNonSpaceToken("Expecting variable or individual name.");

		if (parsedString.equals("?"))
			parsedEntity = parseVariable();
		else { // The entity is an
			if (isValidIndividualName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLIndividual(parsedString);
			} else if (isValidOWLNamedClassName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLNamedClass(parsedString);
			} // SWRL Full
			else if (isValidOWLPropertyName(parsedString)) {
				if (!this.parseOnly)
					parsedEntity = getOWLProperty(parsedString);
			} // SWRL Full
			else if (this.tokenizer.hasMoreTokens())
				throw new SWRLParseException("Invalid entity name: '" + parsedString + "'.");
		}
		return parsedEntity;
	}

	// Parse a variable or a literal.
	private RDFObject parseDObject() throws SWRLParseException
	{
		RDFObject parsedEntity = null;
		String parsedString = getNextNonSpaceToken("Expecting variable or literal.");

		if (parsedString.equals("?"))
			parsedEntity = parseVariable();
		else
			parsedEntity = parseLiteral(parsedString);

		return parsedEntity;
	}

	private RDFResource parseVariable() throws SWRLParseException
	{
		RDFResource parsedEntity = null;
		String variableName = getNextNonSpaceToken("Expected variable name");
		checkThatVariableNameIsValid(variableName);

		if (this.tokenizer.hasMoreTokens()) {
			if (!this.inHead)
				this.variables.add(variableName);
			else if (!this.variables.contains(variableName))
				throw new SWRLParseException("Variable ?" + variableName + " referred to in consequent is not present in antecedent.");
		}

		if (!this.parseOnly)
			parsedEntity = getSWRLVariable(variableName);

		return parsedEntity;
	}

	private RDFObject parseLiteral(String parsedString) throws SWRLParseException
	{
		RDFObject parsedEntity = null;

		if (parsedString.equals("\"")) { // The parsed entity is a string
			String stringValue = getNextStringToken("Expected a string.");
			if (!this.parseOnly)
				parsedEntity = this.owlModel.createRDFSLiteral(stringValue, this.owlModel.getXSDstring());
		}
		// According to the XSD spec, xsd:boolean's have the lexical space: {true, false, 1, 0}. We don't allow {1, 0} since these are parsed
		// as XSDints.
		else if (parsedString.startsWith("t") || parsedString.startsWith("T") || parsedString.startsWith("f") || parsedString.startsWith("F")) {
			if (this.tokenizer.hasMoreTokens()) {
				if (parsedString.equalsIgnoreCase("true") || parsedString.equalsIgnoreCase("false")) {
					if (!this.parseOnly)
						parsedEntity = this.owlModel.createRDFSLiteral(parsedString, this.owlModel.getXSDboolean());
				} else
					throw new SWRLParseException("Invalid literal " + parsedString + ".");
			}
		} else { // Is it an integer, float, long or double then?
			try {
				if (parsedString.contains(".")) {
					Float.parseFloat(parsedString); // Check it
					if (!this.parseOnly)
						parsedEntity = this.owlModel.createRDFSLiteral(parsedString, this.owlModel.getXSDfloat());
				} else {
					Integer.parseInt(parsedString); // Check it
					if (!this.parseOnly)
						parsedEntity = this.owlModel.createRDFSLiteral(parsedString, this.owlModel.getXSDint());
				}
			} catch (NumberFormatException e) {
				String errorMessage = "Invalid literal " + parsedString + ".";
				if (this.parseOnly)
					throw new SWRLIncompleteRuleException(errorMessage);
				else
					throw new SWRLParseException(errorMessage);
			}
		}

		return parsedEntity;
	}

	private boolean isSameAs(String identifier) throws SWRLParseException
	{
		return identifier.equalsIgnoreCase("sameAs");
	}

	private boolean isDifferentFrom(String identifier) throws SWRLParseException
	{
		return identifier.equalsIgnoreCase("differentFrom");
	}

	private boolean isXSDDatatype(String identifier) throws SWRLParseException
	{
		return (identifier.startsWith("xsd:") && this.xmlSchemaSymbols.contains(identifier.substring(4)));
	}

	private void checkThatIdentifierIsValid(String identifier) throws SWRLParseException
	{
		if (!isValidIdentifier(identifier))
			throw new SWRLParseException("Invalid identifier " + identifier + ".");
	}

	// Possible valid identifiers include 'http://swrl.stanford.edu/ontolgies/built-ins/3.3/swrlx.owl#createIndividual'.
	private boolean isValidIdentifier(String s)
	{
		if (s.length() == 0)
			return false;

		if (!Character.isJavaIdentifierStart(s.charAt(0)) && s.charAt(0) != ':')
			return false; // HACK to deal with ":TO" and ":FROM".

		for (int i = 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!(Character.isJavaIdentifierPart(c) || c == ':' || c == '-' || c == '#' || c == '/' || c == '.')) {
				return false;
			} 
		} 
		return true;
	}

	private void checkThatVariableNameIsValid(String variableName) throws SWRLParseException
	{
		checkThatIdentifierIsValid(variableName);

		RDFResource resource = getRDFResource(variableName);

		if ((resource != null) && !(resource instanceof SWRLVariable))
			throw new SWRLParseException("Invalid variable name " + variableName + ". Cannot use name of existing OWL class, property, or individual.");
	}

	private boolean isOWLClassName(String identifier) throws SWRLParseException
	{
		return ParserUtils.getOWLClassFromName(this.owlModel, identifier) != null;
	}

	private boolean isOWLObjectPropertyName(String identifier) throws SWRLParseException
	{
		return ParserUtils.getOWLObjectPropertyFromName(this.owlModel, identifier) != null;
	}

	private boolean isOWLDatatypePropertyName(String identifier) throws SWRLParseException
	{
		return ParserUtils.getOWLDatatypePropertyFromName(this.owlModel, identifier) != null;
	}

	private boolean isSWRLBuiltinName(String identifier) throws SWRLParseException
	{
		RDFResource resource = getRDFResource(identifier);
		return resource != null && resource.getProtegeType().getName().equals(SWRLNames.Cls.BUILTIN);
	}

	private boolean isValidIndividualName(String name) throws SWRLParseException
	{
		try {
			return ParserUtils.getOWLIndividualFromName(this.owlModel, name) != null;
		} catch (Throwable t) {
			return false;
		}
	}

	private boolean isValidOWLNamedClassName(String name) throws SWRLParseException
	{
		try {
			RDFResource resource = getRDFResource(name);

			if (resource == null)
				return false;

			if (resource instanceof OWLNamedClass)
				return true;
			else
				return false;
		} catch (Throwable t) {
			return false;
		} // try
	}

	private boolean isValidOWLPropertyName(String name) throws SWRLParseException
	{
		try {
			RDFResource resource = getRDFResource(name);

			if (resource == null)
				return false;

			if (resource instanceof OWLProperty)
				return true;
			else
				return false;
		} catch (Throwable t) {
			return false;
		}
	}

	private OWLIndividual getOWLIndividual(String name) throws SWRLParseException
	{
		OWLIndividual resource = ParserUtils.getOWLIndividualFromName(this.owlModel, name);

		if (resource == null)
			throw new SWRLParseException(name + " is not a valid individual name");

		return resource;
	}

	private OWLNamedClass getOWLNamedClass(String name) throws SWRLParseException
	{
		OWLNamedClass resource = ParserUtils.getOWLClassFromName(this.owlModel, name);

		if (resource == null)
			throw new SWRLParseException(name + " is not a valid class name");

		return resource;
	}

	private OWLProperty getOWLProperty(String name) throws SWRLParseException
	{
		RDFProperty resource = ParserUtils.getRDFPropertyFromName(this.owlModel, name);

		if (resource == null || !(resource instanceof OWLProperty))
			throw new SWRLParseException(name + " is not a valid property name");

		return (OWLProperty)resource;
	}

	private SWRLVariable getSWRLVariable(String name) throws SWRLParseException
	{
		RDFResource resource = this.owlModel.getRDFResource(NamespaceUtil.getFullName(this.owlModel, name));

		if (resource instanceof SWRLVariable)
			return (SWRLVariable)resource;
		else if (resource == null)
			return this.swrlFactory.createVariable(NamespaceUtil.getFullName(this.owlModel, name));
		else
			throw new SWRLParseException(name + " cannot be used as a variable name");
	}

	private RDFResource getRDFResource(String resourceName)
	{
		RDFResource resource;

		if (this.parseOnly) {
			if (this.cachedRDFResources.containsKey(resourceName))
				resource = this.cachedRDFResources.get(resourceName);
			else {
				resource = ParserUtils.getRDFResourceFromName(this.owlModel, resourceName);
				this.cachedRDFResources.put(resourceName, resource); // May be null
			} 
		} else
			resource = ParserUtils.getRDFResourceFromName(this.owlModel, resourceName);

		return resource;
	}

	private static class Tokenizer
	{
		private final StringTokenizer internalTokenizer;

		public Tokenizer(String input)
		{
			this.internalTokenizer = new StringTokenizer(input, delimiters, true);
		}

		public boolean hasMoreTokens()
		{
			return this.internalTokenizer.hasMoreTokens();
		}

		public String nextToken(String myDelimiters)
		{
			return this.internalTokenizer.nextToken(myDelimiters);
		}

		public String nextToken() throws NoSuchElementException
		{
			String token = this.internalTokenizer.nextToken(delimiters);
			if (!token.equals("'"))
				return token;

			StringBuffer buffer = new StringBuffer();
			while (this.internalTokenizer.hasMoreTokens() && !(token = this.internalTokenizer.nextToken()).equals("'")) {
				buffer.append(token);
			}
			return buffer.toString();
		}
	}
}
