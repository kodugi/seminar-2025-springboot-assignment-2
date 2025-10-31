CREATE TABLE lectures (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY, --각 강의 고유 ID
    year                INT NOT NULL,               -- 강의 개설 연도
    semester            VARCHAR(10) NOT NULL,       -- 강의 개설 학기 (예: 봄학기)
    course_number       VARCHAR(20) NOT NULL,       -- 교과목 번호 (예: F21.201)
    lecture_number      VARCHAR(10) NOT NULL,       -- 강좌 번호 (예: 001)
    course_title        VARCHAR(255) NOT NULL,      -- 교과목명 (예: 대학영어2)
    course_subtitle     VARCHAR(255),               -- 부제명 (예: 글쓰기)
    credit              INT NOT NULL,               -- 학점 수
    instructor          VARCHAR(255) NOT NULL,      -- 담당교수 이름
    category            VARCHAR(50),                -- 교과 구분 (예: "전공선택")
    college             VARCHAR(100),               -- 개설 대학 (예: "공과대학")
    department          VARCHAR(100),               -- 개설 학과 (예: "컴퓨터공학부")
    academic_course     VARCHAR(50),                -- 이수 과정 (예: "학사")
    academic_year       VARCHAR(20),                -- 학년 (예: "1학년")


    created_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

    CONSTRAINT uk_lectures_year_semester_course_lecture UNIQUE (year, semester, course_number, lecture_number)
);