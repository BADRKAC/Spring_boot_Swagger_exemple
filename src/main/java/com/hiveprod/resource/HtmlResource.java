package com.hiveprod.resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@RestController
@RequestMapping("/html")
@Api(value = "html Resource REST Endpoint")
public class HtmlResource {
	
	 @GetMapping("/all")
	    public List<Attribut> getAttributs() {

	        return Arrays.asList(
	                new Attribut("class", "fa-users", "dark-blue"),
	                new Attribut("class", "fa-money", "orange"),
	                new Attribut("clazz", "fa-bell", "green")

	                );
	      
	    }
	 
 

	    private class Attribut {

	        @ApiModelProperty(notes = "title of the Attribut")
	        private String AttributTitle;
	        @ApiModelProperty(notes = "icon of the Attribut")
	        private String icon;
	        
	        @ApiModelProperty(notes = "color of the Attribut")
	        private String color;
	       

	        public Attribut(String attributTitle, String icon, String color) {
				AttributTitle = attributTitle;
				this.icon = icon;
				this.color = color;
			}

			public String getAttributTitle() {
				return AttributTitle;
			}

			public void setAttributTitle(String attributTitle) {
				AttributTitle = attributTitle;
			}

			public String getIcon() {
				return icon;
			}

			public void setIcon(String icon) {
				this.icon = icon;
			}

			public String getColor() {
				return color;
			}

			public void setColor(String color) {
				this.color = color;
			}

			

	       
	    }

}
