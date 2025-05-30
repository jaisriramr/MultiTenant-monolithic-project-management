package com.projectmanagement.multitenantprojectmanagement.auth0;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Auth0Service {

    private final Auth0Client auth0Client;
    private final Auth0Config auth0Config;

    public ResponseEntity<Map<String, Object>> inviteUserToOrganization(String orgId, String inviterName, String email, String roleId) {

        Map<String, String> inviter = Map.of("name", inviterName);
        Map<String, String> invitee = Map.of("email", email);

        Map<String, Object> requestBody = Map.of(
                "client_id", auth0Config.getClientId(),
                "inviter", inviter,
                "invitee", invitee,
                "roles", List.of(roleId));

        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId + "/invitations";

        Map<String, Object> response = auth0Client.makeApiRequest(HttpMethod.POST, url, requestBody, true).getBody();

        if (response != null) {
            // return response.get("id").toString();
            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("Error while sending invite to user");

        }
    }

    public void revokeInvitationSentToAnUser(String orgId, String invitationId) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId + "/invitations/"
                + invitationId;

        auth0Client.makeApiRequest(HttpMethod.DELETE, url, null, true);
    }

    public ResponseEntity<Map<String, Object>> addMembersToAnOrg(String orgId, List<String> userIds) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/"+ orgId +"/members";
        Map<String, Object> requestBody = Map.of("members", userIds);

        return auth0Client.makeApiRequest(HttpMethod.POST, url, requestBody, true);
    }

    public ResponseEntity<Map<String, Object>> removeMembersFromAnOrg(String orgId, List<String> userIds) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/"+ orgId +"/members";
        Map<String, Object> requestBody = Map.of("members", userIds);
        return auth0Client.makeApiRequest(HttpMethod.DELETE, url, requestBody, true);
    }

    public ResponseEntity<Map<String, Object>> createAnOrganization(String name, String displayName) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations";

        String params = "{"
                + "\"name\":\"" + name + "\","
                + "\"display_name\":\"" + name + "\","
                + "\"branding\":{"
                + "\"logo_url\":\"https://images.unsplash.com/photo-1657807783435-a352520a37b1\","
                + "\"colors\":{"
                + "\"primary\":\"#ffffff\","
                + "\"page_background\":\"#f4f4f4\""
                + "}"
                + "},"
                + "\"metadata\":{},"
                + "\"enabled_connections\":[{"
                + "\"connection_id\":\"" + auth0Config.getConnectionId() + "\","
                + "\"assign_membership_on_login\":true,"
                + "\"show_as_button\":true,"
                + "\"is_signup_enabled\":true"
                + "}]"
                + "}";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(params, new TypeReference<Map<String, Object>>() {
            });
            
            return auth0Client.makeApiRequest(HttpMethod.POST, url, body, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error Processing Json",e);
        }
    }

    public void updateAnOrganization(String orgId, String name, String displayName) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId;
        String params = "{\"name\":\""+ name +"\",\"display_name\":\""+ name +"\",\"branding\":{\"logo_url\":\"https://images.unsplash.com/photo-1657807783435-a352520a37b1\",\"colors\":{\"primary\":\"#ffffff\",\"page_background\":\"#f4f4f4\"}},\"metadata\":{}}";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(params, new TypeReference<Map<String, Object>>() {
            });
            
            auth0Client.makeApiRequest(HttpMethod.PATCH, url, body, true);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error Processing Json",e);
        }
    }

    public void deleteAnOrganization(String orgId) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId;
        auth0Client.makeApiRequest(HttpMethod.DELETE, url, null, true);
    }

    public ResponseEntity<Map<String, Object>> createARole(String name, String description) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/roles";

        Map<String, Object> requestBody = Map.of(
            "name", name,
            "description", description
        );
        
        return auth0Client.makeApiRequest(HttpMethod.POST, url, requestBody, true);
    }

    public ResponseEntity<Map<String, Object>> updateARole(String roleId, String name, String description) {

        String url = "https://" + auth0Config.getDomain() + "/api/v2/roles/" + roleId;
        
        Map<String, Object> requestBody = Map.of(
            "name", name,
            "description", description
        );

        return auth0Client.makeApiRequest(HttpMethod.PATCH, url, requestBody, true);
    }

    public void assignRolesToUser(String orgId,String userId, List<String> roles) {
        // /api/v2/organizations/:id/members/:user_id/roles
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId + "/members/" + userId + "/roles";
        Map<String, Object> requestBody = Map.of("roles", roles);

        auth0Client.makeApiRequest(HttpMethod.POST, url, requestBody, true);
    }

    public void removeRolesFromUser(String orgId,String userId, List<String> roles) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/organizations/" + orgId + "/members/" + userId + "/roles";

        Map<String, Object> requestBody = Map.of("roles", roles);

        auth0Client.makeApiRequest(HttpMethod.DELETE, url, requestBody, true);
    }

    public void removeRoleFromAuth0(String roleId) {
        String url = "https://" +  auth0Config.getDomain() + "/api/v2/roles/" + roleId;
        auth0Client.makeApiRequest(HttpMethod.DELETE, url, null, true);
    }

    public void assignOrRemovePermissionToARole(String roleId, List<String> permissions) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/roles/" + roleId + "/permissions";
    
        Stream<Object> permissionList = permissions.stream().map(permission -> Map.of("resource_server_identifier", auth0Config.getIdentifier(), "permission_name", permission));
        Map<String, Object> requestbody = Map.of("permissions", permissionList);

        auth0Client.makeApiRequest(HttpMethod.POST, url, requestbody, true);
    }

    public void removePermissionFromARole(String roleId, List<String> permissions) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/roles/" + roleId + "/permissions";
    
        Stream<Object> permissionList = permissions.stream().map(permission -> Map.of("resource_server_identifier", auth0Config.getIdentifier(), "permission_name", permission));
        Map<String, Object> requestbody = Map.of("permissions", permissionList);

        auth0Client.makeApiRequest(HttpMethod.DELETE, url, requestbody, true);
    }

    public ResponseEntity<Map<String, Object>> createOrUpdateOrDeletePermission(Map<String, Object> permissions) {
        String url = "https://" + auth0Config.getDomain() + "/api/v2/resource-servers/" + auth0Config.getApiId();

        return auth0Client.makeApiRequest(HttpMethod.PATCH, url, permissions, true);
    }

}
