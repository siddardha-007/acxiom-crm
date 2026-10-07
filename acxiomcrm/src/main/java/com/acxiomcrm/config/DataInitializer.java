package com.acxiomcrm.config;

import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.enums.Role;
import com.acxiomcrm.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

   @Bean
   CommandLineRunner createDefaultAdmin(
           AppUserRepository userRepository,
           PasswordEncoder passwordEncoder
   ) {

       return args -> {

           if (!userRepository.existsByEmail(
                   "admin@acxiomcrm.com"
           )) {

               AppUser admin = new AppUser();

               admin.setName("System Admin");

               admin.setEmail(
                       "admin@acxiomcrm.com"
               );

               admin.setPassword(
                       passwordEncoder.encode(
                               "Admin@123"
                       )
               );

               admin.setRole(Role.ADMIN);

               admin.setActive(true);

               userRepository.save(admin);

               System.out.println(
                       "===================================="
               );

               System.out.println(
                       "Default Admin Created"
               );

               System.out.println(
                       "Email: admin@acxiomcrm.com"
               );

               System.out.println(
                       "Password: Admin@123"
               );

               System.out.println(
                       "===================================="
               );
           }
       };
   }
}