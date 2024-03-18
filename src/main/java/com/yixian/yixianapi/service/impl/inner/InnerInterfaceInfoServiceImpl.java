package com.yixian.yixianapi.service.impl.inner;


import cn.hutool.core.io.resource.BytesResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.exception.BaseException;
import com.yixian.yixianapi.mapper.InterfaceInfoMapper;
import com.yixian.yixianapi.model.entity.InterfaceInfo;
import com.yixian.yixianapicommon.entity.InnerInterfaceInfo;
import com.yixian.yixianapicommon.service.InnerInterfaceInfoService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InnerInterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BaseException(MessageConstant.REQUEST_PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
        InnerInterfaceInfo innerInterfaceInfo = new InnerInterfaceInfo();
        innerInterfaceInfo.setId(interfaceInfo.getId());
        return innerInterfaceInfo;
    }

}
