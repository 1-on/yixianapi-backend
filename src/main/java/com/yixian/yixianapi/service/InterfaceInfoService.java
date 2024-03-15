package com.yixian.yixianapi.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yixian.yixianapi.model.dto.interfaceinfo.InterfaceInfoQueryDTO;
import com.yixian.yixianapi.model.entity.InterfaceInfo;
import com.yixian.yixianapi.model.vo.InterfaceInfoVO;

/**
 * @author jiangfei
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-03-10 20:31:22
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 获取接口信息封装
     *
     * @param interfaceInfo
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo);

    /**
     * 获取查询条件
     *
     * @param interfaceInfoQueryDTO
     * @return
     */
    Wrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryDTO interfaceInfoQueryDTO);

    /**
     * 分页获取接口信息封装
     *
     * @param interfaceInfoPage
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage);
}
