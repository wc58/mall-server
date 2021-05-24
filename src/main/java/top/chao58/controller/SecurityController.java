package top.chao58.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "安全控制器")
@RestController
@RequestMapping("security")
public class SecurityController {


    @ApiOperation(value = "登录成功", notes = "登录成功后被调用")
    @PostMapping(value = "loginSuccess", produces = "text/plain;charset=utf-8")
    public String success() {
        return "恭喜你登录成功";
    }

}
