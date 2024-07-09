package com.scalefocus.mk.blog.api.core.security.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for managing Keycloak users and roles.
 * <p>
 * This service provides methods for creating and deleting users and roles in Keycloak.
 * It interacts with the Keycloak Admin API to perform these operations.
 * </p>
 */
@Service
final class KeycloakClientService {

  private final Keycloak keycloak;

  private final String keycloakRealm;

  KeycloakClientService(Keycloak keycloak, @Value("${keycloak.realm}") String keycloakRealm) {
    this.keycloak = keycloak;
    this.keycloakRealm = keycloakRealm;
  }

  /**
   * Creates a new user in Keycloak.
   * <p>
   * This method creates a user with the specified username, password, and role in the configured Keycloak realm.
   * </p>
   *
   * @param username the username of the new user
   * @param password the password of the new user
   * @param roleName the role to assign to the new user
   */
  void createUser(String username, String password, String roleName) {
    UserRepresentation user = createUserRepresentation(username);
    setUserCredentials(password, user);
    RealmResource realmResource = getRealmResource();
    UsersResource usersResource = realmResource.users();
    try (Response response = usersResource.create(user)) {
      validateResponse(response.getStatus(), 201, "Failed to create User");
      String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
      assignRoleToUser(roleName, realmResource, usersResource, userId);
    }
  }

  private static void setUserCredentials(String password, UserRepresentation user) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(password);
    credential.setTemporary(false);
    user.setCredentials(Collections.singletonList(credential));
  }

  private static UserRepresentation createUserRepresentation(String username) {
    UserRepresentation user = new UserRepresentation();
    user.setEnabled(true);
    user.setUsername(username);
    return user;
  }

  private RealmResource getRealmResource() {
    return keycloak.realm(keycloakRealm);
  }

  private static void assignRoleToUser(String roleName, RealmResource realmResource, UsersResource usersResource, String userId) {
    RoleRepresentation roleRepresentation = realmResource.roles().get(roleName).toRepresentation();
    usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
  }

  /**
   * Deletes a user from Keycloak.
   * <p>
   * This method deletes the user with the specified username from the configured Keycloak realm.
   * </p>
   *
   * @param username the username of the user to delete
   */
  void deleteUser(String username) {
    RealmResource realmResource = keycloak.realm(keycloakRealm);
    UsersResource usersResource = realmResource.users();

    List<UserRepresentation> users = usersResource.search(username);
    for (UserRepresentation user : users) {
      if (user.getUsername().equals(username)) {
        try (Response deletedResponse = usersResource.delete(user.getId())) {
          validateResponse(deletedResponse.getStatus(), 200, "Failed to delete User");
        }
      }
    }
  }

  private static void validateResponse(int status, int expectedStatus, String message) {
    if (status != expectedStatus) {
      throw new SecurityResourceException(message);
    }
  }

  /**
   * Creates a new role in Keycloak.
   * <p>
   * This method creates a role with the specified name in the configured Keycloak realm.
   * </p>
   *
   * @param role the name of the new role
   */
  void createRole(String role) {
    RealmResource realmResource = getRealmResource();
    RolesResource rolesResource = realmResource.roles();
    RoleRepresentation roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName(role);
    roleRepresentation.setClientRole(false);
    rolesResource.create(roleRepresentation);
  }

  /**
   * Deletes a role from Keycloak.
   * <p>
   * This method deletes the role with the specified ID from the configured Keycloak realm.
   * </p>
   *
   * @param roleId the ID of the role to delete
   */
  void deleteRole(String roleId) {
    RealmResource realmResource = getRealmResource();
    RolesResource rolesResource = realmResource.roles();
    rolesResource.deleteRole(roleId);
  }
}
