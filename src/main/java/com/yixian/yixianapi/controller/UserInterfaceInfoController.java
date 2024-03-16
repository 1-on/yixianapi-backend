package com.yixian.yixianapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yixian.yixianapi.common.DeleteRequest;
import com.yixian.yixianapi.common.Result;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.context.BaseContext;
import com.yixian.yixianapi.exception.BaseException;
import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoAddDTO;
import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoEditDTO;
import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoQueryDTO;
import com.yixian.yixianapi.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateDTO;
import com.yixian.yixianapi.model.entity.UserInterfaceInfo;
import com.yixian.yixianapi.model.vo.UserInterfaceInfoVO;
import com.yixian.yixianapi.service.UserInterfaceInfoService;
import com.yixian.yixianapi.service.UserService;
import com.yixian.yixianapiclientsdk.client.YixianApiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 接口管理
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private YixianApiClient yixianApiClient;


    // region 增删改查

    /**
     * 新增
     *
     * @param userInterfaceInfoAddDTO
     * @return
     */
    @PostMapping("add")
    public Result<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddDTO userInterfaceInfoAddDTO) {
        // 仅管理员可调用
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        if (userInterfaceInfoAddDTO == null) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddDTO, userInterfaceInfo);
        // 设置创建人信息
        userInterfaceInfo.setUserId(BaseContext.getCurrentId());
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        userInterfaceInfo.setUserId(BaseContext.getCurrentId());

        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        if (!result) {
            throw new BaseException(MessageConstant.OPERATION_ERROR);
        }
        return Result.success(userInterfaceInfo.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {
        // 仅管理员可调用
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        boolean result = userInterfaceInfoService.removeById(id);
        return Result.success(result);
    }

    /**
     * 更新
     *
     * @param userInterfaceInfoUpdateDTO
     * @return
     */
    @PostMapping("/update")
    public Result<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateDTO userInterfaceInfoUpdateDTO) {
        // 仅管理员可调用
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        if (userInterfaceInfoUpdateDTO == null || userInterfaceInfoUpdateDTO.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateDTO, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        Long id = userInterfaceInfoUpdateDTO.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return Result.success(result);
    }


    /**
     * 根据 id 查询
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<UserInterfaceInfoVO> getUserInterfaceInfoVOById(Long id) {
        if (id <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        if (userInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        return Result.success(userInterfaceInfoService.getUserInterfaceInfoVO(userInterfaceInfo));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param userInterfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/list/page")
    public Result<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(@RequestBody UserInterfaceInfoQueryDTO userInterfaceInfoQueryDTO) {
        // 仅管理员可调用
        if (!userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        long current = userInterfaceInfoQueryDTO.getCurrent();
        long size = userInterfaceInfoQueryDTO.getPageSize();
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryDTO));
        return Result.success(userInterfaceInfoPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<UserInterfaceInfoVO>> listUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryDTO userInterfaceInfoQueryDTO) {
        long current = userInterfaceInfoQueryDTO.getCurrent();
        long size = userInterfaceInfoQueryDTO.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryDTO));
        return Result.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage));
    }


    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param userInterfaceInfoQueryDTO
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<UserInterfaceInfoVO>> listMyUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryDTO userInterfaceInfoQueryDTO) {
        if (userInterfaceInfoQueryDTO == null) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        userInterfaceInfoQueryDTO.setUserId(BaseContext.getCurrentId());
        long current = userInterfaceInfoQueryDTO.getCurrent();
        long size = userInterfaceInfoQueryDTO.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BaseException(MessageConstant.PARAMS_ERROR);
        }
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryDTO));
        return Result.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param userInterfaceInfoEditDTO
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> editUserInterfaceInfo(@RequestBody UserInterfaceInfoEditDTO userInterfaceInfoEditDTO) {
        if (userInterfaceInfoEditDTO == null || userInterfaceInfoEditDTO.getId() <= 0) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoEditDTO, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        long id = userInterfaceInfoEditDTO.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BaseException(MessageConstant.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可编辑
        if (!oldUserInterfaceInfo.getUserId().equals(BaseContext.getCurrentId()) && !userService.isAdmin(BaseContext.getCurrentId())) {
            throw new BaseException(MessageConstant.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return Result.success(result);
    }

}
