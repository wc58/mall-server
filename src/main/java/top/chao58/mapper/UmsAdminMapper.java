package top.chao58.mapper;

import top.chao58.pojo.UmsAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chao58.pojo.UmsPermission;

import java.util.List;

/**
 * @Entity top.chao58.pojo.UmsAdmin
 */
public interface UmsAdminMapper extends BaseMapper<UmsAdmin> {

    UmsAdmin getAdminByUsername(String username);

    List<UmsPermission> getPermissionsById(Long id);
}




