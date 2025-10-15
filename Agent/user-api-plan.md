# User API 구현 계획

## 목표
- `GET /user`, `PUT /user/password`, `PATCH /user` 3개 엔드포인트를 완성해 사용자 정보 조회/수정, 비밀번호 변경 기능을 제공한다.
- 서비스 계층, DTO, 보안 정책을 일관되게 정리하고, 테스트로 회귀를 방지한다.

## 엔드포인트 요구사항
- `GET /user` : `userId` 기준으로 사용자 프로필과 진행 중/완료된 코스 수 등을 응답한다. 성공 시 200 OK, 사용자 미존재 시 404.
- `PUT /user/password` : 현재 사용자 비밀번호를 새 비밀번호로 교체한다. 성공 시 204 No Content, 비밀번호 정책 위반은 400, 사용자 미존재는 404.
- `PATCH /user` : 이름, 파트너 등 선택 필드를 갱신한다. 성공 시 204 No Content. 비어 있는 업데이트는 400 처리.

## 구현 순서
1. **도메인 & DTO 정비**
   - `User` 엔티티에 파트너 등 추가 필드가 필요하다면 `@Column` 정의 및 마이그레이션 준비.
   - DTO에 `@Getter`, 생성자, `@Builder` 혹은 레코드 변환 등 사용 편의 기능 추가.
   - `PatchUserRequest`에 변경 가능 필드를 `@Size` 등으로 검증하고, 모든 필드 null일 경우 예외 처리 로직 준비.
   - `UserResponse`에 필요한 응답 필드(이름, 파트너, 코스 카운트 등)를 명확히 하고 정적 팩토리 작성.
2. **Service 계층**
   - `UserService`에 `getUser(String userId)`, `updatePassword(String userId, String rawPassword)`, `updateProfile(PatchUserRequest request)` 메서드 추가.
   - 비밀번호 변경 시 `PasswordEncoder` 의존성 주입 및 인코딩 후 저장. 비밀번호 정책(길이, 문자 조합) 검증 로직 포함.
   - 사용자 조회 실패 시 커스텀 예외(`UserNotFoundException`) 생성.
   - 프로필 수정은 Dirty Checking 활용, 트랜잭션 경계 지정(`@Transactional`).
   - 코스 수 통계는 `UserCourseRepository` 등을 활용해 조회(필요시 Query 메서드 추가).
3. **Controller 구현**
   - `@Valid` 애너테이션으로 요청 검증.
   - `GET /user`는 서비스에서 받은 DTO/엔티티를 `UserResponse`로 변환해 반환.
   - 비밀번호 변경, 프로필 수정은 성공 시 `ResponseEntity.noContent()` 반환.
   - 인증 정보가 필요하다면 `@AuthenticationPrincipal` 활용해 `userId` 일치 여부를 검증.
4. **예외 및 응답 처리**
   - 전역 예외 처리기(`@RestControllerAdvice`)에 사용자 미존재, 검증 오류, 비밀번호 정책 위반 등에 대한 `ErrorResponse` 추가.
   - 공통 응답 규격(상태 코드, 메시지) 문서화.
5. **테스트 전략**
   - `UserServiceTest`: 존재하지 않는 사용자, 정상 조회, 비밀번호 변경, 세부 정보 갱신 케이스.
   - `UserControllerTest` 혹은 `@WebMvcTest`: 요청/응답 JSON 검증, 유효성 실패 시 응답 형식 검증.
   - 필요 시 통합 테스트에서 JPA 영속성 및 Dirty Checking 동작 확인.

## 추가 고려사항
- 비밀번호 변경 시 기존 비밀번호 검증 요구 여부 확인(요청 스펙 확정 필요).
- 코스 수 집계 로직과 퍼포먼스 검토(집계용 쿼리 혹은 캐시 사용 여부).
- 추후 사용자 프로필 사진 등 확장 가능성을 고려한 DTO/엔티티 설계.
- 문서화: API 스펙을 `Agent` 디렉터리 혹은 스웨거 문서에 반영.
