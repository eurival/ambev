package com.ambev.order_viewer.service;

 
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ambev.order_viewer.domain.UserEntity;
import com.ambev.order_viewer.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Replace this with your UserRepository or any data source
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public CustomUserDetailsService(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database (replace with your entity)
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuari não encontrado: " + username));

        // Convert UserEntity to Spring Security's User
        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword()) // Password should be encoded (e.g., BCrypt)
                .authorities(userEntity.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .toArray(String[]::new)) // Convert roles to Spring Security authorities
                .build();
    }



    public static void main(String[] args) {
        // Cria uma instância do BCryptPasswordEncoder
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Define a senha que você deseja criptografar
        String rawPassword = "admin";

        // Criptografa a senha
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Exibe a senha criptografada no console
        System.out.println("Senha original: " + rawPassword);
        System.out.println("Senha criptografada: " + encodedPassword);
    }
}


