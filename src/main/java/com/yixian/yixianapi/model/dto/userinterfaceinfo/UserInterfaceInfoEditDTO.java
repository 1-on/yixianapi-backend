package com.yixian.yixianapi.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInterfaceInfoEditDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 接口状态（0-禁用，1-正常）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
