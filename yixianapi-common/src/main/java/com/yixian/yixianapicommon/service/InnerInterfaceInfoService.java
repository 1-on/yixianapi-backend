package com.yixian.yixianapicommon.service;

import com.yixian.yixianapicommon.entity.InnerInterfaceInfo;

public interface InnerInterfaceInfoService {

    /**
     * 判断接口是否存在
     *
     * @param url
     * @param method
     * @return
     */
    InnerInterfaceInfo getInterfaceInfo(String url, String method);
}
