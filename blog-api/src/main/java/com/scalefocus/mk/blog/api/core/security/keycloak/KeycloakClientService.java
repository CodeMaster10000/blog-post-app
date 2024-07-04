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

@Service
final class KeycloakClientService {

  private final Keycloak keycloak;
  private final String keycloakRealm;

  KeycloakClientService(Keycloak keycloak, @Value("${keycloak.realm}") String keycloakRealm) {
    this.keycloak = keycloak;
    this.keycloakRealm = keycloakRealm;
  }

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

  void createRole(String role) {
    RealmResource realmResource = getRealmResource();
    RolesResource rolesResource = realmResource.roles();
    RoleRepresentation roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName(role);
    roleRepresentation.setClientRole(false);
    rolesResource.create(roleRepresentation);
  }

  void deleteRole(String roleId) {
    RealmResource realmResource = getRealmResource();
    RolesResource rolesResource = realmResource.roles();
    rolesResource.deleteRole(roleId);
  }

}
