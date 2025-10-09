# Auth 구현 계획

## 개요
- `/auth/login` POST 요청으로 사용자 인증을 처리하고, Spring Security 최신 API에 맞춰 JWT 기반 세션리스 인증 흐름을 완성한다.
- 모든 구현은 deprecated된 구성 요소를 회피하고, 기존 `/auth/register` 및 JWT 유틸과 자연스럽게 연동하도록 진행한다.

## 현재 상태 검토
- `AuthController`의 `AuthService` 필드가 `final`이 아니어서 DI가 적용되지 않으며, 로그인 엔드포인트가 주석 처리돼 있다.
- `SignInRequest`, `AccessTokenResponse`, `AuthService.login`, `BasicAuthenticationProvider`, `BasicAuthenticationFilter`, `JwtAuthenticationFilter` 등이 미완성 상태다.
- `SecurityConfig`에 JWT 필터 체인이 추가되지 않았고, `JwtProps`에 대한 `@EnableConfigurationProperties` 선언도 필요할 수 있다.

## 구현 단계
1. **DTO 및 응답 정비**: `SignInRequest`에 접근자/검증 애너테이션을 보완하고, `AccessTokenResponse`를 토큰·유효기간 등의 필드로 확장해 정적 팩터리로 생성한다.
2. **AuthService.login 구현**: 사용자 조회 → 비밀번호 검증(`PasswordEncoder.matches`) → `JwtUtil.generateAccessToken`(필요 시 refresh) → DTO 반환. 인증 실패 시 `ResponseStatusException(HttpStatus.UNAUTHORIZED)` 활용.
3. **인증 컴포넌트 보완**: `BasicAuthenticationProvider`에 `UserDetailsService`와 `PasswordEncoder`를 `final`로 주입하고, 인증 검증 후 `UsernamePasswordAuthenticationToken`을 반환. `BasicAuthenticationFilter`는 `/auth/login` 요청을 가로채 `AuthenticationManager`로 인증을 위임하고, 성공 시 토큰 응답을 작성하도록 OncePerRequestFilter 기반으로 재구성 고려.
4. **JWT 필터 연결**: `JwtAuthenticationFilter`를 최신 방식으로 정리하고, `SecurityConfig`에 `http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`를 추가해 모든 보호된 요청에서 토큰을 검증한다.
5. **예외 및 응답 처리**: 인증 실패, 토큰 유효성 오류에 대해 일관된 JSON 응답을 반환하도록 `AuthenticationEntryPoint`와 `AccessDeniedHandler`를 점검한다.
6. **테스트 작성**: `AuthService` 단위 테스트로 성공/실패 케이스를 검증하고, `JwtUtil` 만료/재발급 시나리오를 테스트한다. WebMvcTest로 `/auth/login` 통합 흐름을 검증한다.
7. **문서 업데이트**: `AGENTS.md`에 로그인 플로우, JWT 발급 방식, Postman 예제 등을 추가해 팀 내 공유를 완성한다.

## 추가 고려 사항
- H2/로컬 프로필에서의 비밀번호 해시 저장 여부와 REST 응답 포맷(응답 래퍼, 에러 코드)을 기존 컨벤션과 맞춘다.
- Refresh 토큰 저장 전략과 재발급 엔드포인트는 후속 작업 범위로 남겨두되, `JwtUtil` 인터페이스가 확장 가능하도록 유지한다.
