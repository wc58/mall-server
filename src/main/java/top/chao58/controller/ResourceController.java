package top.chao58.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"资源控制器"})
@RestController
@RequestMapping("/r")
public class ResourceController {

    @ApiOperation(value = "r1资源", notes = "需要r1的权限才可以访问")
    @PreAuthorize("hasAuthority('r1')")
    @GetMapping(value = "/r1", produces = "text/plain;charset=utf-8")
    public String r1() {
        return "r1资源";
    }

    @ApiOperation(value = "r2资源", notes = "需要pms:product:read的权限才可以访问")
    @PreAuthorize("hasAuthority('pms:product:read')")
    @GetMapping(value = "/r2", produces = "text/plain;charset=utf-8")
    public String r2() {
        return "r2资源";
    }

    @ApiOperation(value = "r3资源", notes = "登录即可访问")
    @GetMapping(value = "/r3", produces = "text/plain;charset=utf-8")
    public String r3() {
        return "r3资源";
    }


}
