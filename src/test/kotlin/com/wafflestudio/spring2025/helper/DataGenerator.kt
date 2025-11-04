package com.wafflestudio.spring2025.helper

import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.model.LectureSchedule
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.lecture.repository.LectureScheduleRepository
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableLecture
import com.wafflestudio.spring2025.timetable.repository.TimetableLectureRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.JwtTokenProvider
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import java.time.LocalTime
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val timetableRepository: TimetableRepository,
    private val timetableLectureRepository: TimetableLectureRepository,
    private val lectureRepository: LectureRepository,
    private val lectureScheduleRepository: LectureScheduleRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun generateUser(
        username: String? = null,
        password: String? = null,
    ): Pair<User, String> {
        val user =
            userRepository.save(
                User(
                    username = username ?: "user-${Random.Default.nextInt(1000000)}",
                    password = BCrypt.hashpw(password ?: "password-${Random.Default.nextInt(1000000)}", BCrypt.gensalt()),
                ),
            )
        return user to jwtTokenProvider.createToken(user.username)
    }

    fun generateBoard(name: String? = null): Board {
        val board =
            boardRepository.save(
                Board(
                    name = name ?: "board-${Random.Default.nextInt(1000000)}",
                ),
            )
        return board
    }

    fun generatePost(
        title: String? = null,
        content: String? = null,
        user: User? = null,
        board: Board? = null,
    ): Post {
        val post =
            postRepository.save(
                Post(
                    title = title ?: "title-${Random.Default.nextInt(1000000)}",
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    boardId = (board ?: generateBoard()).id!!,
                ),
            )
        return post
    }

    fun generateComment(
        content: String? = null,
        user: User? = null,
        post: Post? = null,
    ): Comment {
        val comment =
            commentRepository.save(
                Comment(
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    postId = (post ?: generatePost()).id!!,
                ),
            )
        return comment
    }

    fun generateTimetable(
        user: User? = null,
        year: Int? = null,
        semester: String? = null,
        name: String? = null,
    ): Timetable {
        val timetable =
            timetableRepository.save(
                Timetable(
                    userId = (user ?: generateUser().first).id!!,
                    year = year ?: 2025,
                    semester = semester ?: "",
                    name = name ?: "name-${Random.Default.nextInt(1000000)}",
                )
            )
        return timetable
    }

    fun generateLecture(
        year: Int? = null,
        semester: String? = null,
        courseNumber: String? = null,
        lectureNumber: String? = null,
        courseTitle: String? = null,
        courseSubtitle: String? = null,
        credit: Int? = null,
        instructor: String? = null,
        category: String? = null,
        college: String? = null,
        department: String? = null,
        academicCourse: String? = null,
        academicYear: String? = null,
    ): Lecture {
        val lecture =
            lectureRepository.save(
                Lecture(
                    year = year ?: 2025,
                    semester = semester ?: "",
                    courseNumber = courseNumber ?: "",
                    lectureNumber = lectureNumber ?: "",
                    courseTitle = courseTitle ?: "course-${Random.Default.nextInt(1000000)}",
                    courseSubtitle = courseSubtitle ?: "course-${Random.Default.nextInt(1000000)}",
                    credit = credit ?: 0,
                    instructor = instructor ?: "",
                    category = category ?: "",
                    college = college ?: "",
                    department = department ?: "",
                    academicCourse = academicCourse ?: "",
                    academicYear = academicYear ?: "",
                )
            )
        return lecture
    }

    fun generateLectureWithSchedule(
        lecture: Lecture,
        dayOfWeek: Int? = null,
        startTime: String? = null,
        endTime: String? = null,
        place: String? = null,
    ): LectureSchedule {
        val lectureSchedule = lectureScheduleRepository.save(
            LectureSchedule(
                lectureId = lecture.id!!,
                dayOfWeek = dayOfWeek ?: 7,
                startTime = LocalTime.parse(startTime ?: "9:30"),
                endTime = LocalTime.parse(endTime ?: "10:45"),
                place = place ?: "301-118",
            )
        )
        return lectureSchedule
    }

    fun addLectureToTimetable(
        timetable: Timetable,
        lecture: Lecture,
    ): Unit {
        timetableLectureRepository.save(
            TimetableLecture(
                timetableId = timetable.id!!,
                lectureId = lecture.id!!,
            )
        )
    }
}
