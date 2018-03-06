package com.hiveprod.flex.entity;  
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;

//import com.hiveprod.generator.service.OpenJpaPojoTransformationService;
import com.sun.codemodel.JCodeModel;  
public class JsonToPojo {  
     /**  
      * @param args  
      */  
     public static void main(String[] args) {  
         String packageName="com.hiveprod.flex.entity";        
        // File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-default.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"basic-tags.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"tags.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"validation-states.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"basic-form.json");
         
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"tooltips.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"popovers.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"modals.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"hubspot-messenger-alerts.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"alerts.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"button-group.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"button-loading.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"buttons.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"social-button.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"advanced-select.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"button-dropdown.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"stacked-pills-example.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"basic-tabs-example.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"basic-accordion-example.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-widget-tabs-example.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-button.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-badge.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-button.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-icons.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-label.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-toggle.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"portlet-tabs.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"headings.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"description-list.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"ordered-list.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"unordered-list.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"unstyled-List.json");
         //done	File inputJson= new File("./src/main/resources/json"+File.separator+"glyphicons.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"font-awesome-icons.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"circle-pricing-table.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"autocomplet-dropdown.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"typeahead-tags.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"validation-example.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"basic-element.json");         
         //done	File inputJson= new File("./src/main/resources/json"+File.separator+"donut-chart.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"area-chart.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"line-chart.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"morris-bar-chart.json");
         //done	File inputJson= new File("./src/main/resources/json"+File.separator+"morris-line-chart.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"pie-chart.json");
         //done File inputJson= new File("./src/main/resources/json"+File.separator+"Multiple-axes-line-chart.json");
         
         File inputJson= new File("./src/main/resources/json"+File.separator+"Multiple-axes-line-chart.json");
         
         
         
         
          File outputPojoDirectory=new File("./src-core-entity");  
          outputPojoDirectory.mkdirs();  
          try {  
        	  new JsonToPojo().convert2JSON(inputJson.toURI().toURL(), outputPojoDirectory, packageName, inputJson.getName().replace(".json", ""));  
               //generate annotation
               List<Class> openJpaEntityList = new ArrayList<Class>();
              //openJpaEntityList.add(Exchangerate.class);

             
             

               String projectName="flex";
               //pour faire fonctionner la transformation du pojo vers jpa , il faut commenter new JsonToPojo().convert2JSON en haut et decommenter openJpa
            //OpenJpaPojoTransformationService.transform(openJpaEntityList, projectName);
          } catch (Exception e) {  
               System.out.println("Encountered issue while converting to pojo: "+e.getMessage());  
               e.printStackTrace();  
          }  
     }  
     public void convert2JSON(URL inputJson, File outputPojoDirectory, String packageName, String className) throws IOException{  
          JCodeModel codeModel = new JCodeModel();  
          URL source = inputJson;  
          GenerationConfig config = new DefaultGenerationConfig() {  
	          @Override  
	          public boolean isGenerateBuilders() { // set config option by overriding method  
	              return true;  
	          }  
          
	          public SourceType getSourceType(){  
	        	  return SourceType.JSON;  
	          }  
          };  
          SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());  
          mapper.generate(codeModel, className, packageName, source);  
          codeModel.build(outputPojoDirectory);  
     }  
}