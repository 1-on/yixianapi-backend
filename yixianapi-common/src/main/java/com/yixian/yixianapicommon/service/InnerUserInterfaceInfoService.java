package com.yixian.yixianapicommon.service;


public interface InnerUserInterfaceInfoService {

    /**
         * 获取调用者的接口剩余调用次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    public int getLeftInvokeNum(long interfaceInfoId, long userId);


    /**
     * 调用接口统计
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    void invokeCount(long interfaceInfoId, long userId);
}
