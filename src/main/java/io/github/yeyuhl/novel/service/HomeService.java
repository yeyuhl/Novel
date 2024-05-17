package io.github.yeyuhl.novel.service;

import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dto.resp.HomeBookRespDto;
import io.github.yeyuhl.novel.dto.resp.HomeFriendLinkRespDto;

import java.util.List;

/**
 * 首页模块服务类
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
public interface HomeService {

    /**
     * 首页小说推荐列表查询
     *
     * @return 首页小说推荐列表的 rest 响应结果
     */
    RestResp<List<HomeBookRespDto>> listHomeBooks();

    /**
     * 首页友情链接列表查询
     *
     * @return 友情链接列表
     */
    RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks();
}
