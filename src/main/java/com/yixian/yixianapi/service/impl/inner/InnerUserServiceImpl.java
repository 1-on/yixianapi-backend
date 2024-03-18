package com.yixian.yixianapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.exception.BaseException;
import com.yixian.yixianapi.mapper.UserMapper;

import com.yixian.yixianapi.model.entity.User;
import com.yixian.yixianapicommon.entity.InnerUser;
import com.yixian.yixianapicommon.service.InnerUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public InnerUser getInvokeUser(String accessKey) {
        if (StringUtils.isBlank(accessKey)) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        User user = userMapper.selectOne(queryWrapper);
        InnerUser innerUser = new InnerUser();
        innerUser.setId(user.getId());
        innerUser.setAccessKey(user.getAccessKey());
        innerUser.setSecretKey(user.getSecretKey());
        System.out.println(innerUser);
        return innerUser;
    }
}
