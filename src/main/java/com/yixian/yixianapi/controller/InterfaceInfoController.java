package com.yixian.yixianapi.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yixian.yixianapi.common.DeleteRequest;
import com.yixian.yixianapi.common.IdRequest;
import com.yixian.yixianapi.common.Result;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.context.BaseContext;
import com.yixian.yixianapi.exception.BaseException;
import com.yixian.yixianapi.model.dto.interfaceinfo.*;
import com.yixian.yixianapi.model.entity.InterfaceInfo;
import com.yixian.yixianapi.model.entity.User;
import com.yixian.yixianapi.model.enums.InterfaceInfoStatusEnum;
import com.yixian.yixianapi.model.vo.InterfaceInfoVO;
import com.yixian.yixianapi.service.InterfaceInfoService;
import com.yixian.yixianapi.service.UserService;
import com.yixian.yixianapiclientsdk.client.YixianApiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口管理
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private YixianApiClient yixianApiClient;


    // region 增删改查

    /**
     * 新增
     *
     * @param interfaceInfoAddDTO
     * @return
     */
    @PostMapping("add")
    public Result<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddDTO interfaceInfoAddDTO) {
        if (interfaceInfoAddDTO == null) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddDTO, interfaceInfo);
        // 设置创建人信息
        interfaceInfo.setUserId(BaseContext.getCurrentId());
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        interfaceInfo.setUserId(BaseContext.getCurrentId());

        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BaseException(MessageConstant.OPERATION_ERROR);
        }
        return Result.success(interfaceInfo.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(BaseContext.getCurrentId()) && !userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.removeById(id);
        return Result.success(result);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateDTO interfaceInfoUpdateDTO) {
        if (interfaceInfoUpdateDTO == null || interfaceInfoUpdateDTO.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateDTO, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        Long id = interfaceInfoUpdateDTO.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return Result.success(result);
    }


    /**
     * 根据 id 查询
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<InterfaceInfoVO> getInterfaceInfoVOById(Long id) {
        if (id <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        return Result.success(interfaceInfoService.getInterfaceInfoVO(interfaceInfo));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param interfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/list/page")
    public Result<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryDTO interfaceInfoQueryDTO) {
        long current = interfaceInfoQueryDTO.getCurrent();
        long size = interfaceInfoQueryDTO.getPageSize();
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryDTO));
        return Result.success(interfaceInfoPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryDTO interfaceInfoQueryDTO) {
        long current = interfaceInfoQueryDTO.getCurrent();
        long size = interfaceInfoQueryDTO.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryDTO));
        return Result.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }


    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryDTO interfaceInfoQueryDTO) {
        if (interfaceInfoQueryDTO == null) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        interfaceInfoQueryDTO.setUserId(BaseContext.getCurrentId());
        long current = interfaceInfoQueryDTO.getCurrent();
        long size = interfaceInfoQueryDTO.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryDTO));
        return Result.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param interfaceInfoEditDTO
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditDTO interfaceInfoEditDTO) {
        if (interfaceInfoEditDTO == null || interfaceInfoEditDTO.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoEditDTO, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoEditDTO.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可编辑
        if (!oldInterfaceInfo.getUserId().equals(BaseContext.getCurrentId()) && !userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return Result.success(result);
    }


    /**
     * 发布
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    public Result<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用
        com.yixian.yixianapiclientsdk.model.User user = new com.yixian.yixianapiclientsdk.model.User();
        user.setUsername("test");
        String username = yixianApiClient.getUserNameByPost(user);
        if (StringUtils.isBlank(username)) {
            throw new BaseException(MessageConstant.SYSTEM_ERROR);
        }
        // 仅管理员可修改
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        // 修改接口状态信息
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());

        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return Result.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    public Result<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        // 仅管理员可修改
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        // 修改接口状态信息
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());

        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return Result.success(result);
    }


    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeDTO
     * @return
     */
    @PostMapping("/invoke")
    public Result<Object> InterfaceInfoInvoke(@RequestBody InterfaceInfoInvokeDTO interfaceInfoInvokeDTO) {
        if (interfaceInfoInvokeDTO == null || interfaceInfoInvokeDTO.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        Long id = interfaceInfoInvokeDTO.getId();
        String userRequestParams = interfaceInfoInvokeDTO.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BaseException(MessageConstant.INTERFACE_NOT_OPEN);
        }

        User loginUser = userService.getLoginUser();
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        YixianApiClient tempClient = new YixianApiClient(accessKey, secretKey);
        com.yixian.yixianapiclientsdk.model.User user = JSONUtil.toBean(userRequestParams, com.yixian.yixianapiclientsdk.model.User.class);
        String userNameByPost = tempClient.getUserNameByPost(user);
        return Result.success(userNameByPost);
    }
}
