package com.yixian.yixianapicommon.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InnerUser implements Serializable {

    private static final long serialVersionUID = 7258849204554415556L;
    /**
     * id
     */
    private Long id;

    /**
     * 签名 accessKey
     */
    private String accessKey;

    /**
     * 签名 secretKey
     */
    private String secretKey;
}
