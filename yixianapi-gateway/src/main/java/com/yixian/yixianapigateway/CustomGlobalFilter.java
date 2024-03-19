package com.yixian.yixianapigateway;


import cn.hutool.json.JSONObject;
import com.yixian.yixianapiclientsdk.utils.SignUtils;
import com.yixian.yixianapicommon.entity.InnerInterfaceInfo;
import com.yixian.yixianapicommon.entity.InnerUser;
import com.yixian.yixianapicommon.service.InnerInterfaceInfoService;
import com.yixian.yixianapicommon.service.InnerUserInterfaceInfoService;
import com.yixian.yixianapicommon.service.InnerUserService;
import com.yixian.yixianapigateway.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    public static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "localhost");

    public static final String REQUEST_HOST = "http://localhost:8123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识：" + request.getId());
        String url = REQUEST_HOST + request.getPath().value();
        log.info("请求路径：" + request.getPath().value());
        String method = request.getMethod().toString();
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        ServerHttpResponse response = exchange.getResponse();
        // 2.黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }
        // 3.用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 去数据库查询 accessKey 对应的 secretKey
        InnerUser invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
            System.out.println(invokeUser);
        } catch (Exception e) {
            log.error("getInvokeUserSecretKey error " + e);
        }
        // 没有找到调用用户信息
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        if (nonce == null || Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }
        if (!validTimestamp(timestamp)) {
            return handleNoAuth(response);
        }
        // 用调用者的 secretKey 构造签名并和传入的签名比较
        String serverSign = SignUtils.genSign(body, invokeUser.getSecretKey());
        if (!Objects.equals(serverSign, sign)) {
            return handleNoAuth(response);
        }
        // 4.请求的模拟接口是否存在 (请求方法是否匹配)
        InnerInterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error " + e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // 判断是否还有调用次数
        int leftInvokeNum = innerUserInterfaceInfoService.getLeftInvokeNum(interfaceInfo.getId(), invokeUser.getId());
        if (leftInvokeNum <= 0) {
            throw new BaseException("调用次数不足");
        }
        // 5.请求转发，调用模拟接口
        Mono<Void> filter = chain.filter(exchange);

        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());

    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatusCode statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                return chain.filter(exchange);//降级处理返回数据
            }
            // 装饰，增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        // 往返回值中写数据
                        // 拼接字符串
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // 6.调用成功，接口调用次数+1 invokeCount()
                            try {
                                innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                            } catch (Exception e) {
                                log.error("invokeCount error " + e);
                            }
                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer buff = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[buff.readableByteCount()];
                            buff.read(content);
                            DataBufferUtils.release(buff);//释放掉内存

                            // 7.构建返回日志
                            String joinData = new String(content);
                            log.info("响应结果：" + joinData);
                            return bufferFactory.wrap(joinData.getBytes());
                        }));
                    } else {
                        // 8.调用失败，返回错误码
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());

        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    private boolean validTimestamp(String timestamp) {
        final long FIVE_MINUTES = 5 * 60;
        long currentTime = System.currentTimeMillis() / 1000;
        long time = currentTime - Long.parseLong(timestamp);
        return time >= 0 && time <= FIVE_MINUTES;
    }
}