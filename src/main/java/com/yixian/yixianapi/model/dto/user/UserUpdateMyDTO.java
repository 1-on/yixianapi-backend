package com.yixian.yixianapi.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateMyDTO implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
