package com.kapkiai.smpp.controllers;

import com.kapkiai.smpp.models.AuthRequest;
import com.kapkiai.smpp.models.AuthResponse;
import com.kapkiai.smpp.services.MyUserDetailsService;
import com.kapkiai.smpp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.kapkiai.smpp.utils.GlobalVariables.EXPIRY_ADD;

@RestController
public class TokenController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/auth/generate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(),
                    authRequest.getPassWord()));
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect credentials provided.");
        }

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(authRequest.getUserName());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token,EXPIRY_ADD));

    }
}
