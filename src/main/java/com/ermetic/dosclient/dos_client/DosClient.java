package com.ermetic.dosclient.dos_client;

import com.ermetic.dosclient.config.DosClientConfig;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DosClient {
    private static final String CLIENT_ID_QUERY_PARAM = "clientId";
    private final DosClientConfig dosClientConfig;
    private final WebClient webClient;

    public DosClient(DosClientConfig dosClientConfig) {
        this.dosClientConfig = dosClientConfig;

        int timeoutMS = dosClientConfig.getTimeoutMS();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMS)
                .responseTimeout(Duration.ofMillis(timeoutMS))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeoutMS, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutMS, TimeUnit.MILLISECONDS)));

        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public void sendRequest(int clientId) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder.path(dosClientConfig.getUrl())
                        .queryParam(CLIENT_ID_QUERY_PARAM, clientId)
                        .build())
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.SERVICE_UNAVAILABLE), error -> Mono.empty())
                .bodyToMono(Void.class)
                .subscribe();
    }
}
