-- 회원 테이블
CREATE TABLE member (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255) NOT NULL COMMENT '이메일 (로그인 ID)',
    password    VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt 암호화)',
    name        VARCHAR(50)  NOT NULL COMMENT '이름',
    nickname    VARCHAR(100) NOT NULL COMMENT '닉네임',
    created_at  DATETIME     NOT NULL COMMENT '생성일시',
    modified_at DATETIME     NULL     COMMENT '수정일시',
    CONSTRAINT uk_member_email UNIQUE (email),
    INDEX idx_member_created_at (created_at)
) COMMENT '회원';

-- 원본 건강 데이터 (raw)
CREATE TABLE health_data_raw (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT        NOT NULL COMMENT '회원 ID (FK)',
    record_key    CHAR(36)      NOT NULL COMMENT '사용자 식별용ㅊ 키',
    steps         INT           NOT NULL COMMENT '걸음 수',
    distance      DECIMAL(10,2) NOT NULL COMMENT '이동 거리',
    distance_unit VARCHAR(10)   NOT NULL COMMENT '이동 거리 단위 (m/km)',
    calories      DECIMAL(10,2) NOT NULL COMMENT '소모 칼로리',
    calories_unit VARCHAR(10)   NOT NULL COMMENT '소모 칼로리 단위 (kcal)',
    period_from   DATETIME      NOT NULL COMMENT '데이터 수집 시작일시',
    period_to     DATETIME      NOT NULL COMMENT '데이터 수집 종료일시',
    source        VARCHAR(20)   NULL COMMENT '데이터 출처',
    product       VARCHAR(20)   NULL COMMENT '디바이스 제품군',
    created_at    DATETIME      NOT NULL COMMENT '생성일시',
    CONSTRAINT fk_health_data_raw_member FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_health_data_raw_member_key (member_id, record_key)
) COMMENT '건강 데이터 raw';

-- 일별 집계 데이터
CREATE TABLE health_data_daily (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id      BIGINT        NOT NULL COMMENT '회원 ID (FK)',
    record_key     CHAR(36)      NOT NULL COMMENT '사용자 식별용 키',
    record_date    DATE          NOT NULL COMMENT '집계 기준일',
    total_steps    INT           NOT NULL COMMENT '총 걸음 수',
    total_calories DECIMAL(10,2) NOT NULL COMMENT '총 소모 칼로리',
    total_distance DECIMAL(10,2) NOT NULL COMMENT '총 이동 거리',
    created_at     DATETIME      NOT NULL COMMENT '생성일시',
    modified_at    DATETIME      NULL     COMMENT '수정일시',
    CONSTRAINT fk_health_data_daily_member FOREIGN KEY (member_id) REFERENCES member(id),
    INDEX idx_health_data_daily_key (record_key),
    INDEX idx_health_data_daily_member_date (member_id, record_date)
) COMMENT '건강 데이터 일별 집계';