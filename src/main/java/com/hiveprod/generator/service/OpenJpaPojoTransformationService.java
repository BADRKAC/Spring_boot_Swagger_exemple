package com.hiveprod.generator.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.generic.annotation.GenerateUnitTestAOP;
import com.generic.reflect.ReflectUtil.RU;
//import com.hiveprod.demo.Customer;
import com.hiveprod.gen_entity.entity.Classe;
import com.hiveprod.gen_entity.entity.Paquetage;
import com.hiveprod.gen_entity.entity.Projet;

@GenerateUnitTestAOP	

public class OpenJpaPojoTransformationService {

	private static final Logger log = Logger.getLogger(OpenJpaPojoTransformationService.class);

	public static void main(String[] args) {

		List<Class> openJpaEntityList = new ArrayList<Class>();
		// openJpaEntityList.add(ma.ericsson.rdb.openjpa.entities.AdAccessPluginConf.class); // RDB
		// openJpaEntityList.add(ma.ericsson.eat.openjpa.entities.CParameter.class); // EAT
		// openJpaEntityList.add(ma.ericsson.axperadmin.openjpa.entities.ActiveUsers.class); // XPERADMIN
		// openJpaEntityList.add(ma.ericsson.jbpmbd.openjpa.entities.Correlationmapping.class); // JBPM
		// transform(openJpaEntityList, "ericson_project");

		// SMS & CALLS
//		openJpaEntityList.add(com.hiveprod.sms_backup_restore.call.class);
//		transform(openJpaEntityList, "sms_calls");
		
		// Demo
//		openJpaEntityList.add(com.hiveprod.demo.Customer.class);
//		transform(openJpaEntityList, "hp_gen_demo");
		
		

	}

	public static void transform(List<Class> openJpaEntityList, String projectName) {

		for (Class openJpaEntity : openJpaEntityList) {

			try {

				List<Projet> listProject = new ArrayList<Projet>();
				List<Paquetage> paquetages = new ArrayList<Paquetage>();
				List<Classe> classes = new ArrayList<Classe>();

				String packageName = openJpaEntity.getPackage().getName();
				String schema = null;
				if (openJpaEntity.isAnnotationPresent(Table.class)) {
					Table table = (Table) openJpaEntity.getAnnotation(Table.class);
					schema = table.schema();
				}

				Projet project = new Projet();
				// project.setTablePrefix(tablePrefix);
				// project.setProjetId(projetId);
				project.setPaquetages(paquetages);
				project.setNom(projectName);
				project.setDbName(schema);

				Paquetage paquetage = new Paquetage();
				paquetage.setClasses(classes);
				paquetage.setName(packageName +".hp.compatible");
				// paquetage.setPackageId(packageId);
				paquetage.setProjet(project);

				List<Class> listClass = RU.getClassesByPackage(packageName);

				for (Class clazz : listClass) {
					// if (clazz.isAnnotationPresent(Entity.class)) {
					log.info("CLASS = " + clazz.getName());
					Classe classe = GenHPPOJOService.toClasse(clazz);
					classe.setPaquetage(paquetage);
					classes.add(classe);
					// }
				}

				paquetages.add(paquetage);
				listProject.add(project);

				// for (Class clazz : listClass) {
				// if (clazz.isAnnotationPresent(Entity.class)) {
				// }
				// }

				GenHPPOJOService.createHPPojoClasses(listProject);

			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}

		}

	}

}
