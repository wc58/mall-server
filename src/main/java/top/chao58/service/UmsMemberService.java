package top.chao58.service;

import top.chao58.pojo.UmsMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface UmsMemberService extends IService<UmsMember> {

    String generateAuthCode(String telephone);

    Boolean verifyAuthCode(String telephone, String authCode);


}
