package com.generation.farmacia.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.UserLogin;
import com.generation.farmacia.model.User;
import com.generation.farmacia.repository.UserRepository;
import com.generation.farmacia.security.JwtService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Optional<User> registerUser(User user) {
        if (userRepository.findByUser(user.getUser()).isPresent())
            return Optional.empty();

        user.setPassword(encryptPassword(user.getPassword()));

        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateUser(User user) {
        if (userRepository.findById(user.getId()).isPresent()) {

            Optional <User> searchUser = userRepository.findByUser(user.getUser());

            if ((searchUser.isPresent()) && (searchUser.get().getId() != user.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

        user.setPassword(encryptPassword(user.getPassword()));

        return Optional.ofNullable(userRepository.save(user));
        }
    return Optional.empty();
    }

    public Optional<UserLogin> authenticateUser(Optional<UserLogin> userLogin) {

        var credentials = new UsernamePasswordAuthenticationToken(userLogin.get().getUser(), userLogin.get().getPassword());

        Authentication authentication = authenticationManager.authenticate(credentials);

        if (authentication.isAuthenticated()) {

            Optional<User> user = userRepository.findByUser(userLogin.get().getUser());

            if (user.isPresent()) {
                userLogin.get().setId(user.get().getId());
                userLogin.get().setName(user.get().getName());
                userLogin.get().setPicture(user.get().getPicture());
                userLogin.get().setToken(generateToken(userLogin.get().getUser()));
                userLogin.get().setPassword("");

                return userLogin;
            }
        }

        return Optional.empty();
    }

    private String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private String generateToken(String user) {
        return "Bearer " + jwtService.generateToken(user);
    }

}
