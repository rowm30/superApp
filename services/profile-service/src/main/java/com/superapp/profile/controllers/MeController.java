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
        public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt){
            return jwt.getClaims();
        }

        @GetMapping("/seller-only")
        @PreAuthorize("hasRole('seller')")
        public String sellerOnly(){
            return "seller Dashboard";
        }

        @GetMapping("/headers")
        public Map<String, String> headers(@RequestHeader Map<String, String> headers){
            return headers;
        }
    }
