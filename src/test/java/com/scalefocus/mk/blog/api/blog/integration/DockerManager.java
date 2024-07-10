package com.scalefocus.mk.blog.api.blog.integration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@TestConfiguration
class DockerManager {

    private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);
    private static final String RELATIVE_CONTAINER_DIRECTORY = "container";

    private final String keycloakUrl;
    private final String mySqlUrl;
    private final String mySqlUser;
    private final String mySqlPassword;

    DockerManager(@Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String keycloakUrl,
                  @Value("${spring.datasource.url}") String mySqlUrl,
                  @Value("${spring.datasource.username}") String mySqlUser,
                  @Value("${spring.datasource.password}")String mySqlPassword) {
        this.keycloakUrl = keycloakUrl;
        this.mySqlUrl = mySqlUrl;
        this.mySqlUser = mySqlUser;
        this.mySqlPassword = mySqlPassword;
    }

    @PostConstruct
    void startContainers() {
        try {
            runDockerComposeUp(List.of("mysql", "keycloak"));
            waitForKeycloakInstance();
            waitForMySql();
        } catch (IOException | InterruptedException e) {
            logger.error("Error starting Docker containers", e);
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    void stopContainers() {
        //here
        try {
            logger.info("Stopping Docker containers...");
            List<String> command = Arrays.asList("docker-compose", "down", "-v");
            Process process = startProcessWithCommand(command);
            logProcessOutput(process);
            validateProcessExecution(process, "Failed to stop Docker containers. Exit code: ");
        } catch (InterruptedException | IOException e) {
            logger.error("Error stopping Docker containers", e);
            Thread.currentThread().interrupt();
        }

    }

    private void runDockerComposeUp(List<String> serviceNames) throws IOException, InterruptedException {
        logger.info("Starting Docker containers...");
        List<String> command = new ArrayList<>(List.of("docker-compose", "up"));
        command.addAll(serviceNames);
        command.addAll(List.of("--build", "-d"));
        Process process = startProcessWithCommand(command);
        logProcessOutput(process);
        validateProcessExecution(process, "Failed to start Docker containers. Exit code: ");
    }

    private static void logProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        }
    }

    private static Process startProcessWithCommand(List<String> command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(Paths.get(RELATIVE_CONTAINER_DIRECTORY).toFile());
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private void waitForKeycloakInstance() throws InterruptedException {
        boolean isHealthy = false;
        while (!isHealthy) {
            try {
                URL url = URI.create(keycloakUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    isHealthy = true;
                }
            } catch (IOException e) {
                logger.info("Waiting for Keycloak to start...");
                Thread.sleep(10000);
            }
        }
        logger.info("Keycloak is up and running!");
    }

    private void waitForMySql() throws InterruptedException {
        boolean isHealthy = false;
        while (!isHealthy) {
            try (Connection connection = DriverManager.getConnection(mySqlUrl, mySqlUser, mySqlPassword)) {
                if (connection != null && !connection.isClosed()) {
                    isHealthy = true;
                }
            } catch (SQLException e) {
                logger.info("Waiting for MySQL to start...");
                Thread.sleep(10000);
            }
        }
        logger.info("MySQL is up and running!");
    }

    private static void validateProcessExecution(Process process, String x) throws InterruptedException {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new ProcessingException(x + exitCode);
        }
    }

}