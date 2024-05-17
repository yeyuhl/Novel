package io.github.yeyuhl.novel.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.yeyuhl.novel.core.constant.CacheConsts;
import io.github.yeyuhl.novel.core.constant.DatabaseConsts;
import io.github.yeyuhl.novel.dao.entity.AuthorInfo;
import io.github.yeyuhl.novel.dao.mapper.AuthorInfoMapper;
import io.github.yeyuhl.novel.dto.AuthorInfoDto;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 作家信息缓存管理类
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
@Component
@RequiredArgsConstructor
public class AuthorInfoCacheManager {

    private final AuthorInfoMapper authorInfoMapper;

    /**
     * 查询作家信息并放入缓存中
     * Cacheable注解意味着这个方法的结果是可缓存的
     * cacheManager即指定缓存管理器，value即指定缓存名称，unless即条件表达式，用于确定何时不应将方法结果存储在缓存中，#result == null意味着结果为空不进行缓存
     */
    @Cacheable(cacheManager = CacheConsts.REDIS_CACHE_MANAGER, value = CacheConsts.AUTHOR_INFO_CACHE_NAME, unless = "#result == null")
    public AuthorInfoDto getAuthor(Long userId) {
        QueryWrapper<AuthorInfo> queryWrapper = new QueryWrapper<>();
        // last方法允许将SQL语句拼接到查询的最后，LIMIT_1指定查询结果只返回一条记录
        queryWrapper.eq(DatabaseConsts.AuthorInfoTable.COLUMN_USER_ID, userId)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        // 虽然authorInfoMapper没实现，但是由于继承了BaseMapper，因此能调用父类的方法
        AuthorInfo authorInfo = authorInfoMapper.selectOne(queryWrapper);
        if (Objects.isNull(authorInfo)) {
            return null;
        }
        // 如果authorInfo不为空，则构建AuthorInfoDto并返回，存入缓存中
        return AuthorInfoDto.builder()
                .id(authorInfo.getId())
                .penName(authorInfo.getPenName())
                .status(authorInfo.getStatus()).build();
    }

    /**
     * 调用此方法自动清除作家信息的缓存
     * CacheEvict注解用于清除缓存，cacheManager指定缓存管理器，value指定缓存名称
     */
    @CacheEvict(cacheManager = CacheConsts.REDIS_CACHE_MANAGER, value = CacheConsts.AUTHOR_INFO_CACHE_NAME)
    public void evictAuthorCache() {
    }

}
