package top.chao58.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.chao58.mapper.UmsAdminMapper;
import top.chao58.pojo.UmsAdmin;
import top.chao58.pojo.UmsPermission;
import top.chao58.security.TokenUtils;
import top.chao58.service.UmsAdminService;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin>
        implements UmsAdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenUtils tokenUtils;


    @Override
    public UmsAdmin getAdminByUsername(String username) {
        return baseMapper.getAdminByUsername(username);
    }

    @Override
    public List<UmsPermission> getPermissionsById(Long id) {
        return baseMapper.getPermissionsById(id);
    }

    @Override
    public UmsAdmin register(UmsAdmin admin) {
        // 查询是否注册过
        QueryWrapper<UmsAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", admin.getUsername());
        List<UmsAdmin> queryAdmins = baseMapper.selectList(queryWrapper);
        if (queryAdmins != null && queryAdmins.size() != 0) {
            return null;
        }
        // 对密码进行加密处理
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreateTime(new Date());
        // 插入数据库中
        baseMapper.insert(admin);
        return admin;
    }

    @Override
    public String login(String username, String password) {
        // 查询用户
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // 判断是否正确
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("{}，用户登录失败", username);
            return "";
        }
        log.info("用户详情：{}", userDetails);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
        return tokenUtils.generateToken(username);
    }
}




