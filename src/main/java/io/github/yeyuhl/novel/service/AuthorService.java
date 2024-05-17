package io.github.yeyuhl.novel.service;

import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dto.req.AuthorRegisterReqDto;

/**
 * 作家模块服务类
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
public interface AuthorService {

    /**
     * 作家注册
     *
     * @param dto 注册参数
     * @return void
     */
    RestResp<Void> register(AuthorRegisterReqDto dto);

    /**
     * 查询作家状态
     *
     * @param userId 用户ID
     * @return 作家状态
     */
    RestResp<Integer> getStatus(Long userId);
}
