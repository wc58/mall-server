package top.chao58.service;

import top.chao58.pojo.UmsAdmin;
import com.baomidou.mybatisplus.extension.service.IService;
import top.chao58.pojo.UmsPermission;

import java.util.List;

/**
 *
 */
public interface UmsAdminService extends IService<UmsAdmin> {

    UmsAdmin getAdminByUsername(String username);

    List<UmsPermission> getPermissionsById(Long id);

    UmsAdmin register(UmsAdmin adminParam);

    String login(String username, String password);

}
