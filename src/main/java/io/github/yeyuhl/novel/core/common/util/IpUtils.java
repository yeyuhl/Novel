package io.github.yeyuhl.novel.core.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

/**
 * IP工具类
 *
 * @author yeyuhl
 * @date 2023/5/17
 */
@UtilityClass
public class IpUtils {

    private static final String UNKNOWN_IP = "unknown";

    private static final String IP_SEPARATOR = ",";

    /**
     * 获取真实IP
     */
    public String getRealIp(HttpServletRequest request) {
        // 这个一般是Nginx反向代理设置的参数
        // X-Real-IP：后端服务器从Nginx代理服务器中获取到的真实用户IP
        String ip = request.getHeader("X-Real-IP");
        // 下面三种HTTP请求头都用于确定通过代理服务器连接到Web服务器的客户端的真实IP地址
        if (ip == null || ip.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            // X-Forwarded-For：最常见的，最开始是由Squid这个缓存代理软件引入
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            // Proxy-Client-IP：一般是经过Apache HTTP Server代理后添加的请求头
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            // WL-Proxy-Client-IP：一般是经过WebLogic Web服务器代理后添加的请求头
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 有些网络通过多层代理，那么就会出现多个IP,处理多IP的情况（只获取第一个IP）
        if (ip != null && ip.contains(IP_SEPARATOR)) {
            String[] ipArray = ip.split(IP_SEPARATOR);
            ip = ipArray[0];
        }
        return ip;
    }
}
