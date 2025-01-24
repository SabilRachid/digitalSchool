package com.digital.school.security;

import com.digital.school.model.User;
import com.digital.school.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);    

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("Recherche de l'utilisateur avec le nom : {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    LOGGER.error("Utilisateur non trouvé : {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé : " + username);
                });

        if (!user.isEnabled()) {
            LOGGER.warn("Utilisateur désactivé : {}", username);
            throw new UsernameNotFoundException("Utilisateur désactivé : " + username);
        }

        return user;
    }
}