package com.hiveprod.generator.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.StringUtils;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.generic.annotation.GenerateUnitTestAOP;
import com.generic.service.Utils;
import com.google.common.base.CaseFormat;
import com.hiveprod.gen_entity.entity.Association;
import com.hiveprod.gen_entity.entity.Attribut;
import com.hiveprod.gen_entity.entity.Classe;
import com.hiveprod.gen_entity.entity.Enumeration;
import com.hiveprod.gen_entity.entity.Paquetage;
import com.hiveprod.gen_entity.entity.Projet;
import com.hiveprod.gen_entity.entity.enumeration.EnumerationType;
import com.hiveprod.gen_entity.entity.enumeration.EnumerationTypeAssociation;
import com.hiveprod.annotation.GenericJsonDeserializer;
import com.hiveprod.annotation.GenericJsonSerializer;
import com.hiveprod.annotation.HpColumn;
import com.hiveprod.external.project.DB;

@SuppressWarnings({ "unchecked", "rawtypes" })
@GenerateUnitTestAOP
public class GenHPPOJOService {

	// private final static String SOURCE_PATH = "C:/Users/ZAKARI/Esioox/GenEntity";
	// private final static String SERVER_PATH = "C:/Users/ZAKARI/Esioox/.metadata/.plugins/org.eclipse.wst.server.core/tmp2/wtpwebapps/GenEntity/WEB-INF/classes";
	// private final static String DESKTOP_SERVER_PATH = "C:/Users/ZAKARI/Esioox/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/desktop-gen/WEB-INF/classes";
	//
	// private final static String SOURCE_PATH = "D:/workspace/dev/java/workspaces/eclipse_workspace_esioox/GenEntity";
	//private final static String SOURCE_PATH = "D:/workspace/dev/java/workspaces/eclipse_workspace_hiveprod/hp-gen-entites/";
	//recently commented
private final static String SOURCE_PATH = "C:\\Users\\Lenovo\\eclipse-workspace\\git_jennifer_2101\\hp-gen-2\\hp-generator-core\\src-core-entity\\";
	// private final static String SERVER_PATH = "D:/workspace/dev/java/workspaces/eclipse_workspace_esioox/.metadata/.plugins/org.eclipse.wst.server.core/tmp6/wtpwebapps/GenEntity/WEB-INF/classes";
	// private final static String DESKTOP_SERVER_PATH =
	// "D:/workspace/dev/java/workspaces/eclipse_workspace_esioox/.metadata/.plugins/org.eclipse.wst.server.core/tmp6/wtpwebapps/desktop-gen/WEB-INF/classes";

	/** Format source code : http://www.programcreek.com/2013/04/how-to-format-java-code-by-using-eclipse-jdt/ */

	/** Object OneToOne */
	/** Many-to-Many: One Person Has Many Skills, a Skill is reused between Many Person(s) */
	/** One-to-Many : One Person Has Many Skills, a Skill is NOT reused between Person(s) */
	/** Class ClassSource { */
	/** @OneToMany(mappedBy = "le nom de l'attribut de type ObjectSource dans la Classe ClassDest", fetch = FetchType.LAZY) */
	/** ClassDest dest */
	/** }o */
	/** @ManyToOne(fetch = FetchType.LAZY) */

	/** @JoinTable(name = Config.PREFIX + "template_contacts_dynamic", joinColumns = @JoinColumn(name = "template_id"), inverseJoinColumns = @JoinColumn(name = "categorie_id")) */
	public static void createHPPojoClasses(List<Projet> listProjet) {

		ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

		for (Projet projet : listProjet) {
			String projetName = Utils.normalize(projet.getNom());
			//recently commented
			//File sourceDir = new File(SOURCE_PATH + "/src-generated-entity-" + projetName);
			File sourceDir = new File(SOURCE_PATH);
			if (!sourceDir.exists()) {
				if (!sourceDir.mkdir()) {
					System.err.println("don't exist");
				}
			}

			for (Paquetage paquetage : projet.getPaquetages()) {
				String paquetageName = Utils.normalize(/* projetName+"."+ */paquetage.getName()).replace("-", ".");
				String classPath = sourceDir.getAbsolutePath();
				for (String packagePart : paquetageName.split("\\.")) {
					classPath += "/" + packagePart;
					new File(classPath).mkdir();
					System.out.println("create file in classpath : "+classPath);
				}
				for (Classe classe : paquetage.getClasses()) {
					if(!classe.toString().contains("JsonToPojo")) {
						System.out.println("createClass : " + classe.toString());
						files.addAll(createClass(projet, classPath, paquetageName, classe));
					}
				}
			}
		}
	
		generateDotClasses(files);

	}

	private static ArrayList<JavaFileObject> createClass(Projet projet, String classPath, String paquetageName, Classe classe) {

		ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

		final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
		String classeName = Utils.fromHumainToJavaName(classe.getNom(), true);
		classeName = StringUtils.capitalize(classeName.toLowerCase());
		javaClass.setPackage(paquetageName).setName(classeName);
		javaClass.addImport(DB.class);

		/*************/
		/*************/

		/** Other fields */
		List<Attribut> attributIds = new ArrayList<>();
		for (Attribut attribut : classe.getAttributs()) {

			// System.out.println(attribut.toString());

			if (!EnumerationType.Enum.equals(attribut.getType())) {
				// String typeAttribut = StringUtils.capitalize(attribut.getType().name().toLowerCase());
				String typeAttribut = attribut.getType().name();

				String attributName = Utils.fromHumainToJavaName(attribut.getNom(), false);
				javaClass.addProperty(typeAttribut, attributName);
				FieldSource field = javaClass.getField(attributName);

				/******************/
				/*** DATE FIELD ***/
				/******************/
				if (EnumerationType.Date.equals(attribut.getType())) {
					AnnotationSource annotation = field.addAnnotation(Temporal.class);
					annotation.setEnumValue(TemporalType.TIMESTAMP);
					javaClass.addImport(Date.class);
				}

				/******************/
				/*** BigInteger ***/
				/******************/
				if (EnumerationType.BigInteger.equals(attribut.getType())) {
					javaClass.addImport(BigInteger.class);
				}
				/**************************************/
				/*** @HpColumn(displayField = true) ***/
				/**************************************/
				if (Boolean.TRUE.equals(attribut.getAttributAafficher())) {
					AnnotationSource annotation = field.addAnnotation(HpColumn.class);
					annotation.setLiteralValue("displayField", "true");
				}

				AnnotationSource columnAnnotation = field.addAnnotation(Column.class);
				if (attribut.getDbColumnName() != null && !attribut.getDbColumnName().isEmpty()) {
					columnAnnotation.setStringValue("name", attribut.getDbColumnName());
				} else {
					columnAnnotation.setStringValue("name", Utils.upperCamel(field.getName()));
				}

				/******************************/
				/**** Existing annotation *****/
				/******************************/
				for (Annotation annotation : attribut.getAnnotation()) {
					AnnotationSource annot = field.addAnnotation(annotation.annotationType());
					for (Method method : annotation.annotationType().getDeclaredMethods()) {
						try {
							Object value = method.invoke(annotation, (Object[]) null);
							System.out.println(" " + method.getName() + ": " + value + " => " + value.getClass());
							if (!value.equals("$missing$") && !value.equals("##default") && !value.getClass().equals(Class.class)) {
								if (value instanceof String) {
									annot.setLiteralValue(method.getName(), "\"" + value.toString() + "\"");
								} else {
									annot.setLiteralValue(method.getName(), value.toString());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// for(Field fieldAnnotation : annotation.annotationType().getDeclaredFields()){
					// annot.setLiteralValue("displayField", "true");
					// }

				}

				/*************/
				/**** ID *****/
				/*************/
				if (attribut.getIsId() != null && attribut.getIsId()) {
					field.addAnnotation(Id.class);
					AnnotationSource annotationSource = field.addAnnotation(GeneratedValue.class);// .addAnnotationValue("strategy", GenerationType.AUTO);
					javaClass.addImport(GenerationType.class);
					annotationSource.setLiteralValue("strategy", "GenerationType.AUTO");
					attributIds.add(attribut);
				}
			}

		}

		/*************/
		/** LONG ID **/
		/*************/
		/**** @Id ****/
		/**** @GeneratedValue(strategy = GenerationType.AUTO) **/
		/**** Long id **/
		if (attributIds.size() == 0) {
			// String idFieldName = classeName.toLowerCase() + "Id";
			String idFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, classeName + "Id");
			setFieldAsAnId(javaClass, idFieldName);
		} else if (attributIds.size() > 1) {
			/******************/
			/** Multiple ID ***/
			/******************/
			files.addAll(createMultipleIdClass(classPath, paquetageName, classeName + "Id", attributIds));
			// @IdClass(value= ma.ericsson.rdb.openjpa.entities.AdAccessPluginConfId.class)
			String annotationValue = javaClass.getQualifiedName() + "Id.class";
			AnnotationSource annotation = javaClass.addAnnotation(IdClass.class);
			annotation.setLiteralValue(annotationValue);
			// .addAnnotationValue(annotationValue);
		}

		/***************************************/
		/********** Association Sortante *******/
		/***************************************/
		List<Association> associations = new ArrayList<Association>();
		if (classe.getAssociationsEntrante() != null) {
			associations.addAll(classe.getAssociationsEntrante());
		}

		if (classe.getAssociationsSortante() != null) {
			associations.addAll(classe.getAssociationsSortante());
		}

		for (Association association : associations) {
			createAssociation(projet, classe, javaClass, association);
		}

		/**********************************/
		/*************** ENUM *************/
		/** @Enumerated(EnumType.STRING) **/
		/**********************************/
		// TODO ENUMRATION @JsonValue => voir IconCls48.class
		if (classe.getEnumerations() != null) {
			for (Enumeration enumeration : classe.getEnumerations()) {
				files.addAll(createEnumeration(classPath, paquetageName, javaClass, enumeration));
			}
		}

		MethodSource methodJsonCreator1 = javaClass.addMethod() //
				.setPublic() //
				.setStatic(true) //
				.setName("create") //
				.setReturnType(classeName) //
				.setBody(String.format("if (id != null && id.matches(\"[0-9]+\"))" + //
						" {return (%s) DB.psGen.getOne(Long.parseLong(id), %s.class);}" + //
						" return null;", classeName, classeName));
		methodJsonCreator1.addParameter(String.class, "id");
		methodJsonCreator1.addAnnotation(JsonCreator.class);

		MethodSource methodJsonCreator2 = javaClass.addMethod()//
				.setPublic()//
				.setStatic(true)//
				.setName("create")//
				.setReturnType(classeName)//
				.setBody(String.format("return (%s) DB.psGen.getOne(id, %s.class);", classeName, classeName));
		methodJsonCreator2.addParameter(Long.class, "id");
		methodJsonCreator2.addAnnotation(JsonCreator.class);

		/********************/
		/** @Table @Entity **/
		/********************/
		javaClass.addAnnotation(Entity.class);
		javaClass.addImport(GenericJsonSerializer.class);
		javaClass.addImport(GenericJsonDeserializer.class);

		AnnotationSource jsonSerialize = javaClass.addAnnotation(JsonSerialize.class);
		jsonSerialize.setLiteralValue("using", "GenericJsonSerializer.class");

		AnnotationSource jsonDeserialize = javaClass.addAnnotation(JsonDeserialize.class);
		jsonDeserialize.setLiteralValue("using", "GenericJsonDeserializer.class");

		AnnotationSource annotation = javaClass.addAnnotation(Table.class);
		String prefix = projet.getTablePrefix() == null ? "" : projet.getTablePrefix();
		annotation.setStringValue("name", /* "USER"+ */prefix + classe.getTableName());
		if (projet.getDbName() != null) {
			annotation.setStringValue("schema", projet.getDbName());
		}
		// annotation.setStringValue("catalog", projet.getDbName());

		/********************/
		/** PRINT CODE SRC **/
		/********************/
		// System.out.println("Generating : " + javaClass.getName());
		// System.out.println(javaClass);
		Utils.setFileContent(new File(classPath + "/" + classeName + ".java"), javaClass.toString());
		 generateClass(classPath, javaClass, classeName);
		
		/** Repertoire src>META-INF>persistence.xml */
		/** Fichier src> log4j.propeties */
		JavaFileObject file = new JavaSourceFromString(paquetageName + "." + classeName, javaClass.toString());
		files.add(file);

		return files;

	}

	private static void generateClass(String classPath, final JavaClassSource javaClass, String classeName) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(classPath + "/" + classeName + ".java"))))) {
			System.out.println("class generer dans "+classPath+"/"+classeName);
            out.println(javaClass.toString());
            out.close();
        } catch (Exception e) {
             e.printStackTrace();
        }
	}

	private static ArrayList<JavaFileObject> createMultipleIdClass(String classPath, String paquetageName, String classeName, List<Attribut> attributIds) {
		ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

		final JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
		javaClass.setPackage(paquetageName).setName(classeName);
		javaClass.addInterface(Serializable.class);

		/*************/
		/*************/

		for (Attribut attribut : attributIds) {

			System.out.println(attribut.toString());

			String typeAttribut = StringUtils.capitalize(attribut.getType().name().toLowerCase());
			String attributName = Utils.normalize(attribut.getNom());
			javaClass.addProperty(typeAttribut, attributName);
//			FieldSource field = javaClass.getField(attributName);

		}

		/********************/
		/** PRINT CODE SRC **/
		/********************/
		System.out.println("Generating : " + javaClass.getName());
		// System.out.println(javaClass);
		Utils.setFileContent(new File(classPath + "/" + classeName + ".java"), javaClass.toString());
		/** Repertoire src>META-INF>persistence.xml */
		/** Fichier src> log4j.propeties */
		JavaFileObject file = new JavaSourceFromString(paquetageName + "." + classeName, javaClass.toString());
		files.add(file);

		return files;

	}

	private static ArrayList<JavaFileObject> createEnumeration(String classPath, String paquetageName, JavaClassSource javaClass, Enumeration enumeration) {

		ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

		String enumerationName = Utils.normalize(enumeration.getNom());
		String typeAttribut = StringUtils.capitalize(enumerationName.toLowerCase());
		javaClass.addProperty(typeAttribut, enumerationName);
		javaClass.addImport(paquetageName + "." + typeAttribut);
		FieldSource field = javaClass.getField(enumerationName);
		AnnotationSource annotation = field.addAnnotation(Enumerated.class);
		annotation.setEnumValue(EnumType.STRING);
		String[] valeurs = new String[enumeration.getValeursEnumeration().size()];

		final JavaEnumSource javaEnumSource = Roaster.create(JavaEnumSource.class);
		javaEnumSource.setName(typeAttribut);
		javaEnumSource.setPackage(paquetageName);

		for (int i = 0; i < enumeration.getValeursEnumeration().size(); i++) {
			valeurs[i] = enumeration.getValeursEnumeration().get(i).getValeur();
			javaEnumSource.addEnumConstant(enumeration.getValeursEnumeration().get(i).getValeur());

		}
		Utils.setFileContent(new File(classPath + "/" + typeAttribut + ".java"), javaEnumSource.toString());
		System.out.println(javaEnumSource);
		JavaFileObject file = new JavaSourceFromString(paquetageName + "." + typeAttribut, javaEnumSource.toString());
		files.add(file);

		return files;
	}

	private static void createAssociation(Projet projet, Classe classe, JavaClassSource javaClass, Association association) {

		// for (Association association : classe.getAssociationsEntrante()) {

		String classSource = null;
		String classDest = null;
		EnumerationTypeAssociation typeAssociation = association.getTypeAssociation();

		if (association.getClasseSource().equals(classe)) {
			classSource = Utils.normalize(association.getClasseSource().getNom());
			classDest = Utils.normalize(association.getClasseDestination().getNom());
		} else {
			classSource = Utils.normalize(association.getClasseDestination().getNom());
			classDest = Utils.normalize(association.getClasseSource().getNom());
			if (typeAssociation.equals(EnumerationTypeAssociation.OneToMany)) {
				typeAssociation = EnumerationTypeAssociation.ManyToOne;
			} else if (typeAssociation.equals(EnumerationTypeAssociation.ManyToOne)) {
				typeAssociation = EnumerationTypeAssociation.OneToMany;
			}
		}

		classSource = StringUtils.capitalize(classSource.toLowerCase());
		classDest = StringUtils.capitalize(classDest.toLowerCase());

		/* Utils.normalize(projetName).replace("-", ".") + "." + */
		javaClass.addImport(association.getClasseDestination().getPaquetage().getName().toLowerCase() + "." + classDest);

		/***************************************/
		/********** One-TO-One *****************/
		/***************************************/
		if (typeAssociation.equals(EnumerationTypeAssociation.OneToOne)) {

			/***************************************/
			/********** assoc id property **********/
			/***************************************/
			String assocPropName = (association.getNom() != null) ? association.getNom() : classDest.toLowerCase();
			String assocIdPropName = assocPropName + "Id";

			javaClass.addProperty(Long.class, assocIdPropName);
			FieldSource idClassDestination = javaClass.getField(assocIdPropName);
			AnnotationSource columnAnnotation = idClassDestination.addAnnotation(Column.class);
			columnAnnotation.setStringValue("name", assocIdPropName);
			columnAnnotation.setLiteralValue("updatable", "false");
			columnAnnotation.setLiteralValue("insertable", "false");

			/***************************************/
			/********** Object property ************/
			/***************************************/
			javaClass.addProperty(classDest, assocPropName);
			FieldSource fieldClasseDest = javaClass.getField(assocPropName);
			fieldClasseDest.addAnnotation(OneToOne.class);
			AnnotationSource annotation = fieldClasseDest.addAnnotation(JoinColumn.class);
			annotation.setStringValue("name", assocIdPropName);

		}

		/***************************************/
		/********** One-TO-Many ***************/
		/***************************************/
		if (typeAssociation.equals(EnumerationTypeAssociation.OneToMany)) {

			javaClass.addImport(List.class);
			javaClass.addProperty("List<" + classDest + ">", classDest.toLowerCase() + "s");
			FieldSource fieldClasseSource = javaClass.getField(classDest.toLowerCase() + "s");
			AnnotationSource annotation = fieldClasseSource.addAnnotation(OneToMany.class);
			annotation.setLiteralValue("cascade", "javax.persistence.CascadeType.ALL");
			annotation.setStringValue("mappedBy", classSource.toLowerCase());
		}

		/***************************************/
		/********** Many-TO-One ***************/
		/***************************************/

		if (typeAssociation.equals(EnumerationTypeAssociation.ManyToOne)) {

			javaClass.addProperty(Long.class, classDest.toLowerCase() + "Id");
			FieldSource idClassDest = javaClass.getField(classDest.toLowerCase() + "Id");
			AnnotationSource columnAnnotation = idClassDest.addAnnotation(Column.class);
			columnAnnotation.setStringValue("name", classDest.toLowerCase() + "Id");
			columnAnnotation.setLiteralValue("updatable", "false");
			columnAnnotation.setLiteralValue("insertable", "false");

			javaClass.addProperty(classDest, classDest.toLowerCase());
			FieldSource fieldClasseDest = javaClass.getField(classDest.toLowerCase());
			fieldClasseDest.addAnnotation(ManyToOne.class);
			AnnotationSource annotation = fieldClasseDest.addAnnotation(JoinColumn.class);
			annotation.setStringValue("name", classDest.toLowerCase() + "Id");
			annotation.setLiteralValue("nullable", "false");
		}

		/***************************************/
		/********** Many-TO-Many ***************/
		/***************************************/
		if (typeAssociation.equals(EnumerationTypeAssociation.ManyToMany)) {
			javaClass.addImport(List.class);

			javaClass.addProperty("List<" + association.getClasseDestination().getPaquetage().getName() + "." + classDest + ">", classDest.toLowerCase() + "s");
			FieldSource fieldClasseDest = javaClass.getField(classDest.toLowerCase() + "s");
			fieldClasseDest.addAnnotation(ManyToMany.class);
			AnnotationSource annotation = fieldClasseDest.addAnnotation(JoinTable.class);
			annotation.setStringValue("name", projet.getTablePrefix() + classSource + "_" + classDest);
			annotation.setLiteralValue("joinColumns", "@javax.persistence.JoinColumn(name = \"" + classDest + "Id\")");
			annotation.setLiteralValue("inverseJoinColumns", "@javax.persistence.JoinColumn(name = \"" + classSource + "Id\")");
		}
	}

	private static void setFieldAsAnId(JavaClassSource javaClass, String idFieldName) {
		if (javaClass.getField(idFieldName) == null) {
			javaClass.addProperty(Long.class, idFieldName);
		}
		FieldSource fieldId = javaClass.getField(idFieldName);
		fieldId.addAnnotation(Id.class);

		AnnotationSource annotationSource = fieldId.addAnnotation(GeneratedValue.class);// .addAnnotationValue("strategy", GenerationType.AUTO);
		javaClass.addImport(GenerationType.class);
		annotationSource.setLiteralValue("strategy", "GenerationType.AUTO");

		// AnnotationSource columnAnnotation = fieldId.addAnnotation(Column.class);
		// columnAnnotation.setStringValue("name", upperCamel(fieldId.getName()));

	}

	private static void generateDotClasses(ArrayList<JavaFileObject> files) {

		try {

			/** http://www.javaworld.com/article/2071777/design-patterns/add-dynamic-java-code-to-your-application.html */
			// int errorCode = com.sun.tools.javac.Main.compile(new String[] {
			// "-classpath", "bin",
			// "-d", "/temp/dynacode_classes",
			// "dynacode/sample/PostmanImpl.java" });
			//
			// // The dir contains the compiled classes.
			// File classesDir = new File("/temp/dynacode_classes/");
			// // The parent classloader
			// ClassLoader parentLoader = Postman.class.getClassLoader();
			// // Load class "sample.PostmanImpl" with our own classloader.
			// URLClassLoader loader1 = new URLClassLoader(
			// new URL[] { classesDir.toURL() }, parentLoader);
			// Class cls1 = loader1.loadClass("sample.PostmanImpl");
			// Postman postman1 = (Postman) cls1.newInstance();
			// /*
			// * Invoke on postman1 ...
			// * Then PostmanImpl.java is modified and recompiled.
			// */
			// // Reload class "sample.PostmanImpl" with a new classloader.
			// URLClassLoader loader2 = new URLClassLoader(
			// new URL[] { classesDir.toURL() }, parentLoader);
			// Class cls2 = loader2.loadClass("sample.PostmanImpl");
			// Postman postman2 = (Postman) cls2.newInstance();

			/*JavaCompiler compiler =*/ ToolProvider.getSystemJavaCompiler();
			/*DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();*/

			Object[] all = files.toArray();
			// Iterable<? extends JavaFileObject> compilationUnits = (Iterable<? extends JavaFileObject>) Arrays.asList(all);
			/*Iterable compilationUnits =*/ Arrays.asList(all);
			final List<String> compilerOptions = new ArrayList();
			// compilerOptions.add("-d");
			// compilerOptions.add(SERVER_PATH);
			// compilerOptions.add("-d");
			// compilerOptions.add(DESKTOP_SERVER_PATH);
			compilerOptions.add("-d");
			compilerOptions.add(SOURCE_PATH + "/build/classes");
			compilerOptions.add("-classpath");
			compilerOptions.add(getClassPath());
			System.out.println("compile "+SOURCE_PATH);
			// String[] options = new String[] { "-d", SOURCE_PATH+"/build/classes"};

			// CompilationTask task = compiler.getTask(new PrintWriter(new OutputStreamWriter(System.err, Charset.defaultCharset()), true), null, diagnostics, compilerOptions, null, compilationUnits);
			// boolean success = task.call();
			//
			// System.out.println(diagnostics.getDiagnostics());
			//
			// PersistenceService.setInstance("my-persistence-unit-local");
			// System.out.println("Success: " + success);
			//
			// if (success) {
			//
			// try {
			//
			// Method[] methodes = Class.forName("generated.database.pack1.Maclasse").getDeclaredMethods();
			// for (int i = 0; i < methodes.length; i++) {
			// System.out.println(methodes[i].getName());
			// }
			// } catch (ClassNotFoundException e) {
			// e.printStackTrace();
			// System.err.println("No");
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	private static String getClassPath() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader) classLoader).getURLs();
		StringBuilder buf = new StringBuilder(1000);
		buf.append(".");
		String separator = System.getProperty("path.separator");
		for (URL url : urls) {
			buf.append(separator).append(url.getFile());
		}

		return buf.toString();

	}

//	public static Classe toClasse(Class clazz) {
//		return toClasse(clazz, null);
//	}

	public static Classe toClasse(Class clazz) {

		Classe classe = new Classe();

		// TODO classe.setAssociationsEntrante(toAssociationsEntrante(classe, clazz));
		classe.setAssociationsSortante(toAssociationsSortante(classe, clazz));

		classe.setAttributs(toAttributs(classe, clazz));

		// classe.setClasseFilles(classeFilles);
		// classe.setClasseId(classeId);
		// classe.setClasseMere(classeMere);
		// TODO classe.setEnumerations(toEnumerations(classe, clazz));
		classe.setNom(clazz.getSimpleName());

		Table table = (Table) clazz.getAnnotation(Table.class);
		if (table != null) {
			classe.setTableName(table.name());
		} else {
			classe.setTableName(Utils.upperCamel(clazz.getSimpleName()));
		}
		return classe;
	}

	public static void main(String[] args) {

		System.out.println(Utils.upperCamel("postCode22sdf33"));
	}

//	private static List<Enumeration> toEnumerations(Classe classe, Class clazz) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	private static List<Association> toAssociationsSortante(Classe classe, Class clazz) {
		List<Association> res = new ArrayList<Association>();
		for (Field field : clazz.getDeclaredFields()) {
			if (EnumerationType.getEnumerationType(field.getType()) == null) {
				Association assoc = toAssociation(classe, field);
				res.add(assoc);
			}

		}

		return res;
	}

//	private static List<Association> toAssociationsEntrante(Classe classe, Class clazz) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public static List<Attribut> toAttributs(Classe classe, Class clazz) {
		List<Attribut> res = new ArrayList<Attribut>();
		boolean findAnAttributToDisplay = false;
		Attribut attrId = null;
		for (Field field : clazz.getDeclaredFields()) {
			Attribut attr = toAttribut(field);
			if (attr.getType() == null) {
				System.out.println("excluding not premitif field : " + field.toString());
				continue;
			}
			attr.setClasseProprietaire(classe);
			res.add(attr);
			if (attr.getType().equals(EnumerationType.String) && !findAnAttributToDisplay) {
				attr.setAttributAafficher(true);
				findAnAttributToDisplay = true;
			}

			if (field.isAnnotationPresent(Id.class)) {
				attr.setIsId(true);
				attrId = attr;
			}

		}

		if (!findAnAttributToDisplay) {
			if (attrId != null) {
				attrId.setAttributAafficher(true);
			} else if (res.size() > 0) {
				res.get(0).setAttributAafficher(true);
			}
		}

		return res;
	}

	private static Attribut toAttribut(Field field) {
		Attribut attr = new Attribut();
		Column column = field.getAnnotation(Column.class);
		if (column != null) {
			attr.setDbColumnName(column.name());
		}
		attr.setNom(field.getName());
		attr.setType(EnumerationType.getEnumerationType(field.getType()));

		attr.setAnnotation(field.getAnnotations());

		return attr;
	}

	private static Association toAssociation(Classe classe, Field field) {
		Association assoc = new Association();
		assoc.setNom(field.getName());

		// Classe classeDestination = toClasse(RU.getListGenericType(field));
		// assoc.setClasseDestination(classeDestination);
		assoc.setClasseDestination(classe);
		// Classe classeSource = toClasse(field.getDeclaringClass());
		// assoc.setClasseSource(classeSource);
		assoc.setClasseSource(classe);

		// assoc.setTypeAssociation(typeAssociation);

		assoc.setTypeAssociation(EnumerationTypeAssociation.getEnumerationType(field));
		// attr.setAttributId(attributId);
		return assoc;
	}

}

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;

	JavaSourceFromString(String name, String code) {
		super(URI.create(name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
}
