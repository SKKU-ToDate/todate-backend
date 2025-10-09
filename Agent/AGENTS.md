# Repository Guidelines
## 프로젝트 구조 및 모듈 구성
- `src/main/java/com/todate/backend`는 인증(auth), 사용자(user), 코스(course), 스팟(spot) 도메인을 중심으로 controller → service → domain/repository 계층을 유지한다. 새로운 모듈도 동일한 계층을 따라 패키지를 추가한다.
- `src/main/resources`에는 공통 설정 `application.yml`과 로컬 전용 `application-local.yml`이 있으며, 정적 자원은 `static`, 템플릿은 `templates`에 둔다.
- 테스트 코드는 `src/test/java` 아래에서 프로덕션 패키지를 거울처럼 복제하고, 공용 테스트 픽스처는 도메인별 하위 패키지에 모아 재사용성을 높인다.
## 빌드, 테스트, 개발 커맨드
- `./gradlew clean build` : 전체 빌드와 모든 단위 테스트를 실행해 CI와 동일한 결과를 확인한다.
- `./gradlew test` : JUnit 기반 테스트만 실행한다. 실패 시 `--info` 옵션으로 로그를 확장해 원인을 파악한다.
- `./gradlew bootRun --args='--spring.profiles.active=local'` : 로컬 서버를 8080 포트에서 기동하고 H2 메모리 DB 설정을 적용한다.
## 코딩 스타일 및 네이밍 규칙
- Java 21과 Spring Boot 3.5를 기준으로 하며, 클래스·패키지는 `PascalCase`, 메서드·변수는 `camelCase`를 사용한다.
- 컨트롤러 엔드포인트는 REST 관례에 따라 복수형 URI와 명확한 HTTP 메서드를 사용한다.
- Lombok을 사용할 때는 필수 인자가 드러나도록 `@Builder`, `@RequiredArgsConstructor`를 우선 적용하고, DTO에는 명시적 필드명을 유지한다.
- 코드 포매팅은 4칸 들여쓰기와 공백 기반 정렬을 따른다. 변경이 크면 IDE 자동 정렬 후 `diff`를 검토한다.
## 테스트 지침
- 단위 테스트는 JUnit 5와 Spring Boot Test를 사용하며, 클래스명은 `클래스명Test` 패턴을 따른다.
- 서비스 계층은 Mockito 혹은 `@DataJpaTest`로 외부 의존성을 격리하고, 컨트롤러는 `@WebMvcTest`로 슬라이스 테스트를 작성한다.
- 새로운 기능은 성공·실패 케이스를 모두 포함한 최소 두 개 시나리오를 준비하고, `@DisplayName`으로 한글 설명을 명시한다.
## 커밋 및 PR 가이드라인
- 커밋 메시지는 `[TYPE] 요약` 형식을 사용한다. TYPE 예시는 `FEAT`, `FIX`, `REFACTOR`, `REMOVE`, `CHORE`이며, 본문에는 변경 이유와 영향 범위를 적는다.
- PR은 변경 요약, 테스트 결과(`./gradlew test` 출력 요약), 연관 이슈 링크를 포함한다. API 스펙이 변하면 요청·응답 예시나 문서 링크를 함께 제공한다.
- 리뷰 전 로컬에서 테스트를 통과시키고, 설정이나 문서가 변하면 `HELP.md` 또는 관련 가이드를 갱신했는지 확인한다.
## 보안 및 설정 팁
- 민감한 비밀키는 환경 변수나 외부 시크릿 관리 도구로 주입하고, `application-local.yml`에는 목업 값만 남긴다.
- 데이터베이스 혹은 OAuth 자격 증명을 커밋하지 말고, 필요한 경우 샘플 값을 별도 공유 채널에 제공한다.
