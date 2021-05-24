package top.chao58.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import top.chao58.pojo.UmsAdmin;
import top.chao58.pojo.UmsPermission;
import top.chao58.security.AdminUserDetails;
import top.chao58.service.UmsAdminService;

import java.util.List;

@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UmsAdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("查询用户名：{}", username);
        UmsAdmin admin = adminService.getAdminByUsername(username);
        if (admin == null) {
            log.warn("{}：此用户名不存在", username);
            return null;
        }
        List<UmsPermission> permissions = adminService.getPermissionsById(admin.getId());
        log.info("{}：用户对应的权限：{}", username, permissions.toString());
        return new AdminUserDetails(admin, permissions);
    }
}
