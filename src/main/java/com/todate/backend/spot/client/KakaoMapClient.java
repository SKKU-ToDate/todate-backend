package com.todate.backend.spot.client;

import com.todate.backend.spot.dto.response.KaKaoSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    // KakaoClientConfig에서 빈으로 등록된 WebClient가 주입됩니다.
    private final WebClient kakaoWebClient;

    // 카카오 로컬 API: 키워드로 장소 검색
    public KaKaoSearchResponseDto searchByKeyword(String keyword, String rect) {

        log.info("[KakaoMapClient] Request Keyword: {}, Rect: {}", keyword, rect);

        try {
            // 2. HTTP GET 요청 수행
            return kakaoWebClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/v2/local/search/keyword.json")
                                .queryParam("query", keyword);
                        if (rect != null && !rect.isEmpty()) {
                            uriBuilder.queryParam("rect", rect);
                        }
                        return uriBuilder.build();
                    })
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