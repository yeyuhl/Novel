package io.github.yeyuhl.novel.core.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS过滤处理
 *
 * @author yeyuhl
 * @date 2023/5/17
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Map<String, String> REPLACE_RULE = new HashMap<>();

    static {
        REPLACE_RULE.put("<", "&lt;");
        REPLACE_RULE.put(">", "&gt;");
    }

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 重写getParameterValues方法，将参数名和参数值都做xss过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        // 获取参数
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapeValues = new String[length];
            // 遍历参数，并且用相应的替换字符串替换掉REPLACE_RULE映射中出现的任何字符
            for (int i = 0; i < length; i++) {
                escapeValues[i] = values[i];
                int index = i;
                REPLACE_RULE.forEach(
                    (k, v) -> escapeValues[index] = escapeValues[index].replaceAll(k, v));
            }
            // 返回替换后的字符串数组
            return escapeValues;
        }
        return new String[0];
    }
}
