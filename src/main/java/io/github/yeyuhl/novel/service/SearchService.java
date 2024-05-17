package io.github.yeyuhl.novel.service;

import io.github.yeyuhl.novel.core.common.resp.PageRespDto;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dto.req.BookSearchReqDto;
import io.github.yeyuhl.novel.dto.resp.BookInfoRespDto;

/**
 * 搜索模块服务类
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
public interface SearchService {

    /**
     * 小说搜索
     *
     * @param condition 搜索条件
     * @return 搜索结果
     */
    RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition);

}
