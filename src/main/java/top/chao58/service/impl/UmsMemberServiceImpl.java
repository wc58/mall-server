package top.chao58.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import top.chao58.pojo.UmsMember;
import top.chao58.service.RedisService;
import top.chao58.service.UmsMemberService;
import top.chao58.mapper.UmsMemberMapper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Log4j2
@Service
public class UmsMemberServiceImpl extends ServiceImpl<UmsMemberMapper, UmsMember> implements UmsMemberService {

    @Autowired
    private RedisService redisService;

    @Value("${mall.authcode.prefix}")
    private String AUTH_CODE_PREFIX;
    @Value("${mall.authcode.expire}")
    private Long AUTH_CODE_EXPIRE;


    @Override
    public String generateAuthCode(String telephone) {
        // 生成随机验证码
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            builder.append(random.nextInt(10));
        }
        String authCode = builder.toString();
        log.info("请求的电话号码：{}，对应的验证码：{}", telephone, authCode);
        // 保存到redis
        redisService.set(AUTH_CODE_PREFIX + telephone, authCode);
        // 设置过期时间
        redisService.expire(AUTH_CODE_PREFIX + telephone, AUTH_CODE_EXPIRE);
        // 返回给前端
        return authCode;
    }

    @Override
    public Boolean verifyAuthCode(String telephone, String authCode) {
        log.info("请求的电话号码：{}，请求的验证码：{}", telephone, authCode);
        // 首先根据电话号码取出验证码
        String realCode = redisService.get(AUTH_CODE_PREFIX + telephone);
        log.info("真实的验证码：{}", authCode);
        if (StrUtil.isBlank(realCode)) {
            return false;
        }
        // 不为空的前提下，判断验证码是否正确
        return realCode.equals(authCode);
    }
}




