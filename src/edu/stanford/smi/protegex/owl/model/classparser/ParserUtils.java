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

package edu.stanford.smi.protegex.owl.model.classparser;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;


public class ParserUtils {
    public final static String RESTRICTIONS_CAN_USE_BROWSER_TEXT="protege.owl.edit.restrictions.with.browser.text";
    public final static String SINGLE_QUOTE_STRING = "'";
    public final static String[] SUBSTRINGS_REQUIRING_QUOTES = {" ", ";", " ", "#"};
    public final static char[] INITIAL_CHARACTERS_REQUIRING_QUOTES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    private static boolean editUsingBrowserText = ApplicationProperties.getBooleanProperty(RESTRICTIONS_CAN_USE_BROWSER_TEXT, true);
    
    
  public static RDFResource getFrameByName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, RDFResource.class);
  }
  
  public static OWLNamedClass getOWLClassFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, OWLNamedClass.class);
  }
  
  public static RDFProperty getRDFPropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, RDFProperty.class);
  }
  
  public static OWLDatatypeProperty getOWLDatatypePropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, OWLDatatypeProperty.class);
  }
  
  public static OWLObjectProperty getOWLObjectPropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, OWLObjectProperty.class);
  }
  
  public static RDFResource getRDFResourceFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, RDFResource.class);
  }

  public static RDFResource getRDFResourceFromRDFSLabel(OWLModel model, String label) 
  throws AmbiguousNameException {
  	RDFResource resource = null;
  	
    resource = getFrameByRDFSLabel(model, label, RDFResource.class);

    return resource;
  }

  public static RDFSClass getRDFSClassFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, RDFSClass.class);
  }

  public static OWLIndividual getOWLIndividualFromName(OWLModel model, String  name)  
  throws AmbiguousNameException {
    return getFrameByName(model, name, OWLIndividual.class); 
  }
  
  /**
   * Calculates the frame with the given name using the rdfs:label slot as needed.
   * This method either looks for an rdfs property or an rdfs class depending on the is
   * Property parameter.  This method determines whether to use the rdfs:label to look up
   * the frame based on whether 
   * 
   * @param model the owl model
   * @param name the string which is either an rdfs label or a name.
   * @param isProperty if true search for an rdf property, if false look for an rdfs class.  If null
   *            return whatever is found.
   * @throws AmbiguousNameException
   */
  @SuppressWarnings("unchecked")
  private static <X extends RDFResource> X getFrameByName(OWLModel model, 
                                                          String name, 
                                                          Class<? extends X> targetClass) 
  throws AmbiguousNameException { // there is a priority order here as we get more and more desperate
    X resource;
    if ((resource = getFrameByURI(model, name, targetClass)) != null) {
        return resource;
    }
    if ((resource = getFrameByRDFSLabel(model, name, targetClass)) != null) {
        return resource;
    }
    if ((resource = getFrameByBrowserText(model, name, targetClass)) != null) {
        return resource;
    }
    return null;
  }
  
  private static <X extends RDFResource> X getFrameByURI(OWLModel model, 
                                                         String name, 
                                                         Class<? extends X> targetClass) {
      RDFResource resource = (RDFResource) ((KnowledgeBase) model).getFrame(name);
      if (resource != null && resourceCorrectlyTyped(resource, targetClass)) {
        return targetClass.cast(resource);
      }
      else {
        resource = null;
      }
      String fullName = NamespaceUtil.getFullName(model, name);
      if (fullName != null && !name.equals(fullName)) {
          resource = (RDFResource) ((KnowledgeBase) model).getFrame(fullName);
          if (resource != null && resourceCorrectlyTyped(resource, targetClass)) {
            return targetClass.cast(resource);
          }
      }
      return null;
  }
  
  private static <X extends RDFResource> X getFrameByRDFSLabel(OWLModel model, 
                                                               String name, 
                                                               Class<? extends X> targetClass) {
      return getFrameUsingDatatypeProperty(model, name, targetClass, model.getRDFSLabelProperty());
  }
  
  private static <X extends RDFResource> X getFrameByBrowserText(OWLModel model, 
                                                                 String name, 
                                                                 Class<? extends X> targetClass) 
  throws AmbiguousNameException {
      X resource = null;
      Set<RDFProperty> browserSlots = new HashSet<RDFProperty>();
      if (editUsingBrowserText) {
          Project p = model.getProject();
          for (Cls cls : p.getClsesWithDirectBrowserSlots()) {
              if (cls instanceof RDFSNamedClass) {
                  BrowserSlotPattern pattern = cls.getBrowserSlotPattern();
                  Slot slot;
                  if (pattern.isSimple() && ((slot = pattern.getFirstSlot()) instanceof RDFProperty)) {
                      browserSlots.add((RDFProperty) slot);
                  }
              }
          }
          for (RDFProperty property : browserSlots) {
              X newResource = getFrameUsingDatatypeProperty(model, name, targetClass, property);
              if (resource != null && newResource != null) {
                  throw new AmbiguousNameException("Multiple resources share the same name "  + name);
              }
              else {
            	  if (resource == null) {
            		  resource = newResource;
            	  }
              }
          }
      }
      return resource;
  }
  
  private static <X extends RDFResource> X getFrameUsingDatatypeProperty(OWLModel model, 
                                                                 String name, 
                                                                 Class<? extends X> targetClass,
                                                                 RDFProperty property) 
  throws AmbiguousNameException {
      String lang = model.getDefaultLanguage();
      RDFResource resource = null;
      Collection frames = ((KnowledgeBase) model).getFramesWithValue(property, 
                                                                     null, 
                                                                     false, 
                                                                     DefaultRDFSLiteral.getRawValue(name, lang));
      for (Object o : frames) {
          if (resourceCorrectlyTyped((Frame) o, targetClass) && displaysWithProperty(model, (Instance) o, property)) {
              if (resource != null) {
                  throw new AmbiguousNameException("Multiple resources share the same name "  + name);
              }
              resource = (RDFResource) o;
          }
      }
      if (resource != null) {
          return targetClass.cast(resource);
      }
      // if the above failed try again with the null language
      if (lang != null) {
          frames = ((KnowledgeBase) model).getFramesWithValue(property, 
                                                              null, 
                                                              false, 
                                                              DefaultRDFSLiteral.getRawValue(name, (String) null));
          for (Object o : frames) {
              if (resourceCorrectlyTyped((Frame) o, targetClass) && displaysWithProperty(model, (Instance) o, property)) {
                  if (resource != null) {
                      throw new AmbiguousNameException("After falling back to the null language multiple resources share the same name " + name);
                  }
                  resource = (RDFResource) o;
              }
          }
      }
      return targetClass.cast(resource);
  }
  
  private static boolean displaysWithProperty(OWLModel model, Instance i, RDFProperty property) {
	Cls type;
	if (i instanceof OWLIndividual) {
		type = OWLUI.getOneNamedDirectTypeWithBrowserPattern((OWLIndividual) i);
		if (type == null) { return false; }
	} else {
		type = i.getDirectType();
	}
    BrowserSlotPattern bsp = type.getBrowserSlotPattern();
    List<Slot> slots = bsp.getSlots();
    return slots.size() == 1 && slots.contains(property);
  }
  
  @SuppressWarnings("unchecked")
  private static boolean resourceCorrectlyTyped(Frame resource, Class targetClass) {
    if (targetClass == null) {
      return true;
    }
    return targetClass.isAssignableFrom(resource.getClass());
  }
 
  public static String quoteIfNeeded(String id) {
      if (id == null) {
          return null;
      }
      else if (quoteNeeded(id)) {
          return SINGLE_QUOTE_STRING + id + SINGLE_QUOTE_STRING;
      }
      return id;
  }
  
  public static boolean quoteNeeded(String id) {
      if (id.length() == 0) {
          return true;
      }
      for (String bad : SUBSTRINGS_REQUIRING_QUOTES) {
          if (id.contains(bad)) {
              return true;
          }
      }
      char firstChar = id.charAt(0);
      for (char badChar : INITIAL_CHARACTERS_REQUIRING_QUOTES) {
    	  if (firstChar == badChar) {
    		  return true;
    	  }
      }
      return false;
  }
  
  public static String dequoteIdentifier(String id) {
      if (id.startsWith(SINGLE_QUOTE_STRING) && id.endsWith(SINGLE_QUOTE_STRING))  {
          return id.substring(1, id.length() - 1);
      }
      else {
          return id;
      }
  }
  
  public static boolean isLexError(Error e) {
      String clazz = e.getClass().getCanonicalName();
      if (clazz.endsWith("TokenMgrError")) {
          return  true;
      }
      return false;
  }
  
  /**
   * This routine tries a simple approach to splitting the portion of the text
   * String between 0 and pos-1 into the prefix (which hopefully is well formed) and
   * the suffix which needs to be auto-completed.  This routein attempts two approaches.
   * 
   * If there is no quoting action on the strings then it starts working backwards from the last
   * character to find the first character that cannot be part of an unquoted identifier.  Thus
   * for the string
   * 
   *      hasTopping some Chees
   *                           ^
   *                           
   * the routine will work backwards over the letters 's', 'e', 'e', 'h', 'C' and will stop at the space.
   * 
   * In the second case the user is quoting an identifier that needs auto-completion.
   * In this case the routine will work backwards to the last quote symbol.  Thus for the 
   * string
   * 
   *       hasTopping some 'Chees
   *                             ^
   *                             
   * The routine will work backwards to the quote just before the 'C'.
   * 
   * @param text
   */
  public static int findSplittingPoint(String text) {
      if (countQuotes(text) % 2 == 1) {
          int index = text.lastIndexOf(SINGLE_QUOTE_STRING);
          return index;
      }
      int i = text.length() -1;
      while (i >= 0 && isIdChar(text.charAt(i))) {
          i--;
      }
      return i+1;
      
  }
  
  private static int countQuotes(String text) {
      int index = 0;
      int counter = 0;
      while ((index = text.indexOf(SINGLE_QUOTE_STRING, index)) != -1) {
          counter++;
          index++;
      }
      return counter;
  }

  public static boolean isIdChar(char ch) {
      return Character.isJavaIdentifierPart(ch) || ch == ':' || ch == '-';
  }
}
