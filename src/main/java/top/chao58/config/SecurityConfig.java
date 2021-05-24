package top.chao58.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.chao58.security.RestfulAccessDeniedHandler;
import top.chao58.security.RestfulAuthenticationEntryPoint;
import top.chao58.security.TokenAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestfulAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private RestfulAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * 放行url
     */
    private final List<String> permitUrl = new ArrayList<>();
    /**
     * 需认证url
     */
    private final List<String> authenticationUrl = new ArrayList<>();

    {

        authenticationUrl.add("/r/*");

        // sagger3接口
        permitUrl.add("/swagger**/**");
        permitUrl.add("/webjars/**");
        permitUrl.add("/v3/**");
        // 管理接口
        permitUrl.add("/admin/registry");
        permitUrl.add("/admin/login");
        // 用户接口
        permitUrl.add("/member/getAuthCode");
        permitUrl.add("/member/verify");
        // 搜索接口
        permitUrl.add("/es/**");
        // 订单接口
        permitUrl.add("/order/**");
        permitUrl.add("/order/**");

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭csrf保护
        http.csrf().disable();
        // 不使用session管理
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 登录失败和权限不足的响应
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
        // token过滤器
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // url拦截
        /*http.authorizeRequests()
                .antMatchers(permitUrl.toArray(new String[0])).permitAll()
                .anyRequest().authenticated();*/
        http.authorizeRequests()
                .antMatchers(authenticationUrl.toArray(new String[0])).authenticated()
                .anyRequest().permitAll();
    }
}
