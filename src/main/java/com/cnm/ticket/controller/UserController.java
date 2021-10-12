package com.cnm.ticket.controller;

import com.cnm.ticket.entity.User;
import com.cnm.ticket.entity.jwt.AuthenticationRequest;
import com.cnm.ticket.entity.jwt.AuthenticationResponse;
import com.cnm.ticket.service.UserService;
import com.cnm.ticket.util.JwtUtil;
import com.cnm.ticket.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtTokenUtil;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Response> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        String password = authenticationRequest.getPassword();
        String username = authenticationRequest.getUsername();

        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad Credentials!");
        }
        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        Response response = Response.builder()
                .success(true)
                .message("Login Successfully!")
                .data(new AuthenticationResponse(jwt))
                .code(200)
                .build();
        return ResponseEntity.ok(response);

    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)

    public ResponseEntity<Response> createUser(@RequestBody User user) throws Exception {
        userService.create(user);
        try {
            Response response = Response.builder()
                    .success(true)
                    .code(200)
                    .message("User has been created successfully!")
                    .data(user)
                    .build();
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            throw new Exception("Failed to create user!");
        }

    }
}
