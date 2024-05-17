package io.github.yeyuhl.novel.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.yeyuhl.novel.core.constant.CacheConsts;
import io.github.yeyuhl.novel.core.constant.DatabaseConsts;
import io.github.yeyuhl.novel.dao.entity.BookChapter;
import io.github.yeyuhl.novel.dao.entity.BookInfo;
import io.github.yeyuhl.novel.dao.mapper.BookChapterMapper;
import io.github.yeyuhl.novel.dao.mapper.BookInfoMapper;
import io.github.yeyuhl.novel.dto.resp.BookInfoRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小说信息缓存管理类
 *
 * @author yeyuhl
 * @date 2023/5/8
 */
@Component
@RequiredArgsConstructor
public class BookInfoCacheManager {

    private final BookInfoMapper bookInfoMapper;

    private final BookChapterMapper bookChapterMapper;

    /**
     * 从缓存中查询小说信息（先判断缓存中是否已存在，存在则直接从缓存中取，否则执行方法体中的逻辑后缓存结果）
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER, value = CacheConsts.BOOK_INFO_CACHE_NAME)
    public BookInfoRespDto getBookInfo(Long id) {
        return cachePutBookInfo(id);
    }

    /**
     * 从缓存中查询小说信息（不管缓存中是否存在要查询的小说的信息，都执行方法体中的逻辑，然后缓存起来）
     */
    @CachePut(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER, value = CacheConsts.BOOK_INFO_CACHE_NAME)
    public BookInfoRespDto cachePutBookInfo(Long id) {
        // 查询基础信息
        BookInfo bookInfo = bookInfoMapper.selectById(id);
        // 根据传入的小说id查询小说内容，然后将章节升序排序，最后返回一条记录（首章）
        QueryWrapper<BookChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper
            .eq(DatabaseConsts.BookChapterTable.COLUMN_BOOK_ID, id)
            .orderByAsc(DatabaseConsts.BookChapterTable.COLUMN_CHAPTER_NUM)
            .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        BookChapter firstBookChapter = bookChapterMapper.selectOne(queryWrapper);
        // 构建响应对象
        return BookInfoRespDto.builder()
            .id(bookInfo.getId())
            .bookName(bookInfo.getBookName())
            .bookDesc(bookInfo.getBookDesc())
            .bookStatus(bookInfo.getBookStatus())
            .authorId(bookInfo.getAuthorId())
            .authorName(bookInfo.getAuthorName())
            .categoryId(bookInfo.getCategoryId())
            .categoryName(bookInfo.getCategoryName())
            .commentCount(bookInfo.getCommentCount())
            .firstChapterId(firstBookChapter.getId())
            .lastChapterId(bookInfo.getLastChapterId())
            .picUrl(bookInfo.getPicUrl())
            .visitCount(bookInfo.getVisitCount())
            .wordCount(bookInfo.getWordCount())
            .build();
    }

    /**
     * 清除缓存
     */
    @CacheEvict(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER, value = CacheConsts.BOOK_INFO_CACHE_NAME)
    public void evictBookInfoCache(Long bookId) {
    }

    /**
     * 查询每个类别下最新更新的500个小说ID列表，并放入缓存中1个小时
     */
    @Cacheable(cacheManager = CacheConsts.CAFFEINE_CACHE_MANAGER, value = CacheConsts.LAST_UPDATE_BOOK_ID_LIST_CACHE_NAME)
    public List<Long> getLastUpdateIdList(Long categoryId) {
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.BookTable.COLUMN_CATEGORY_ID, categoryId)
            .gt(DatabaseConsts.BookTable.COLUMN_WORD_COUNT, 0)
            .orderByDesc(DatabaseConsts.BookTable.COLUMN_LAST_CHAPTER_UPDATE_TIME)
            .last(DatabaseConsts.SqlEnum.LIMIT_500.getSql());
        return bookInfoMapper.selectList(queryWrapper).stream().map(BookInfo::getId).toList();
    }

}
