package io.github.yeyuhl.novel.core.interceptor;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yeyuhl.novel.core.common.constant.ErrorCodeEnum;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.core.common.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 流量限制拦截器：实现接口防刷和限流
 *
 * @author yeyuhl
 * @date 2023/5/17
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FlowLimitInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    /**
     * novel 项目所有的资源
     */
    private static final String NOVEL_RESOURCE = "novelResource";

    static {
        // 接口限流规则：所有的请求，限制每秒最多只能通过2000个，超出限制匀速排队，利用sentinel实现
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule1 = new FlowRule();
        rule1.setResource(NOVEL_RESOURCE);
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 限制每秒最多只能通过2000个请求(QPS=2000)
        rule1.setCount(2000);
        rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        rules.add(rule1);
        FlowRuleManager.loadRules(rules);

        // 接口防刷规则1：所有的请求，限制每个IP每秒最多只能通过50个，超出直接拒绝
        ParamFlowRule rule2 = new ParamFlowRule(NOVEL_RESOURCE)
            .setParamIdx(0)
            .setCount(50);
        // 接口防刷规则2：所有的请求，限制每个IP每分钟最多只能通过1000个，超出直接拒绝
        ParamFlowRule rule3 = new ParamFlowRule(NOVEL_RESOURCE)
            .setParamIdx(0)
            .setCount(1000)
            .setDurationInSec(60);
        ParamFlowRuleManager.loadRules(Arrays.asList(rule2, rule3));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = IpUtils.getRealIp(request);
        Entry entry = null;
        try {
            //使用SphU.entry()函数获取名为NOVEL_RESOURCE的资源，并指定该资源一次只能由单个线程访问(EntryType.IN)
            // resource：要获取的资源的名称
            // entryType：条目的类型，在本例中，我们使用的是 EntryType.IN，这表明我们想要获取一次只能由单个线程访问的资源（避免竞争）
            // acquireCount：获取资源的次数，大多数情况下，只获取一次资源
            // ip：请求访问资源的客户端的IP地址
            // 如果资源可用，SphU.entry()函数将返回一个Entry对象，Entry对象可用于在不再需要时释放资源
            // 如果资源不可用，SphU.entry()函数将抛出BlockException，应用程序可以处理此异常以采取适当的操作，例如重试请求或向用户显示错误消息
            entry = SphU.entry(NOVEL_RESOURCE, EntryType.IN, 1, ip);
            // 调用父类的preHandle
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } catch (BlockException ex) {
            // 处理限流请求
            log.info("IP:{}被限流了！", ip);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter()
                .write(objectMapper.writeValueAsString(RestResp.fail(ErrorCodeEnum.USER_REQ_MANY)));
        } finally {
            // 完成对该资源的访问后，需要调用entry.exit方法来退出该资源以便Sentinel可以正确统计资源的使用情况
            // 并且这样能避免资源泄露和提高程序性能
            if (entry != null) {
                entry.exit(1, ip);
            }
        }
        return false;
    }

}
