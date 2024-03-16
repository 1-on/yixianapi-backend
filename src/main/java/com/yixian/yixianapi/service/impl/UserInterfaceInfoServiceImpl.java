package com.yixian.yixianapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yixian.yixianapi.constant.CommonConstant;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.exception.BaseException;
import com.yixian.yixianapi.mapper.UserInterfaceInfoMapper;
import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoQueryDTO;
import com.yixian.yixianapi.model.entity.UserInterfaceInfo;
import com.yixian.yixianapi.model.vo.UserInterfaceInfoVO;
import com.yixian.yixianapi.service.UserInterfaceInfoService;
import com.yixian.yixianapi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiangfei
 * @description 针对表【user_interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2024-03-15 14:36:22
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }
        Long id = userInterfaceInfo.getId();
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Integer totalNum = userInterfaceInfo.getTotalNum();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        Integer status = userInterfaceInfo.getStatus();
        Date createTime = userInterfaceInfo.getCreateTime();
        Date updateTime = userInterfaceInfo.getUpdateTime();
        Integer isDelete = userInterfaceInfo.getIsDelete();

        // 创建时，参数不能为空
        if (add) {
            if (userId <= 0 || interfaceInfoId <= 0) {
                throw new BaseException(MessageConstant.PARAMS_ERROR);
            }
        }
        if (leftNum < 0) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }

    }

    @Override
    public UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo) {
        UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
        BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);
        return userInterfaceInfoVO;
    }

    @Override
    public Wrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryDTO userInterfaceInfoQueryDTO) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryDTO == null) {
            return queryWrapper;
        }
        Long id = userInterfaceInfoQueryDTO.getId();
        Long userId = userInterfaceInfoQueryDTO.getUserId();
        Long interfaceInfoId = userInterfaceInfoQueryDTO.getInterfaceInfoId();
        Integer totalNum = userInterfaceInfoQueryDTO.getTotalNum();
        Integer leftNum = userInterfaceInfoQueryDTO.getLeftNum();
        Integer status = userInterfaceInfoQueryDTO.getStatus();
        int current = userInterfaceInfoQueryDTO.getCurrent();
        int pageSize = userInterfaceInfoQueryDTO.getPageSize();
        String sortField = userInterfaceInfoQueryDTO.getSortField();
        String sortOrder = userInterfaceInfoQueryDTO.getSortOrder();

        // 拼接查询条件

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(interfaceInfoId), "interfaceInfoId", interfaceInfoId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage) {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoPage.getRecords();
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>(userInterfaceInfoPage.getCurrent(), userInterfaceInfoPage.getSize(), userInterfaceInfoPage.getTotal());
        if (CollUtil.isEmpty(userInterfaceInfoList)) {
            return userInterfaceInfoVOPage;
        }
        // 填充信息
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = userInterfaceInfoList.stream().map(userInterfaceInfo -> {
            UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
            BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);
            return userInterfaceInfoVO;
        }).collect(Collectors.toList());
        userInterfaceInfoVOPage.setRecords(userInterfaceInfoVOList);
        return userInterfaceInfoVOPage;
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("leftNum = leftNum - 1,totalNum = totalNum + 1");
        return this.update(updateWrapper);


    }
}




