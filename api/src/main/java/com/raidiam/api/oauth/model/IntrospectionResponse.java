package com.raidiam.api.oauth.model;

import java.util.Arrays;
import java.util.List;

public class IntrospectionResponse {

    private boolean active;
    private String clientId;
    private String scope;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean hasScope(String requiredScope) {
        if (scope == null || scope.isEmpty()) {
            return false;
        }
        List<String> scopes = Arrays.asList(scope.split(" "));
        return scopes.contains(requiredScope);
    }
}
