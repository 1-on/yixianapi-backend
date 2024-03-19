package com.yixian.yixianapiinterface;

import com.yixian.yixianapiclientsdk.client.YixianApiClient;

import com.yixian.yixianapiclientsdk.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YixianapiInterfaceApplicationTests {

    @Resource
    private YixianApiClient yixianApiClient;

    @Test
    void contextLoads() {
        String result = yixianApiClient.getNameByGet("yixian");
        User user = new User();
        user.setUsername("yixian");
        String userNameByPost = yixianApiClient.getUserNameByPost(user);
        System.out.println(result);
        System.out.println(userNameByPost);
    }

}
