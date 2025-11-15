# ToDate Backend 프로젝트 분석

## 프로젝트 개요

**ToDate Backend**는 커플을 위한 데이트 코스 관리 애플리케이션의 백엔드 시스템입니다.

### 기술 스택
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.4
- **빌드 도구**: Gradle
- **데이터베이스**: H2 (개발), PostgreSQL (운영)
- **인증**: Spring Security + JWT (JJWT 0.12.7)
- **기타**: Spring Data JPA, Lombok, Validation

### 프로젝트 설정
- **프로파일**: local (기본값)
- **로컬 DB**: H2 (PostgreSQL 호환 모드)
- **JPA DDL**: create-drop (개발 모드)
- **JWT 유효기간**: Access/Refresh Token 각 1일

## 아키텍처

### 레이어드 아키텍처
```
Controller Layer (REST API)
    ↓
Service Layer (비즈니스 로직, @Transactional)
    ↓
Repository Layer (Spring Data JPA)
    ↓
Domain Layer (JPA Entity)

DTO Layer (Request/Response 분리)
```

### 도메인 주도 설계 (DDD)
- `auth`: 인증/인가
- `user`: 사용자 관리
- `course`: 데이트 코스 관리
- `spot`: 장소 관리

### 보안 아키텍처
- JWT 기반 Stateless 인증
- Bearer 토큰 방식
- SecurityConfig: 보안 필터 체인
- JwtAuthenticationFilter: JWT 검증
- BasicAuthenticationProvider: 인증 제공자

## 주요 도메인

### 1. Auth 도메인 (인증)

**컨트롤러**: `AuthController` (`/auth`)
- `POST /auth/register` - 회원가입
- `POST /auth/login` - 로그인

**서비스**: `AuthService`
- `signupUser(SignUpRequest)`: 사용자 회원가입 (중복 확인, 암호화)
- `login(SignInRequest)`: 로그인 (JWT 토큰 생성)

**JWT 유틸리티**: `JwtUtil`
- `generateAccessToken()`: 액세스 토큰 생성
- `generateRefreshToken()`: 리프레시 토큰 생성
- `extractSubject()`: 사용자명 추출
- `isExpired()`: 토큰 만료 확인

**보안 설정**: `SecurityConfig`
- `/auth/**`, `/h2-console/**`: 인증 불필요
- 나머지 엔드포인트: JWT 인증 필요
- BCrypt 비밀번호 인코더

### 2. User 도메인 (사용자)

**엔티티**:
- `User` (`users` 테이블)
  - username (PK): 사용자 ID
  - password: 암호화된 비밀번호
  - name: 사용자 이름
  - UserDetails 구현 (Spring Security)
  
- `Relationship` (`relationships` 테이블)
  - id (PK, Auto)
  - partner1 (OneToOne, Unique): 파트너 1
  - partner2 (OneToOne, Unique): 파트너 2
  - 각 사용자는 최대 1명의 파트너와 관계 가능

**컨트롤러**: `UserController` (`/user`)
- `GET /user` - 사용자 정보 조회
- `PUT /user/password` - 비밀번호 변경
- `PATCH /user` - 사용자 정보 수정

**서비스**: `UserService`
- `getUser()`: 사용자 정보 + 파트너 정보 + 코스 개수 조회
- `changePassword()`: 비밀번호 변경 (정책 검증)
- `updateUser()`: 사용자 정보 수정
- `updateRelationship()`: 파트너 관계 변경
- `removeRelationship()`: 관계 삭제

**레포지토리**:
- `UserRepository`: 사용자 조회, 존재 확인
- `RelationshipRepository`: 관계 조회

### 3. Course 도메인 (데이트 코스)

**엔티티**:
- `Course` (`course` 테이블)
  - id (PK, Auto)
  - name: 코스 이름
  - date: 데이트 날짜
  - isHistory: 히스토리 여부 (기본값: false)

- `UserCourse` (`user_course` 테이블)
  - id (PK, Auto)
  - user (ManyToOne): 사용자
  - course (ManyToOne): 코스
  - 사용자-코스 다대다 관계 중간 테이블

**컨트롤러**: `CourseController` (`/course`)
- `POST /course` - 코스 생성
- `GET /course/{username}` - 코스 목록 조회
- `DELETE /course/{id}` - 코스 삭제

**서비스**: `CourseService`
- `CreateCourse()`: 새 코스 생성 (기본 이름, 현재 날짜)
- `findAllCourses()`: 사용자의 전체 코스 조회
- `DeleteCourse()`: 권한 확인 후 코스 삭제

**레포지토리**:
- `CourseRepository`: 기본 CRUD
- `UserCourseRepository`: 사용자별 코스 조회, 개수 카운트

### 4. Spot 도메인 (장소) - 개발 중

**엔티티**: `Spot` (`spot` 테이블)
- id (PK): 장소 ID
- courseId (ManyToOne): 소속 코스
- seq: 순서
- placeName: 장소명
- addressName: 주소
- placeUrl: 장소 URL
- latitude: 위도
- longitude: 경도

**컨트롤러**: `SpotController` (`/spot`)
- `GET /spot/query/keyword` - 키워드로 장소 검색 (미구현)

**서비스**: `SpotService` (비어있음)

**레포지토리**: `SpotRepository`

## 데이터베이스 ERD

```
User (username, password, name)
  ├─ 1:1 → Relationship (partner1, partner2)
  └─ M:N → UserCourse → Course (id, name, date, isHistory)
                           └─ 1:M → Spot (id, seq, placeName, address, coordinates)
```

## API 엔드포인트 요약

### Auth API (인증 불필요)
- `POST /auth/register` - 회원가입
- `POST /auth/login` - 로그인

### User API (인증 필요)
- `GET /user` - 사용자 정보 조회
- `PUT /user/password` - 비밀번호 변경
- `PATCH /user` - 사용자 정보 수정

### Course API (인증 필요)
- `POST /course` - 코스 생성
- `GET /course/{username}` - 코스 목록 조회
- `DELETE /course/{id}` - 코스 삭제

### Spot API (인증 필요)
- `GET /spot/query/keyword` - 장소 검색 (미구현)

## 현재 개발 상황

### Git 상태
- **현재 브랜치**: feat#6
- **Base 브랜치**: master

### feat#6 브랜치 변경사항
1. `.gitignore`: 환경 변수 파일 제외 (`**/.env*`)
2. `Spot.java`: 
   - `@GeneratedValue` 제거 (ID 수동 관리)
   - 장소 정보 필드 추가 (placeName, address, URL, coordinates)
3. `SpotController.java`: 장소 검색 API 스켈레톤 추가
4. `SpotService.java`: 서비스 클래스 생성
5. `UserService.java`: `@Transactional` 설정 변경

**작업 목적**: Spot(장소) 기능 개발 및 외부 지도 API 연동 준비

## 코드 컨벤션 및 패턴

### 어노테이션
- **Lombok**: `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Spring**: `@Service`, `@RestController`, `@RequestMapping`, `@Transactional`
- **JPA**: `@Entity`, `@Table`, `@Id`, `@Column`, `@ManyToOne`, `@OneToOne`
- **Validation**: `@Valid`

### 네이밍 컨벤션
- **클래스**: PascalCase
- **메서드**: camelCase (일부 PascalCase 혼용 - 개선 필요)
- **필드**: camelCase
- **패키지**: lowercase

### 설계 패턴
- Repository Pattern (Spring Data JPA)
- DTO Pattern (Request/Response 분리)
- Builder Pattern (Lombok)
- Factory Pattern (`User.create()` 정적 팩토리)
- Strategy Pattern (`PasswordPolicy` 인터페이스)

### 예외 처리
- `ResponseStatusException` 사용
- HTTP 상태 코드와 메시지 포함
- 예: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")`

## 테스트 현황

### 존재하는 테스트
- `BackendApplicationTests.java`: 컨텍스트 로드 테스트
- `CourseServiceTest.java`: CourseService 단위 테스트
- `CourseIntegrationTest.java`: Course 통합 테스트

### 테스트 미비 영역
- Auth 도메인 (전체)
- User 도메인 (전체)
- Spot 도메인 (전체)

## 향후 개발 방향

### 1. Spot 기능 완성 (진행 중)
- 외부 지도 API(카카오맵/네이버) 연동
- 키워드 기반 장소 검색 구현
- 코스에 장소 추가/삭제/수정 기능

### 2. Course 기능 확장
- 코스 상세 정보 조회 (Spot 포함)
- 코스 수정 (이름, 날짜 변경)
- 히스토리 기능 활용 (isHistory)

### 3. User 기능 개선
- 비밀번호 정책 강화 (AllowAllPasswordPolicy 교체)
- 프로필 이미지 업로드
- 사용자 설정 추가

### 4. 인증 개선
- Refresh Token 재발급 API
- 소셜 로그인 (OAuth2)
- 이메일 인증

### 5. 품질 개선
- 테스트 커버리지 향상 (Auth, User, Spot)
- API 문서화 (Swagger/OpenAPI)
- 통합 예외 처리 (ControllerAdvice)
- 메서드 네이밍 일관성 개선

### 6. 추가 기능
- 코스 공유 기능
- 리뷰/평가 시스템
- 알림 기능

## 강점 및 개선사항

### ✅ 강점
- 명확한 도메인 분리
- 레이어드 아키텍처 준수
- Spring Boot 베스트 프랙티스 활용
- JWT 기반 보안 구현
- Lombok으로 코드 간결화
- 커플 관계 모델링 우수

### ⚠️ 개선 필요
- 일관되지 않은 메서드 네이밍
- 테스트 커버리지 부족
- API 문서화 부재
- 예외 처리 통일 필요
- Spot 기능 미완성
- 비밀번호 정책 미흡

### 🔒 보안 고려사항
- JWT Secret Key 환경 변수 관리 (현재 yml 하드코딩)
- HTTPS 적용 필요 (운영 환경)
- CORS 설정 확인 필요
- Rate Limiting 고려

## 빌드 및 실행

### Gradle 명령어
```bash
./gradlew build          # 빌드
./gradlew test           # 테스트
./gradlew bootRun        # 실행
./gradlew clean build    # 클린 빌드
```

### H2 콘솔
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./.local/h2/devdb`
- Username: `sa`
- Password: (비어있음)

---

**마지막 분석 일시**: 2025-11-15  
**분석 브랜치**: feat#6  
**분석 도구**: Serena MCP