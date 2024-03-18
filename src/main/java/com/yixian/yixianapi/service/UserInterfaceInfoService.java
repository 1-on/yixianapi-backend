package com.yixian.yixianapi.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoQueryDTO;
import com.yixian.yixianapi.model.entity.UserInterfaceInfo;
import com.yixian.yixianapi.model.vo.UserInterfaceInfoVO;


/**
 * @author jiangfei
 * @description 针对表【user_interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-03-15 14:36:22
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 获取接口信息封装
     *
     * @param userInterfaceInfo
     * @return
     */
    UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo);

    /**
     * 获取查询条件
     *
     * @param userInterfaceInfoQueryDTO
     * @return
     */
    Wrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryDTO userInterfaceInfoQueryDTO);

    /**
     * 分页获取接口信息封装
     *
     * @param userInterfaceInfoPage
     * @return
     */
    Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage);


    int getLeftInvokeNum(long interfaceInfoId, long userId);


    /**
     * 调用接口统计
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
