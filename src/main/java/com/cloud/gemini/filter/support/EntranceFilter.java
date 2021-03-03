package com.cloud.gemini.filter.support;

import com.cloud.gemini.constant.KeyConstant;
import com.cloud.gemini.filter.AbstractGatewayWebFilter;
import com.cloud.gemini.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Order(-1)
@Component
@Slf4j
public class EntranceFilter extends AbstractGatewayWebFilter {

    @Resource
    private ExecutorService gatewayCoreAsyncRequestExecutor;

    // 如果有后置逻辑，需要覆写filter
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String traceId = UUID.randomUUID().toString();
        exchange.getAttributes().put(KeyConstant.traceId, traceId);
        LogUtil.logRecord(log, "[EntranceFilter] order(-1) 前前前前置处理", traceId);

        // 触发body的解析
//        Mono<Boolean> parseBody = parseBody(exchange.getRequest());

        String path = exchange.getRequest().getURI().getPath();
        if (path != null && path.indexOf("check_backend_active") >= 0) {
            ServerHttpResponse response = exchange.getResponse();
            log.error("心跳检测线程：" + Thread.currentThread().getName());
            return response.writeWith(Mono.just(response.bufferFactory().wrap("success".getBytes())));
        }

        // 切换线程池，指定上游源头的调度器
//        return parseBody.flatMap(t -> chain.filter(exchange).subscribeOn(Schedulers.fromExecutorService(gatewayCoreAsyncRequestExecutor)));
        return chain.filter(exchange).subscribeOn(Schedulers.fromExecutorService(gatewayCoreAsyncRequestExecutor));
    }

    private Mono<Boolean> parseBody(ServerHttpRequest request) {
        System.out.println("开始解析body数据...........");
        return request.getBody()
                .flatMap(buffer -> {
                    byte[] array = new byte[buffer.readableByteCount()];
                    buffer.read(array);
                    DataBufferUtils.release(buffer);
                    return Mono.just(array);
                })
                .reduce((b1, b2) -> {
                    byte[] newByte = new byte[b1.length + b2.length];
                    System.arraycopy(b1, 0, newByte, 0, b1.length);
                    System.arraycopy(b2, 0, newByte, b1.length, b2.length);
                    return newByte;
                })
                .flatMap(bodyBytes -> {
                    String body = null;
                    body = new String(bodyBytes, StandardCharsets.UTF_8);
                    System.out.println("222222");
                    System.out.println(body);
                    return Mono.just(true);
                });
    }

    @Override
    protected Mono<Boolean> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.just(true);
    }

    @Override
    protected Mono<Void> doDenyResponse(ServerWebExchange exchange) {
        return Mono.empty();
    }
}
