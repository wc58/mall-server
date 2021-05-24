package top.chao58.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.chao58.service.UmsMemberService;
import top.chao58.util.R;

@Api(tags = "用户管理控制器")
@RequestMapping("/member")
@RestController
public class UmsMemberController {

    @Autowired
    private UmsMemberService memberService;

    @ApiOperation(value = "获取验证码", notes = "根据电话号码获取验证码")
    @GetMapping("/getAuthCode")
    public R getAuthCode(@Parameter(description = "电话号码", required = true) @RequestParam String telephone) {
        return R.ok().put("code", memberService.generateAuthCode(telephone));
    }

    @ApiOperation(value = "校验验证码", notes = "根据电话号码验证码是否正确")
    @GetMapping("/verify")
    public R verifyAuthCode(@Parameter(description = "电话号码", required = true) @RequestParam String telephone,
                            @Parameter(description = "验证码", required = true) @RequestParam String code) {
        Boolean valid = memberService.verifyAuthCode(telephone, code);
        return valid ? R.ok().setMessage("验证码正确") : R.error().setMessage("验证码错误");
    }

}
