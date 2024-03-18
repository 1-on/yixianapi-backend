package com.yixian.yixianapi.service.impl.inner;

import com.yixian.yixianapi.service.UserInterfaceInfoService;
import com.yixian.yixianapicommon.service.InnerUserInterfaceInfoService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public int getLeftInvokeNum(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.getLeftInvokeNum(interfaceInfoId, userId);
    }

    @Override
    public void invokeCount(long interfaceInfoId, long userId) {
        userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }


}
