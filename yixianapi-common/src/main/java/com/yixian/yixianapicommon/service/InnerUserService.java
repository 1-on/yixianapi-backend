package com.yixian.yixianapicommon.service;

import com.yixian.yixianapicommon.entity.InnerUser;

public interface InnerUserService {

    /**
     * 获取调用用户的 SecretKey
     *
     * @param accessKey
     * @return
     */
    InnerUser getInvokeUser(String accessKey);
}
