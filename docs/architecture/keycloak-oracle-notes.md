# Keycloak oracle findings

- access token TTL: 300s (ADR-0001 ka 5 min match — decision confirmed)
- refresh TTL: 1800s (ADR mein 30 din likha — revisit karna hai)
- access token aud = "account", id token aud = "web-shell"
  -> audience validation config-driven rakhna, hardcode nahi (Cognito client_id use karta hai)
- access/id = RS256, refresh = HS512 (symmetric, kyunki sirf IdP verify karta hai)
- sid claim = session id, revocation ke liye yahi key hai
- PKCE mismatch: HTTP 400, invalid_grant
