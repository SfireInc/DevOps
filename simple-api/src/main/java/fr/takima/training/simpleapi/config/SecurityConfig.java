package fr.takima.training.simpleapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private static final String DEFAULT_USER = "admin";
    private static final String PASSWORD_FILE = "admin-password.properties";
    private static final String PASSWORD_PROPERTY = "admin.password";
    
    @Value("${app.security.password:}")
    private String configuredPassword;
    
    private String password;

    @PostConstruct
    public void init() {
        // Check if a password is already configured in application properties
        if (configuredPassword != null && !configuredPassword.isEmpty()) {
            password = configuredPassword;
            logger.info("Using configured admin password from application properties");
            return;
        }
        
        // Check if we have a stored password file from previous deployment
        Path passwordFilePath = Paths.get(PASSWORD_FILE);
        if (Files.exists(passwordFilePath)) {
            try {
                Properties props = new Properties();
                props.load(Files.newInputStream(passwordFilePath));
                password = props.getProperty(PASSWORD_PROPERTY);
                if (password != null && !password.isEmpty()) {
                    logger.info("Using existing admin password from previous deployment");
                    return;
                }
            } catch (IOException e) {
                logger.warn("Failed to read existing password file", e);
            }
        }
        
        // Generate a new random password for first deployment
        generateAndSavePassword();
    }
    
    private void generateAndSavePassword() {
        // Generate a random password
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16]; // 128 bits
        random.nextBytes(bytes);
        password = Base64.getEncoder().encodeToString(bytes);
        
        // Log the generated password
        logger.info("========================================================");
        logger.info("Generated admin password for first deployment: {}", password);
        logger.info("Please save this password for future use");
        logger.info("========================================================");
        
        // Save the password to a file for future application restarts
        try {
            Properties props = new Properties();
            props.setProperty(PASSWORD_PROPERTY, password);
            File file = new File(PASSWORD_FILE);
            try (FileOutputStream out = new FileOutputStream(file)) {
                props.store(out, "Admin password for simple-api");
            }
            logger.info("Saved admin password to {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.warn("Failed to save admin password to file", e);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/departments/**").authenticated()
                .antMatchers("/students/**").authenticated()
                .anyRequest().permitAll()
            .and()
            .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser(DEFAULT_USER)
            .password(passwordEncoder().encode(password))
            .roles("USER", "ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}