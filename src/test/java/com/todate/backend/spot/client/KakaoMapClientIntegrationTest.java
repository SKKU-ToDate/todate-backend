package com.todate.backend.spot.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.todate.backend.spot.dto.KaKaoSearchResponseDto;

import org.springframework.beans.factory.annotation.Value;

@SpringBootTest
@ActiveProfiles("local")
class KakaoMapClientIntegrationTest {

    @Autowired
    private KakaoMapClient kakaoMapClient;

    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.base-url}")
    private String baseUrl;

    @Test
    @DisplayName("Search by keyword 'Kakao Friends' and verify results")
    void searchByKeyword_integration() {
        // Given
        String keyword = "카카오프렌즈";
        String rect = "";

        System.out.println("DEBUG: apiKey=" + (apiKey != null ? apiKey.substring(0, 3) + "..." : "null"));
        System.out.println("DEBUG: baseUrl=" + baseUrl);

        // When
        KaKaoSearchResponseDto response = kakaoMapClient.searchByKeyword(keyword, rect);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDocuments()).isNotEmpty();

        // Print first result for manual verification in logs
        System.out.println("First Result: " + response.getDocuments().get(0).getPlaceName());

        assertThat(response.getDocuments().get(0).getPlaceName()).contains("카카오");
    }
}
