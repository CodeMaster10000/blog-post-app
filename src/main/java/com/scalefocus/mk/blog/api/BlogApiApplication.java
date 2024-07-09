package com.scalefocus.mk.blog.api;

import com.scalefocus.mk.blog.api.core.config.EnvironmentInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Blog API application.
 * <p>
 * This class serves as the entry point for the Spring Boot application. It contains
 * the main method which uses SpringApplication.run to launch the application.
 * </p>
 */
@SpringBootApplication
public class BlogApiApplication {

	/**
	 * Main method to launch the Blog API application.
	 * <p>
	 * This method starts the Spring Boot application by invoking SpringApplication.run
	 * with the BlogApiApplication class and command-line arguments.
	 * </p>
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BlogApiApplication.class);
		application.addInitializers(new EnvironmentInitializer());
		application.run(args);
	}
}
