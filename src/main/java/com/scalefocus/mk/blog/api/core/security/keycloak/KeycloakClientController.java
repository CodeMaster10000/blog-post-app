package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Keycloak users and roles.
 * <p>
 * This controller provides endpoints for creating and deleting users and roles in Keycloak.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/keycloak")
final class KeycloakClientController {

  private final KeycloakClientService keycloakClientService;

  public KeycloakClientController(KeycloakClientService keycloakClientService) {
    this.keycloakClientService = keycloakClientService;
  }

  /**
   * Creates a new user in Keycloak.
   * <p>
   * This endpoint creates a new user with the specified username, password, and role.
   * </p>
   *
   * @param username the username of the new user
   * @param password the password of the new user
   * @param roleName the role to assign to the new user
   * @return a ResponseEntity indicating the result of the operation
   */
  @PostMapping("/users")
  public ResponseEntity<String> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String roleName) {
    keycloakClientService.createUser(username, password, roleName);
    return ResponseEntity.ok("User created successfully.");
  }

  /**
   * Deletes a user from Keycloak.
   * <p>
   * This endpoint deletes the user with the specified username.
   * </p>
   *
   * @param username the username of the user to delete
   * @return a ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/users/{username}")
  public ResponseEntity<String> deleteUser(@PathVariable String username) {
    keycloakClientService.deleteUser(username);
    return ResponseEntity.ok("User deleted successfully.");
  }

  /**
   * Creates a new role in Keycloak.
   * <p>
   * This endpoint creates a new role with the specified name.
   * </p>
   *
   * @param role the name of the new role
   * @return a ResponseEntity indicating the result of the operation
   */
  @PostMapping("/roles")
  public ResponseEntity<String> createRole(@RequestBody String role) {
    keycloakClientService.createRole(role);
    return ResponseEntity.ok("Role created successfully.");
  }

  /**
   * Deletes a role from Keycloak.
   * <p>
   * This endpoint deletes the role with the specified ID.
   * </p>
   *
   * @param roleId the ID of the role to delete
   * @return a ResponseEntity indicating the result of the operation
   */
  @DeleteMapping("/roles/delete/{roleId}")
  public ResponseEntity<String> deleteRole(@PathVariable String roleId) {
    keycloakClientService.deleteRole(roleId);
    return ResponseEntity.ok("Role deleted successfully.");
  }
}
