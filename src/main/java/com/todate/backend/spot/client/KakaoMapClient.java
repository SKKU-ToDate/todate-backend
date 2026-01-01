package com.todate.backend.spot.client;

import com.todate.backend.spot.dto.KaKaoSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    // KakaoClientConfig에서 빈으로 등록된 WebClient가 주입됩니다.
    private final WebClient kakaoWebClient;

    // 카카오 로컬 API: 키워드로 장소 검색
    public KaKaoSearchResponseDto searchByKeyword(String keyword, String rect) {

        // 1. URI 생성 및 인코딩 (RFC 3986 표준 준수)
        // 기술적 이유: 한글 검색어는 URL에 그대로 들어갈 수 없으므로 UTF-8 인코딩이 필수입니다.
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/v2/local/search/keyword.json")
                .queryParam("query", keyword);

        if (rect != null && !rect.isEmpty()) {
            builder.queryParam("rect", rect);
        }

        URI uri = builder.build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        log.info("[KakaoMapClient] Request URI: {}", uri);

        try {
            // 2. HTTP GET 요청 수행
            return kakaoWebClient.get()
                    .uri(uri)
                    .retrieve() // 응답 본문을 추출하기 위한 상태(Status) 및 헤더 검사 시작
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Kakao API Error: " + body)))
                    .bodyToMono(KaKaoSearchResponseDto.class) // JSON -> Java Object 역직렬화
                    .block(); // MVC 환경이므로 동기(Blocking) 방식으로 결과를 대기

        } catch (Exception e) {
            log.error("[KakaoMapClient] Failed to call Kakao API. Keyword: {}", keyword, e);
            // 호출 실패 시 시스템 전체가 중단되지 않도록 예외를 전파하거나 빈 객체를 반환할 수 있습니다.
            throw new RuntimeException("카카오 지도 검색 서비스 호출 중 오류가 발생했습니다. Cause: " + e.getMessage(), e);
        }
    }
}