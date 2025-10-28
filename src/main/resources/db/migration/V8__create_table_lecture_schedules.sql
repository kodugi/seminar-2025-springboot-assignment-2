CREATE TABLE lecture_schedules (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    lecture_id  BIGINT NOT NULL,
    day_of_week INT NOT NULL,               -- 요일 (0: 월요일, 1: 화요일 ... 5: 금요일)
    start_time  TIME NOT NULL,              -- 시작 시간
    end_time    TIME NOT NULL,              -- 종료 시간
    place       VARCHAR(255),               -- 강의실

    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (lecture_id) REFERENCES lectures (id) ON DELETE CASCADE
);