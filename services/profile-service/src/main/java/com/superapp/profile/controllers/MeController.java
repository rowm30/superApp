    package com.superapp.profile.controllers;


    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.oauth2.jwt.Jwt;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestHeader;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.Map;

    @RestController
    public class MeController {

        @GetMapping("/me")
        public Map<String, Object> me(
                @RequestHeader(value = "X-Passport-Sub", required = false) String sub,
                @RequestHeader(value = "X-Passport-Username", required = false) String username,
                @RequestHeader(value = "X-Passport-Sid", required = false) String sid)
        {
            return Map.of(
                    "sub", sub == null ? "" : sub,
                    "username", username==null ? "" : username,
                    "sid", sid == null ? "" : sid,
                    "source", "passport"
            );
        }

        @GetMapping("/headers")
        public Map<String, String> headers(@RequestHeader Map<String, String> headers){
            return headers;
        }
    }
