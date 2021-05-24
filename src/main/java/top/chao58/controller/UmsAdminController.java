package top.chao58.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chao58.pojo.UmsAdmin;
import top.chao58.service.UmsAdminService;
import top.chao58.util.R;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "管理员控制器")
@RestController
@RequestMapping("/admin")
public class UmsAdminController {

    @Autowired
    private UmsAdminService adminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;


    @ApiOperation("注册管理员")
    @PostMapping("/registry")
    public R register(@RequestBody UmsAdmin adminParam) {
        UmsAdmin admin = adminService.register(adminParam);
        if (admin == null) {
            return R.error().setMessage("注册失败");
        }
        return R.ok();
    }

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public R login(@RequestBody UmsAdmin adminParam) {
        String username = adminParam.getUsername();
        String password = adminParam.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return R.error().setMessage("账号或密码为空");
        }
        String token = adminService.login(username, password);
        if (StrUtil.isBlank(token)) {
            return R.error().setMessage("登录失败");
        }
        log.info("{}：对应的token：{}", username, token);
        return R.ok().put("tokenHeader", tokenHeader).put("token", token);
    }


}
