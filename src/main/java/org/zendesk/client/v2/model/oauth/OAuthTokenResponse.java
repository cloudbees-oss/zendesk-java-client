package org.zendesk.client.v2.model.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuth token response.
 *
 * @author patrikvarga
 * @since 15/12/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthTokenResponse {

    private String accessToken;
    private String tokenType;
    private String scope;

    public OAuthTokenResponse() {
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
