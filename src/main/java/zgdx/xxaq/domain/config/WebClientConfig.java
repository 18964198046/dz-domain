//package zgdx.xxaq.domain.config;
//
//import io.netty.channel.ChannelOption;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.tcp.TcpClient;
//
//
//@Slf4j
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    public WebClient webClient(WebClient.Builder builder) {
//        return builder
//                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.67 Safari/537.36")
//                .clientConnector(reactorClientHttpConnector())
//                .filter(logResposneStatus())
//                .build();
//    }
//
//    private ExchangeFilterFunction logResposneStatus() {
//        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
//            log.info("Response Status {}", clientResponse.rawStatusCode());
//            return Mono.just(clientResponse);
//        });
//    }
//
//    private ReactorClientHttpConnector reactorClientHttpConnector() {
//        return new ReactorClientHttpConnector(httpClient());
//    }
//
//    private HttpClient httpClient() {
//        return HttpClient.from(tcpClient());
//    }
//
//    private TcpClient tcpClient() {
//        TcpClient tcpClient = TcpClient.create();
//        tcpClient.configure().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
//        return tcpClient;
//    }
//
//
//}
