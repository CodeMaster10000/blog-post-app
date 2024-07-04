package com.scalefocus.mk.blog.api.core.security.keycloak;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keycloak")
final class KeycloakClientController {

  private final KeycloakClientService keycloakClientService;

  public KeycloakClientController(KeycloakClientService keycloakClientService) {
    this.keycloakClientService = keycloakClientService;
  }

  @PostMapping("/users")
  public ResponseEntity<String> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String roleName) {
    keycloakClientService.createUser(username, password, roleName);
    return ResponseEntity.ok("User created successfully.");
  }

  @DeleteMapping("/users/{username}")
  public ResponseEntity<String> deleteUser(@PathVariable String username) {
    keycloakClientService.deleteUser(username);
    return ResponseEntity.ok("User deleted successfully.");
  }

  @PostMapping("/roles")
  public ResponseEntity<String> createRole(@RequestBody String role) {
    keycloakClientService.createRole(role);
    return ResponseEntity.ok("Role created successfully.");
  }

  @DeleteMapping("/roles/delete/{roleId}")
  public ResponseEntity<String> deleteRole(@PathVariable String roleId) {
    keycloakClientService.deleteRole(roleId);
    return ResponseEntity.ok("Role deleted successfully.");
  }

}
