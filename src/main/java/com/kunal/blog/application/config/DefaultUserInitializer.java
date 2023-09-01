package com.kunal.blog.application.config;

import com.kunal.blog.application.entity.User;
import com.kunal.blog.application.entity.UserRole;
import com.kunal.blog.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

//@Component
public class DefaultUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setUsername("Kunal");
        user.setPassword(passwordEncoder.encode("userpassword"));
        user.setRole(UserRole.AUTHOR);
        // Set other user properties
        userRepository.save(user);

        User artiUser = new User();
        artiUser.setUsername("arti");
        artiUser.setPassword(passwordEncoder.encode("artipassword"));
        artiUser.setRole(UserRole.AUTHOR);
        userRepository.save(artiUser);


        User admin = new User();
        admin.setUsername("Shubham");
        admin.setRole(UserRole.ADMIN);
        admin.setPassword(passwordEncoder.encode("adminpassword"));
        // Set other admin properties
        userRepository.save(admin);
    }
}
