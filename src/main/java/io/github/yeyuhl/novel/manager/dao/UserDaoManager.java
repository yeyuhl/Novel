package io.github.yeyuhl.novel.manager.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.yeyuhl.novel.core.constant.DatabaseConsts;
import io.github.yeyuhl.novel.dao.entity.UserInfo;
import io.github.yeyuhl.novel.dao.mapper.UserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户模块DAO管理类
 *
 * @author yeyuhl
 * @date 2023/5/8
 */
@Component
@RequiredArgsConstructor
public class UserDaoManager {

    private final UserInfoMapper userInfoMapper;

    /**
     * 根据用户ID集合批量查询用户信息列表
     *
     * @param userIds 需要查询的用户ID集合
     * @return 满足条件的用户信息列表
     */
    public List<UserInfo> listUsers(List<Long> userIds) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(DatabaseConsts.CommonColumnEnum.ID.getName(), userIds);
        return userInfoMapper.selectList(queryWrapper);
    }

}
