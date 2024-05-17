package io.github.yeyuhl.novel.service;

import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.dto.req.UserInfoUpdateReqDto;
import io.github.yeyuhl.novel.dto.req.UserLoginReqDto;
import io.github.yeyuhl.novel.dto.req.UserRegisterReqDto;
import io.github.yeyuhl.novel.dto.resp.UserInfoRespDto;
import io.github.yeyuhl.novel.dto.resp.UserLoginRespDto;
import io.github.yeyuhl.novel.dto.resp.UserRegisterRespDto;

/**
 * 用户模块服务类
 *
 * @author yeyuhl
 * @date 2023/5/7
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return JWT
     */
    RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto);

    /**
     * 用户登录
     *
     * @param dto 登录参数
     * @return JWT + 昵称
     */
    RestResp<UserLoginRespDto> login(UserLoginReqDto dto);

    /**
     * 用户反馈
     *
     * @param userId  反馈用户ID
     * @param content 反馈内容
     * @return void
     */
    RestResp<Void> saveFeedback(Long userId, String content);

    /**
     * 用户信息修改
     *
     * @param dto 用户信息
     * @return void
     */
    RestResp<Void> updateUserInfo(UserInfoUpdateReqDto dto);

    /**
     * 用户反馈删除
     *
     * @param userId 用户ID
     * @param id     反馈ID
     * @return void
     */
    RestResp<Void> deleteFeedback(Long userId, Long id);

    /**
     * 查询书架状态接口
     *
     * @param userId 用户ID
     * @param bookId 小说ID
     * @return 0-不在书架 1-已在书架
     */
    RestResp<Integer> getBookshelfStatus(Long userId, String bookId);

    /**
     * 用户信息查询
     * @param userId 用户ID
     * @return 用户信息
     */
    RestResp<UserInfoRespDto> getUserInfo(Long userId);
}
