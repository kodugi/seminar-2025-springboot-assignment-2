CREATE TABLE timetables (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY, -- 각 시간표의 고유 ID
    user_id     BIGINT NOT NULL,          -- 시간표 소유자 (users 테이블 참조)
    year        INT NOT NULL,             -- 시간표 대상 연도 (예: 2025)
    semester    VARCHAR(10) NOT NULL,     -- 시간표 대상 학기 (예: 봄학기)
    name        VARCHAR(255) NOT NULL,    -- 사용자가 지정한 시간표 이름

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
);