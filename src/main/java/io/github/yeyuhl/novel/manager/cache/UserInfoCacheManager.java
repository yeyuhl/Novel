package io.github.yeyuhl.novel.manager.cache;

import io.github.yeyuhl.novel.core.constant.CacheConsts;
import io.github.yeyuhl.novel.dao.entity.UserInfo;
import io.github.yeyuhl.novel.dao.mapper.UserInfoMapper;
import io.github.yeyuhl.novel.dto.UserInfoDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 用户信息缓存管理类
 *
 * @author yeyuhl
 * @date 2023/5/10
 */
@Component
@RequiredArgsConstructor
public class UserInfoCacheManager {

    private final UserInfoMapper userInfoMapper;

    /**
     * 查询用户信息，并放入缓存中
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER, value = CacheConsts.USER_INFO_CACHE_NAME)
    public UserInfoDto getUser(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return UserInfoDto.builder()
            .id(userInfo.getId())
            .status(userInfo.getStatus()).build();
    }


}
