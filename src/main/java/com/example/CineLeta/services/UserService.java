package com.example.CineLeta.services;

import com.example.CineLeta.models.User;
import com.example.CineLeta.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuthPostLogin(String email, String googleId, String name){
        Optional<User> usuario = userRepository.findByGoogleId(googleId);

        if(usuario.isPresent()){
            return usuario.get();
        }

        User user = new User();
        user.setEmail(email);
        user.setGoogleId(googleId);
        user.setNickname(name);

        userRepository.save(user);

        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

}
