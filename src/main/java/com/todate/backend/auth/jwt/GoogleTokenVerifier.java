package com.todate.backend.auth.jwt;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.todate.backend.auth.config.GoogleOAuth2Props;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Google ID Token 검증 클래스
 *
 * 학습 포인트:
 * 1. ID Token은 JWT 형식 (Header.Payload.Signature)
 * 2. Google의 Public Key로 서명 검증
 * 3. 다음 claim들을 자동 검증:
 *    - iss: https://accounts.google.com
 *    - aud: YOUR_CLIENT_ID
 *    - exp: 현재 시간 < 만료 시간
 * 4. Public Key는 자동으로 캐싱됨 (효율적)
 */
@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(GoogleOAuth2Props props) {
        this.verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(),
            new GsonFactory()
        )
        // aud (Audience) 검증: 우리 앱의 Client ID인지 확인
        .setAudience(Collections.singletonList(props.clientId()))
        // iss (Issuer) 검증: Google이 발급했는지 확인 (자동)
        .build();
    }

    /**
     * Google ID Token을 검증하고 Payload를 반환
     *
     * @param idTokenString Flutter에서 받은 ID Token (JWT 문자열)
     * @return GoogleIdToken.Payload (사용자 정보 포함)
     * @throws IllegalArgumentException 토큰이 유효하지 않은 경우
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            // 1. JWT 파싱 및 서명 검증
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                // 검증 실패: 서명 불일치, aud 불일치, 만료됨 등
                throw new IllegalArgumentException("Invalid Google ID token");
            }

            // 2. Payload 추출 (사용자 정보)
            GoogleIdToken.Payload payload = idToken.getPayload();

            // 3. 추가 검증 (선택적)
            if (!payload.getEmailVerified()) {
                throw new IllegalArgumentException("Email not verified");
            }

            return payload;

        } catch (GeneralSecurityException e) {
            // 서명 검증 실패
            throw new IllegalArgumentException("Security error verifying token", e);
        } catch (IOException e) {
            // 네트워크 오류 (Public Key 가져오기 실패)
            throw new IllegalArgumentException("Network error verifying token", e);
        }
    }

    /**
     * Payload에서 사용자 정보 추출 헬퍼 메서드들
     */
    public String getGoogleId(GoogleIdToken.Payload payload) {
        return payload.getSubject();  // 'sub' claim: Google User ID
    }

    public String getEmail(GoogleIdToken.Payload payload) {
        return payload.getEmail();
    }

    public String getName(GoogleIdToken.Payload payload) {
        return (String) payload.get("name");
    }

    public String getPicture(GoogleIdToken.Payload payload) {
        return (String) payload.get("picture");
    }
}
