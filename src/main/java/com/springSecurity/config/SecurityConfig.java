package com.springSecurity.config;

import com.springSecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

//(debug = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //указываем, куда у кадого пользователя есть доступ, а куда нет -
        // все настройки для условий в хода и т.д.


        //не пускаем пользователей по адресу "/authenticated/**"
       http.authorizeRequests()
               .antMatchers("/authenticated/**").authenticated()
               .antMatchers("/only_for_admins").hasRole("Admin")
               .antMatchers("/read_profile").hasAuthority("READ_PROFILE")
             // пропуск на адрес по роли пользователя
              // .antMatchers("/admin/**").hasAnyRole("ADMIN","SUPERADMIN")

             //в профиле могут быть только аутент. пользователи
              // .antMatchers("/profile/**").authenticated()

               // это пропуск по авторитету ????
              // .antMatchers("/profile/**").hasAuthority()

               .and()
               //базовая аутентификация, вылетит окно для прохода аутент-ции, если еще не аут-ан пользователь
               //.httpBasic()

               //генерирует стандартную форму для логир-я, аналог .httpBasic()
               .formLogin()

               //определяем нестандартный урл куда летит пост запрос
              // .loginProcessingUrl("/hellologin")

               // обработает после успешной аут-ции
               //.successHandler()
               .and()
               //при выходе настраиваем переброс на корень приложения например
               .logout().logoutSuccessUrl("/");

    }
//In memory users 1
//    @Bean
//    public UserDetailsService users(){
//        //UserDetails - мин инфа о пользователях
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2y$12$84d5n4cWzICabqMlVi4G3OdOpQazqg7j4ds.fvOE422nEf0WplEtK")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2y$12$84d5n4cWzICabqMlVi4G3OdOpQazqg7j4ds.fvOE422nEf0WplEtK")
//                .roles("ADMIN","USER")
//                .build();
//        return new InMemoryUserDetailsManager(user,admin);
//    }

//jdbc authentication 2 - в базе(в стандартных таблицах)
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource){
//                UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2y$12$84d5n4cWzICabqMlVi4G3OdOpQazqg7j4ds.fvOE422nEf0WplEtK")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2y$12$84d5n4cWzICabqMlVi4G3OdOpQazqg7j4ds.fvOE422nEf0WplEtK")
//                .roles("ADMIN","USER")
//                .build();
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//        //
//        if (jdbcUserDetailsManager.userExists(admin.getUsername())){
//            jdbcUserDetailsManager.deleteUser(admin.getUsername());
//        }
//        jdbcUserDetailsManager.createUser(user);
//        jdbcUserDetailsManager.createUser(admin);
//        return jdbcUserDetailsManager;
//    }


    //Jdbc аутентификация + бины снизу
//    @Bean
//    public JdbcUserDetailsManager users(DataSource dataSource){
//        return  new JdbcUserDetailsManager(dataSource);
//    }


    //свои собственные таблицы и сущности
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        //проверяет наличие пользователя, если есть кладет в контекст
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }




}
