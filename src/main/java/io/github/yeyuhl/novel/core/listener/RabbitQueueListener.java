package io.github.yeyuhl.novel.core.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import io.github.yeyuhl.novel.core.constant.AmqpConsts;
import io.github.yeyuhl.novel.core.constant.EsConsts;
import io.github.yeyuhl.novel.dao.entity.BookInfo;
import io.github.yeyuhl.novel.dao.mapper.BookInfoMapper;
import io.github.yeyuhl.novel.dto.es.EsBookDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ队列监听器
 *
 * @author yeyuhl
 * @date 2023/5/18
 */
@Component
@ConditionalOnProperty(prefix = "spring", name = {"elasticsearch.enabled", "amqp.enabled"}, havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class RabbitQueueListener {

    private final BookInfoMapper bookInfoMapper;

    private final ElasticsearchClient esClient;

    /**
     * 监听小说信息改变的ES更新队列，更新最新小说信息到ES
     * RabbitListener注解是让Spring监听指定的队列，当队列中有消息时，Spring会自动接收消息，然后调用方法处理
     */
    @RabbitListener(queues = AmqpConsts.BookChangeMq.QUEUE_ES_UPDATE)
    @SneakyThrows
    public void updateEsBook(Long bookId) {
        BookInfo bookInfo = bookInfoMapper.selectById(bookId);
        // index方法用于索引文档，如果文档不存在则创建，如果存在则更新
        IndexResponse response = esClient.index(i -> i
                .index(EsConsts.BookIndex.INDEX_NAME)
                .id(bookInfo.getId().toString())
                .document(EsBookDto.build(bookInfo))
        );
        log.info("Indexed with version " + response.version());
    }

}
