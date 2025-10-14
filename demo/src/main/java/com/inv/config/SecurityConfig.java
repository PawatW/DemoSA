package com.inv.config;

import com.inv.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // React dev server
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(request -> request
                        // Public endpoints
                        .requestMatchers("/register", "/login", "/test").permitAll()

                        // Technician endpoints
                        .requestMatchers(HttpMethod.POST, "/requests").hasRole("Technician")

                        // Foreman endpoints
                        .requestMatchers(HttpMethod.GET, "/requests/pending").hasRole("Foreman")
                        .requestMatchers(HttpMethod.PUT, "/requests/{id}/approve").hasRole("Foreman")
                        .requestMatchers(HttpMethod.PUT, "/requests/{id}/reject").hasRole("Foreman")

                        // Authenticated endpoints (สำหรับ role อื่นๆ หรือ role ร่วม)
                        .requestMatchers(HttpMethod.GET, "/orders/confirmed").hasAnyRole("Technician", "Admin")
                        .requestMatchers(HttpMethod.GET, "/orders/{orderId}/items").hasAnyRole("Technician", "Admin", "Foreman")
                        .requestMatchers(HttpMethod.GET, "/requests/{requestId}/items").hasAnyRole("Technician", "Foreman", "Admin")

                        // เพิ่ม Rule สำหรับ Warehouse
                        .requestMatchers(HttpMethod.POST, "/stock/in").hasRole("warehouse")

                        .requestMatchers(HttpMethod.POST, "/customers").hasAnyRole("admin", "sales", "technician", "foreman")

                        .requestMatchers(HttpMethod.POST, "/suppliers").authenticated()

                        .requestMatchers("/staff/**").hasRole("admin")

                        // อนุญาตให้ warehouse สร้างสินค้าได้
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("warehouse")
                        // อนุญาตให้ทุกคนที่ login แล้วดึงข้อมูล Category ได้
                        .requestMatchers(HttpMethod.GET, "/categories").authenticated()

                        // เพิ่ม: Rules สำหรับการปิด Request
                        .requestMatchers(HttpMethod.GET, "/requests/ready-to-close").hasRole("warehouse")
                        .requestMatchers(HttpMethod.PUT, "/requests/{id}/close").hasRole("warehouse")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
