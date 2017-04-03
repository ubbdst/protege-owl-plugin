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

package edu.stanford.smi.protegex.owl.javacode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

/**
 * A class that can create Java interfaces in the Protege-OWL format from an OWL model.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JavaCodeGenerator {

    private OWLModel owlModel;

    private JavaCodeGeneratorOptions options;

    private static Map<Class, Class> primitiveTypesMap = new HashMap<Class, Class>();

    private static final boolean TRANSITIVE = true;
    private static final boolean ALL_PROPERTIES = TRANSITIVE;
    private static final boolean ONLY_LOCAL_PROPERTIES = ! TRANSITIVE;

    static {
        primitiveTypesMap.put(Integer.class, int.class);
        primitiveTypesMap.put(Float.class, float.class);
        primitiveTypesMap.put(Boolean.class, boolean.class);
    }


    public JavaCodeGenerator(OWLModel owlModel, JavaCodeGeneratorOptions options) {
        this.owlModel = owlModel;
        this.options = options;
        File folder = options.getOutputFolder();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        String pack = options.getPackage();
        if (pack != null) {
            pack = pack.replace('.', '/');
            File file = folder == null ? new File(pack) : new File(folder, pack);
            file.mkdirs();
            File f = new File(file, "impl");
            f.mkdirs();
        }
        else {
            File file = folder == null ? new File("impl") : new File(folder, "impl");
            file.mkdirs();
        }
    }


    public void createAll() throws IOException {
        for (Iterator it = owlModel.getUserDefinedRDFSNamedClasses().iterator(); it.hasNext();) {
            RDFSNamedClass aClass = (RDFSNamedClass) it.next();

            // Filter out SWRL classes
            // Is there a better way of filtering out the SWRL classes?
            if (!aClass.getNamespace().equals(SWRLNames.SWRL_NAMESPACE)) {
            	createInterface(aClass);
            	createImplementation(aClass);
            }
        }
        createFactoryClass();
    }


    public void createFactoryClass() throws IOException {
        String localName = options.getFactoryClassName();
        if (localName != null && localName.length() > 0) {
            File file = getInterfaceFile(localName);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printFactoryClass(printWriter);
            fileWriter.close();
        }
    }


    private void printFactoryClass(PrintWriter printWriter) {
        printInterfacePackageStatement(printWriter);
        printWriter.println();
        if (options.getPackage() != null) {
            printWriter.println("import " + options.getPackage() + ".impl.*;");
        }
        else {
            printWriter.println("import impl;");
        }
        printWriter.println();
        printWriter.println("import edu.stanford.smi.protege.model.FrameID;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.*;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;");
        printWriter.println("import edu.stanford.smi.protegex.owl.javacode.ProtegeJavaMapping;");
        printWriter.println();
        printWriter.println("import java.util.*;");

        printWriter.println();
        printWriter.println("/**");
        printWriter.println(" * Generated by Protege-OWL  (http://protege.stanford.edu/plugins/owl).");
        printWriter.println(" *");
        printWriter.println(" * @version generated on " + new Date());
        printWriter.println(" */");
        printWriter.println("public class " + options.getFactoryClassName() + " {");
        printWriter.println();
        printWriter.println("    private OWLModel owlModel;");
        printWriter.println();
        printWriter.println("    static {");
        for (Object o : owlModel.getUserDefinedRDFSNamedClasses()) {
            RDFSNamedClass clazz = (RDFSNamedClass) o;
            printWriter.println("        ProtegeJavaMapping.add(\"" + clazz.getName() + "\", "
                                            + getInterfaceName(clazz) + ".class, "
                                            + getImplementationName(clazz) + ".class);");
        }
        printWriter.println("    }");
        printWriter.println();
        printWriter.println("    public " + options.getFactoryClassName() + "(OWLModel owlModel) {");
        printWriter.println("        this.owlModel = owlModel;");
        printWriter.println("    }");
        printWriter.println();
        printWriter.println("    public <X> X create(Class<? extends X> javaInterface, String name) {");
        printWriter.println("        return ProtegeJavaMapping.create(owlModel, javaInterface, name);");
        printWriter.println("    }");
        for (Iterator it = owlModel.getUserDefinedRDFSNamedClasses().iterator(); it.hasNext();) {
            RDFSNamedClass cls = (RDFSNamedClass) it.next();
            if (!cls.getNamespace().equals(SWRLNames.SWRL_NAMESPACE)) {
	            String interfaceName = getInterfaceName(cls);
	            String implementationName = getImplementationName(cls);
	            printWriter.println();
	            printWriter.println();
	            printWriter.println("    public RDFSNamedClass get" + interfaceName + "Class() {");
	            printWriter.println("        final String uri = \"" + cls.getURI() + "\";");
	            printWriter.println("        final String name = owlModel.getResourceNameForURI(uri);");
	            printWriter.println("        return owlModel.getRDFSNamedClass(name);");
	            printWriter.println("    }");
	            printWriter.println();
	            printWriter.println("    public " + interfaceName + " create" + interfaceName + "(String name) {");
	            printWriter.println("        final RDFSNamedClass cls = get" + interfaceName + "Class();");
	            printWriter.println("        if (name == null) {");
	            printWriter.println("            name = owlModel.getNextAnonymousResourceName();");
	            printWriter.println("        }");
	            printWriter.println("        return  new " + implementationName + "(owlModel, cls.createInstance(name).getFrameID());");
	            printWriter.println("    }");
	            printWriter.println();
	            printWriter.println("    public " + interfaceName + " get" + interfaceName + "(String name) {");
	            printWriter.println("        RDFResource res = owlModel.getRDFResource(OWLUtil.getInternalFullName(owlModel, name));");
	            printWriter.println("        if (res == null) {return null;}");
	            printWriter.println("        if (res instanceof " + interfaceName + ") {");
	            printWriter.println("            return (" + interfaceName + ") res;");
	            printWriter.println("        } else if (res.hasProtegeType(get"+ interfaceName + "Class())) {");
	            printWriter.println("            return new " + implementationName + "(owlModel, res.getFrameID());");
	            printWriter.println("        }");
	            printWriter.println("        return null;");
	            printWriter.println("    }");

	            String collectionType  = options.getSetMode() ? "Set" : "Collection";
	            String collectionClass = options.getSetMode() ? "HashSet" : "ArrayList";

	            printWriter.println();
	            printWriter.println("    public " + collectionType + "<" + interfaceName + "> getAll" + interfaceName + "Instances() {");
	            printWriter.println("        return getAll" + interfaceName + "Instances(false);");
	            printWriter.println("    }");
	            printWriter.println();
	            printWriter.println("    public " + collectionType + "<" + interfaceName + "> getAll" + interfaceName + "Instances(boolean transitive) {");
	            printWriter.println("        " + collectionType + "<" + interfaceName + "> result = new " + collectionClass + "<" + interfaceName + ">();");
	            printWriter.println("        final RDFSNamedClass cls = get" + interfaceName + "Class();");
	            printWriter.println("        RDFResource owlIndividual;");
	            printWriter.println("        for (Iterator it = cls.getInstances(transitive).iterator();it.hasNext();) {");
	            printWriter.println("            owlIndividual = (RDFResource) it.next();");
	            printWriter.println("            result.add(new " + implementationName + "(owlModel, owlIndividual.getFrameID()));");
	            printWriter.println("        }");
	            printWriter.println("        return result;");
	            printWriter.println("    }");
            }
        }

        for (Iterator it = owlModel.getUserDefinedRDFProperties().iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (!property.getNamespace().equals(SWRLNames.SWRL_NAMESPACE)) {
                RDFPropertyCode propertyCode = new RDFPropertyCode(property, options.getPrefixMode());
                String name = propertyCode.getUpperCaseJavaName();

	            printWriter.println();
	            printWriter.println();
                printWriter.println("    public RDFProperty get" + name + "Property() {");
                printWriter.println("        final String uri = \"" + property.getURI() + "\";");
                printWriter.println("        final String name = owlModel.getResourceNameForURI(uri);");
                printWriter.println("        return owlModel.getRDFProperty(name);");
                printWriter.println("    }");
            }
        }

        printWriter.println("}");
    }

    public void createImplementation(RDFSNamedClass cls) throws IOException {

        File file = getImplementationFile(cls);
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printImplementationCode(printWriter, cls);
        fileWriter.close();

        if (options.getAbstractMode()) {
            createUserImplementation(cls);
        }
    }


    public void createUserImplementation(RDFSNamedClass cls) throws IOException {
        String localName = getImplementationName(cls);
        File file = getImplementationFile(localName);
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printUserImplementationCode(printWriter, cls);
            fileWriter.close();
        }
    }


    public void printImplementationCode(PrintWriter printWriter, RDFSNamedClass aClass) {
        RDFSClassCode code = new RDFSClassCode(aClass, options.getPrefixMode());
        boolean considerAllProperties = hasMultipleSuperclasses(aClass) ? ALL_PROPERTIES : ONLY_LOCAL_PROPERTIES;
        if (options.getPackage() != null) {
            printWriter.println("package " + options.getPackage() + ".impl;");
        }
        else {
            printWriter.println("package impl;");
        }
        printWriter.println();
        printWriter.println("import edu.stanford.smi.protege.model.FrameID;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.*;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.impl.*;");

        printWriter.println("import edu.stanford.smi.protegex.owl.javacode.AbstractCodeGeneratorIndividual;");

        perhapsAddImportJavaUtil(code, printWriter, considerAllProperties);
        String pack = options.getPackage();
        if (pack != null) {
        	printWriter.println("import " + pack + "." + getInterfaceNamePossiblyAbstract(aClass) + ";");
            printWriter.println("import " + pack + ".*;");
            printWriter.println();
        }
        printWriter.println("/**");
        printWriter.println(" * Generated by Protege-OWL  (http://protege.stanford.edu/plugins/owl).");
        printWriter.println(" * Source OWL Class: " + aClass.getURI());
        printWriter.println(" *");
        printWriter.println(" * @version generated on " + new Date());
        printWriter.println(" */");
        String implementationName = getImplementationNamePossiblyAbstract(aClass);
        printWriter.println("public class " + implementationName + getImplementationExtendsCode(aClass));
        printWriter.println("         implements " + getInterfaceNamePossiblyAbstract(aClass) + " {");
        printConstructors(printWriter, implementationName);
        for (Iterator it = code.getPropertyCodes(considerAllProperties).iterator(); it.hasNext();) {
            printWriter.println();
            printWriter.println();
            RDFPropertyAtClassCode propertyCode = (RDFPropertyAtClassCode) it.next();
            printImplementationPropertyCode(printWriter, propertyCode);
        }
        printWriter.println("}");
    }


    public void printUserImplementationCode(PrintWriter printWriter, RDFSNamedClass cls) {
        if (options.getPackage() != null) {
            printWriter.println("package " + options.getPackage() + ".impl;");
        }
        else {
            printWriter.println("package impl;");
        }
        printWriter.println();
        printWriter.println("import edu.stanford.smi.protege.model.FrameID;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.*;");
        printWriter.println("import edu.stanford.smi.protegex.owl.model.impl.*;");
        printWriter.println("import edu.stanford.smi.protegex.owl.javacode.AbstractCodeGeneratorIndividual;");

        String pack = options.getPackage();
        if (pack != null) {
            printWriter.println("import " + pack + ".*;");
            printWriter.println();
        }
        String implementationName = getImplementationName(cls);
        printWriter.println("public class " + implementationName + " extends " + getImplementationNamePossiblyAbstract(cls));
        printWriter.println("         implements " + getInterfaceName(cls) + " {");
        printConstructors(printWriter, implementationName);
        printWriter.println("}");
    }


    private void printConstructors(PrintWriter printWriter, String implementationName) {
        printWriter.println();
        printWriter.println("    public " + implementationName + "(OWLModel owlModel, FrameID id) {");
        printWriter.println("        super(owlModel, id);");
        printWriter.println("    }");
        printWriter.println();
        printWriter.println();
        printWriter.println("    public " + implementationName + "() {");
        printWriter.println("    }");
    }


    private String getImplementationExtendsCode(RDFSNamedClass aClass) {
        String str = " extends ";
        String base = getBaseImplementation(aClass);
        if (base == null) {
            return str + "AbstractCodeGeneratorIndividual";
        }
        else {
            return str + base;
        }
    }


    public void createInterface(RDFSNamedClass aClass) throws IOException {

        File baseFile = getInterfaceFile(aClass);
        FileWriter fileWriter = new FileWriter(baseFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printInterfaceCode(printWriter, aClass);
        fileWriter.close();

        if (options.getAbstractMode()) {
            createUserInterface(aClass);
        }
    }


    private void createUserInterface(RDFSNamedClass aClass) throws IOException {
        String localName = getInterfaceName(aClass);
        File file = getInterfaceFile(localName);
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printUserInterfaceCode(printWriter, aClass);
            fileWriter.close();
        }
    }


    public void printInterfaceCode(PrintWriter printWriter, RDFSNamedClass aClass) {
        RDFSClassCode code = new RDFSClassCode(aClass, options.getPrefixMode());
        printInterfacePackageStatement(printWriter);
        printWriter.println("import edu.stanford.smi.protegex.owl.model.*;");
        printWriter.println();
        perhapsAddImportJavaUtil(code, printWriter, ONLY_LOCAL_PROPERTIES);
        printWriter.println("/**");
        printWriter.println(" * Generated by Protege-OWL  (http://protege.stanford.edu/plugins/owl).");
        printWriter.println(" * Source OWL Class: " + aClass.getURI());
        printWriter.println(" *");
        printWriter.println(" * @version generated on " + new Date());
        printWriter.println(" */");
        printWriter.println("public interface " + getInterfaceNamePossiblyAbstract(aClass) + getInterfaceExtendsCode(aClass) + " {");
        for (Iterator it = code.getPropertyCodes(ONLY_LOCAL_PROPERTIES).iterator(); it.hasNext();) {
            RDFPropertyAtClassCode propertyCode = (RDFPropertyAtClassCode) it.next();
            createInterfacePropertyCode(printWriter, propertyCode);
            if (it.hasNext()) {
                printWriter.println();
            }
        }
        printWriter.println("}");
    }


    private void printInterfacePackageStatement(PrintWriter printWriter) {
        if (options.getPackage() != null) {
            printWriter.println("package " + options.getPackage() + ";");
            printWriter.println();
        }
    }


    public void printUserInterfaceCode(PrintWriter printWriter, RDFSNamedClass cls) {
        printInterfacePackageStatement(printWriter);
        printWriter.println("public interface " + getInterfaceName(cls) + " extends " + getInterfaceNamePossiblyAbstract(cls) + " {");
        printWriter.println("}");
    }


    private String getInterfaceExtendsCode(RDFSNamedClass aClass) {
        String str = " extends ";
        String base = getBaseInterface(aClass);
        if (base == null) {
            return str + "RDFIndividual";
        }
        else {
            return str + base;
        }
    }


    public void printImplementationPropertyCode(PrintWriter printWriter, RDFPropertyAtClassCode propertyCode) {
        RDFProperty property = propertyCode.getRDFProperty();
        String name = propertyCode.getUpperCaseJavaName();
        String javaTypeName = getJavaTypeName(propertyCode);
        String simpleJavaTypeName = propertyCode.getJavaType();
        boolean multiple = propertyCode.isMultiple();

        String getProperty = "get" + name + "Property()";

        printWriter.println();
        printWriter.println("    // Property " + property.getURI());
        printWriter.println();
        printWriter.println("    public " + javaTypeName + " get" + name + "() {");
        if (multiple) {
            if (options.getSetMode()) {
                if (propertyCode.isCustomType()) {
                    printWriter.println("        return new HashSet(getPropertyValuesAs(" + getProperty + ", " + simpleJavaTypeName + ".class));");
                }
                else {
                    printWriter.println("        return new HashSet(getPropertyValues(" + getProperty + "));");
                }
            }
            else {
                if (propertyCode.isCustomType()) {
                    printWriter.println("        return getPropertyValuesAs(" + getProperty + ", " + simpleJavaTypeName + ".class);");
                }
                else {
                    printWriter.println("        return getPropertyValues(" + getProperty + ");");
                }
            }
        }
        else {
            if (propertyCode.isPrimitive()) {
                String x = "" + Character.toUpperCase(javaTypeName.charAt(0)) + javaTypeName.substring(1);
                printWriter.println("        return getPropertyValueLiteral(" + getProperty + ").get" + x + "();");
            }
            else if (propertyCode.isCustomType()) {
                printWriter.println("        return (" + javaTypeName + ") getPropertyValueAs(" + getProperty +
                        ", " + simpleJavaTypeName + ".class);");
            }
            else {
                printWriter.println("        return (" + javaTypeName + ") getPropertyValue(" + getProperty + ");");
            }
        }
        printWriter.println("    }");

        printWriter.println();
        printWriter.println();
        printWriter.println("    public RDFProperty get" + name + "Property() {");
        printWriter.println("        final String uri = \"" + property.getURI() + "\";");
        printWriter.println("        final String name = getOWLModel().getResourceNameForURI(uri);");
        printWriter.println("        return getOWLModel().getRDFProperty(name);");
        printWriter.println("    }");

        printWriter.println();
        printWriter.println();
        printWriter.println("    public boolean has" + name + "() {");
        printWriter.println("        return getPropertyValueCount(" + getProperty + ") > 0;");
        printWriter.println("    }");

        String varName = "new" + name;
        if (multiple) {
            printWriter.println();
            printWriter.println();
            printWriter.println("    public Iterator list" + name + "() {");
            if (propertyCode.isCustomType()) {
                printWriter.println("        return listPropertyValuesAs(" + getProperty + ", " + simpleJavaTypeName + ".class);");
            }
            else {
                printWriter.println("        return listPropertyValues(" + getProperty + ");");
            }
            printWriter.println("    }");

            printWriter.println();
            printWriter.println();
            printWriter.println("    public void add" + name + "(" + simpleJavaTypeName + " " + varName + ") {");
            printWriter.println("        addPropertyValue(" + getProperty + ", " + varName + ");");
            printWriter.println("    }");

            printWriter.println();
            printWriter.println();
            String oldVarName = "old" + name;
            printWriter.println("    public void remove" + name + "(" + simpleJavaTypeName + " " + oldVarName + ") {");
            printWriter.println("        removePropertyValue(" + getProperty + ", " + oldVarName + ");");
            printWriter.println("    }");
        }

        printWriter.println();
        printWriter.println();
        printWriter.println("    public void set" + name + "(" + javaTypeName + " " + varName + ") {");
        if (multiple) {
            printWriter.println("        setPropertyValues(" + getProperty + ", " + varName + ");");
        }
        else {
            if (propertyCode.isPrimitive()) {
                String t = null;
                if ("int".equals(javaTypeName)) {
                    t = "java.lang.Integer";
                }
                else if ("boolean".equals(javaTypeName)) {
                    t = "java.lang.Boolean";
                }
                else {
                    t = "java.lang.Float";
                }
                printWriter.println("        setPropertyValue(" + getProperty + ", new " + t + "(" + varName + "));");
            }
            else {
                printWriter.println("        setPropertyValue(" + getProperty + ", " + varName + ");");
            }
        }
        printWriter.println("    }");
    }


    public void createInterfacePropertyCode(PrintWriter printWriter, RDFPropertyAtClassCode propertyCode) {
        RDFProperty property = propertyCode.getRDFProperty();
        String name = propertyCode.getUpperCaseJavaName();
        String javaTypeName = getJavaTypeName(propertyCode);

        printWriter.println();
        printWriter.println("    // Property " + property.getURI());
        printWriter.println();
        printWriter.println("    " + javaTypeName + " get" + name + "();");

        printWriter.println();
        printWriter.println("    RDFProperty get" + name + "Property();");

        printWriter.println();
        printWriter.println("    boolean has" + name + "();");

        if (propertyCode.isMultiple()) {
            String simpleJavaTypeName = propertyCode.getJavaType();
            printWriter.println();
            printWriter.println("    Iterator list" + name + "();");
            printWriter.println();
            printWriter.println("    void add" + name + "(" + simpleJavaTypeName + " new" + name + ");");
            printWriter.println();
            printWriter.println("    void remove" + name + "(" + simpleJavaTypeName + " old" + name + ");");
        }

        printWriter.println();
        printWriter.println("    void set" + name + "(" + javaTypeName + " new" + name + ");");
    }


    private String getJavaTypeName(RDFPropertyAtClassCode propertyCode) {
        if (propertyCode.isMultiple()) {
            return options.getSetMode() ? "Set" : "Collection";
        }
        else {
            return propertyCode.getJavaType();
        }
    }


    public String getBaseImplementation(RDFSNamedClass aClass) {
    	String resultString = "";
        for (Iterator it = aClass.getSuperclasses(false).iterator(); it.hasNext();) {
            RDFSClass superclass = (RDFSClass) it.next();
            String name = getSystemJavaClass(superclass);
            if (name != null) {
                return "Default" + name;
            }
            if (superclass instanceof RDFSNamedClass && !owlModel.getOWLThingClass().equals(superclass)) {
            	//if first superclass found
            	if (resultString.equals("")) {
					resultString = getImplementationName((RDFSNamedClass) superclass);
				} else {
					return null;
				}
            }
        }
        //if no superclass found
        if (resultString.equals("")) {
			return null;
		} else {
			return resultString;
		}
    }


    public String getBaseInterface(RDFSNamedClass cls) {
    	String resultString = "";
        for (Iterator it = cls.getSuperclasses(false).iterator(); it.hasNext();) {
            RDFSClass superclass = (RDFSClass) it.next();
            String name = getSystemJavaClass(superclass);
            if (name != null) {
                return name;
            }
            else if (superclass instanceof RDFSNamedClass && !owlModel.getOWLThingClass().equals(superclass)) {
            	resultString += (resultString.equals("") ? "" : ", ") + getInterfaceName((RDFSNamedClass) superclass);
            }
        }
        if (resultString.equals("")) {
			return cls.getProtegeType() instanceof OWLNamedClass ? "OWLIndividual" : "RDFIndividual";
		} else {
			return resultString;
		}
    }


    public File getImplementationFile(RDFSNamedClass aClass) {
        String localName = getImplementationNamePossiblyAbstract(aClass);
        return getImplementationFile(localName);
    }


    private File getImplementationFile(String localName) {
        String pack = options.getPackage();
        if (pack != null) {
            pack = pack.replace('.', '/') + "/";
        }
        else {
            pack = "";
        }
        return new File(options.getOutputFolder(), pack + "impl/" + localName + ".java");
    }


    public String getImplementationName(RDFSNamedClass aClass) {
        return "Default" + getInterfaceName(aClass);
    }


    public String getImplementationNamePossiblyAbstract(RDFSNamedClass aClass) {
        return "Default" + getInterfaceNamePossiblyAbstract(aClass);
    }


    public File getInterfaceFile(RDFSNamedClass aClass) {
        String localName = getInterfaceNamePossiblyAbstract(aClass);
        return getInterfaceFile(localName);
    }


    private File getInterfaceFile(String localName) {
        String pack = options.getPackage();
        if (pack != null) {
            pack = pack.replace('.', '/') + "/";
        }
        else {
            pack = "";
        }
        return new File(options.getOutputFolder(), pack + localName + ".java");
    }


    public String getInterfaceNamePossiblyAbstract(RDFSNamedClass aClass) {
        String str = new RDFSClassCode(aClass, options.getPrefixMode()).getJavaName();
        if (options.getAbstractMode()) {
            str += "_";
        }
        return str;
    }


    public String getInterfaceName(RDFSNamedClass aClass) {
        return new RDFSClassCode(aClass, options.getPrefixMode()).getJavaName();
    }


    private String getSystemJavaClass(RDFSClass superclass) {
        if (superclass.equals(owlModel.getOWLObjectPropertyClass())) {
            return "OWLObjectProperty";
        }
        else if (superclass.equals(owlModel.getOWLDatatypePropertyClass())) {
            return "OWLDatatypeProperty";
        }
        else if (superclass.equals(owlModel.getRDFPropertyClass())) {
            return "RDFProperty";
        }
        else if (superclass.equals(owlModel.getOWLNamedClassClass())) {
            return "OWLNamedClass";
        }
        else if (superclass.equals(owlModel.getRDFSNamedClassClass())) {
            return "RDFSNamedClass";
        }
        return null;
    }


    public static String getValidJavaName(String name) {
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isJavaIdentifierPart(c)) {
                name = name.replace(c, '_');
            }
        }
        return name;
    }


    private void perhapsAddImportJavaUtil(RDFSClassCode code, PrintWriter printWriter, boolean transitive) {
        for (Iterator it = code.getPropertyCodes(transitive).iterator(); it.hasNext();) {
            RDFPropertyAtClassCode propertyAtClassCode = (RDFPropertyAtClassCode) it.next();
            if (propertyAtClassCode.isMultiple()) {
                printWriter.println("import java.util.*;");
                printWriter.println();
                return;
            }
        }
    }

    public boolean hasMultipleSuperclasses(RDFSNamedClass aClass) {
    	boolean superclassFound = false;
        for (Iterator it = aClass.getSuperclasses(false).iterator(); it.hasNext();) {
            RDFSClass superclass = (RDFSClass) it.next();
            String name = getSystemJavaClass(superclass);
            if (name != null) {
                return false;
            }
            if (superclass instanceof RDFSNamedClass && !owlModel.getOWLThingClass().equals(superclass)) {
            	//if first superclass found
            	if (superclassFound == false) {
					superclassFound = true;
				} else {
					return true;
				}
            }
        }

        return false;
    }

}
