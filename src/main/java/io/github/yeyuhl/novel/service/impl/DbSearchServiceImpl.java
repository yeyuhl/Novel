package io.github.yeyuhl.novel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.yeyuhl.novel.core.common.resp.PageRespDto;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dao.entity.BookInfo;
import io.github.yeyuhl.novel.dao.mapper.BookInfoMapper;
import io.github.yeyuhl.novel.dto.req.BookSearchReqDto;
import io.github.yeyuhl.novel.dto.resp.BookInfoRespDto;
import io.github.yeyuhl.novel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索模块，数据库搜索服务实现类
 *
 * @author yeyuhl
 * @date 2023/5/10
 */
@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "enabled", havingValue = "false")
@Service
@RequiredArgsConstructor
@Slf4j
public class DbSearchServiceImpl implements SearchService {

    private final BookInfoMapper bookInfoMapper;

    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {
        Page<BookInfoRespDto> page = new Page<>();
        page.setCurrent(condition.getPageNum());
        page.setSize(condition.getPageSize());
        List<BookInfo> bookInfos = bookInfoMapper.searchBooks(page, condition);
        return RestResp.ok(PageRespDto.of(condition.getPageNum(), condition.getPageSize(), page.getTotal(),
                bookInfos.stream().map(v -> BookInfoRespDto.builder()
                        .id(v.getId())
                        .bookName(v.getBookName())
                        .categoryId(v.getCategoryId())
                        .categoryName(v.getCategoryName())
                        .authorId(v.getAuthorId())
                        .authorName(v.getAuthorName())
                        .wordCount(v.getWordCount())
                        .lastChapterName(v.getLastChapterName())
                        .build()).toList()));
    }

}
