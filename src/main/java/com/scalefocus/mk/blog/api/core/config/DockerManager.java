package com.scalefocus.mk.blog.api.core.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
final class DockerManager {

    private static final Logger logger = LoggerFactory.getLogger(DockerManager.class);

    private final DockerDataConfig dockerDataConfig;

    DockerManager(DockerDataConfig dockerDataConfig) {
        this.dockerDataConfig = dockerDataConfig;
    }

    @PostConstruct
    void startContainers() {
        try {
            runDockerComposeUp();
            waitForKeycloakInstance();
            waitForMySql();
        } catch (IOException | InterruptedException e) {
            logger.error("Error starting Docker containers", e);
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    void stopContainers() {
        try {
            logger.info("Stopping Docker containers...");
            List<String> command = Arrays.asList("docker-compose", "down");
            Process process = startProcessWithCommand(command);
            logProcessOutput(process);
            validateProcessExecution(process, "Failed to stop Docker containers. Exit code: ");
        } catch (InterruptedException | IOException e) {
            logger.error("Error stopping Docker containers", e);
            Thread.currentThread().interrupt();
        }

    }

    private void runDockerComposeUp() throws IOException, InterruptedException {
        logger.info("Starting Docker containers...");
        List<String> command = Arrays.asList("docker-compose", "up", "--build", "-d");
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
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private void waitForKeycloakInstance() throws InterruptedException {
        boolean isHealthy = false;
        while (!isHealthy) {
            try {
                URL url = URI.create(dockerDataConfig.getKeycloakUrl()).toURL();
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
        String jdbcUrl = dockerDataConfig.getMySqlUrl();
        String username = dockerDataConfig.getMySqlUser();
        String password = dockerDataConfig.getMySqlPassword();

        while (!isHealthy) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
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