package io.github.yeyuhl.novel.service.impl;

import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dto.resp.HomeBookRespDto;
import io.github.yeyuhl.novel.dto.resp.HomeFriendLinkRespDto;
import io.github.yeyuhl.novel.manager.cache.FriendLinkCacheManager;
import io.github.yeyuhl.novel.manager.cache.HomeBookCacheManager;
import io.github.yeyuhl.novel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页模块服务实现类
 *
 * @author yeyuhl
 * @date 2023/5/9
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeBookCacheManager homeBookCacheManager;

    private final FriendLinkCacheManager friendLinkCacheManager;

    @Override
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        return RestResp.ok(homeBookCacheManager.listHomeBooks());
    }

    @Override
    public RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks() {
        return RestResp.ok(friendLinkCacheManager.listFriendLinks());
    }
}
