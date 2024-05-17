package io.github.yeyuhl.novel.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.github.yeyuhl.novel.core.common.resp.PageRespDto;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.core.constant.EsConsts;
import io.github.yeyuhl.novel.dto.es.EsBookDto;
import io.github.yeyuhl.novel.dto.req.BookSearchReqDto;
import io.github.yeyuhl.novel.dto.resp.BookInfoRespDto;
import io.github.yeyuhl.novel.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 搜索模块，Elasticsearch搜索服务实现类
 *
 * @author yeyuhl
 * @date 2023/5/10
 */
@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class EsSearchServiceImpl implements SearchService {

    private final ElasticsearchClient esClient;

    @SneakyThrows
    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) {
        SearchResponse<EsBookDto> response = esClient.search(s -> {
                    // 指定索引名称
                    SearchRequest.Builder searchBuilder = s.index(EsConsts.BookIndex.INDEX_NAME);
                    // 构建检索条件
                    buildSearchCondition(condition, searchBuilder);
                    // 排序
                    if (!StringUtils.isBlank(condition.getSort())) {
                        searchBuilder.sort(o -> o.field(f -> f
                                .field(StringUtils.underlineToCamel(condition.getSort().split(" ")[0]))
                                .order(SortOrder.Desc))
                        );
                    }
                    // 分页
                    searchBuilder.from((condition.getPageNum() - 1) * condition.getPageSize())
                            .size(condition.getPageSize());
                    // 设置高亮显示
                    searchBuilder.highlight(h -> h.fields(EsConsts.BookIndex.FIELD_BOOK_NAME,
                                    t -> t.preTags("<em style='color:red'>").postTags("</em>"))
                            .fields(EsConsts.BookIndex.FIELD_AUTHOR_NAME,
                                    t -> t.preTags("<em style='color:red'>").postTags("</em>")));

                    return searchBuilder;
                },
                EsBookDto.class
        );

        // 获取搜索结果的总数，然后创建一个空的list对象，用于存储转换后的搜索结果
        TotalHits total = response.hits().total();
        List<BookInfoRespDto> list = new ArrayList<>();
        List<Hit<EsBookDto>> hits = response.hits().hits();
        // 遍历搜索结果中的每一条记录，并将其转换为 BookInfoRespDto 类型的对象
        for (var hit : hits) {
            EsBookDto book = hit.source();
            assert book != null;
            // 如果记录中包含高亮字段，则使用高亮字段替换原始字段
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME))) {
                book.setBookName(hit.highlight().get(EsConsts.BookIndex.FIELD_BOOK_NAME).get(0));
            }
            if (!CollectionUtils.isEmpty(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME))) {
                book.setAuthorName(hit.highlight().get(EsConsts.BookIndex.FIELD_AUTHOR_NAME).get(0));
            }
            list.add(BookInfoRespDto.builder()
                    .id(book.getId())
                    .bookName(book.getBookName())
                    .categoryId(book.getCategoryId())
                    .categoryName(book.getCategoryName())
                    .authorId(book.getAuthorId())
                    .authorName(book.getAuthorName())
                    .wordCount(book.getWordCount())
                    .lastChapterName(book.getLastChapterName())
                    .build());
        }
        assert total != null;
        return RestResp.ok(
                PageRespDto.of(condition.getPageNum(), condition.getPageSize(), total.value(), list));

    }

    /**
     * 构建检索条件
     */
    private void buildSearchCondition(BookSearchReqDto condition, SearchRequest.Builder searchBuilder) {
        // 构建布尔查询
        BoolQuery boolQuery = BoolQuery.of(b -> {

            // 只查询有字数的小说
            b.must(RangeQuery.of(m -> m.field(EsConsts.BookIndex.FIELD_WORD_COUNT).gt(JsonData.of(0)))._toQuery());

            // 如果condition（查询条件）的keyword不为空
            if (!StringUtils.isBlank(condition.getKeyword())) {
                // 关键词匹配，每个字段后面的数字是权重，越大对搜索结果影响就越大
                b.must((q -> q.multiMatch(t -> t
                        .fields(EsConsts.BookIndex.FIELD_BOOK_NAME + "^2",
                                EsConsts.BookIndex.FIELD_AUTHOR_NAME + "^1.8",
                                EsConsts.BookIndex.FIELD_BOOK_DESC + "^0.1")
                        .query(condition.getKeyword()))));
            }

            // 精确查询（作品方向和作品分类）
            if (Objects.nonNull(condition.getWorkDirection())) {
                b.must(TermQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORK_DIRECTION)
                        .value(condition.getWorkDirection()))._toQuery());
            }

            if (Objects.nonNull(condition.getCategoryId())) {
                b.must(TermQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_CATEGORY_ID)
                        .value(condition.getCategoryId()))._toQuery());
            }

            // 范围查询（字数和更新时间）
            if (Objects.nonNull(condition.getWordCountMin())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        .gte(JsonData.of(condition.getWordCountMin())))._toQuery());
            }

            if (Objects.nonNull(condition.getWordCountMax())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_WORD_COUNT)
                        .lt(JsonData.of(condition.getWordCountMax())))._toQuery());
            }

            if (Objects.nonNull(condition.getUpdateTimeMin())) {
                b.must(RangeQuery.of(m -> m
                        .field(EsConsts.BookIndex.FIELD_LAST_CHAPTER_UPDATE_TIME)
                        .gte(JsonData.of(condition.getUpdateTimeMin().getTime())))._toQuery());
            }

            return b;

        });
        searchBuilder.query(q -> q.bool(boolQuery));
    }
}
