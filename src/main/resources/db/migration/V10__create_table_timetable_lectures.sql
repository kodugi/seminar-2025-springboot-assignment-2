CREATE TABLE timetable_lectures (
    -- 각 연결 레코드의 고유 ID
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- 어떤 시간표(timetables 테이블)에 속하는지
    timetable_id    BIGINT NOT NULL,
    -- 어떤 강의(lectures 테이블)가 포함되는지
    lecture_id      BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


    FOREIGN KEY (timetable_id) REFERENCES timetables (id) ON DELETE CASCADE,

    FOREIGN KEY (lecture_id) REFERENCES lectures (id),

    CONSTRAINT uk_timetable_lecture UNIQUE (timetable_id, lecture_id)
);