package com.wafflestudio.spring2025.comment.controller

import com.wafflestudio.spring2025.comment.dto.CreateCommentRequest
import com.wafflestudio.spring2025.comment.dto.CreateCommentResponse
import com.wafflestudio.spring2025.comment.dto.UpdateCommentRequest
import com.wafflestudio.spring2025.comment.dto.UpdateCommentResponse
import com.wafflestudio.spring2025.comment.dto.core.CommentDto
import com.wafflestudio.spring2025.comment.service.CommentService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping
    @Operation(
        summary = "댓글 목록 조회",
        description = "게시글 Id를 받아 해당 게시글의 댓글 목록 조회"
    )
    fun list(
        @PathVariable postId: Long,
    ): ResponseEntity<List<CommentDto>> {
        val comments = commentService.list(postId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping
    @Operation(
        summary = "댓글 생성",
        description = "게시글 Id와 내용을 입력받아 댓글 생성"
    )
    fun create(
        @PathVariable postId: Long,
        @RequestBody createRequest: CreateCommentRequest,
        @LoggedInUser user: User,
    ): ResponseEntity<CreateCommentResponse> {
        val comment =
            commentService.create(
                postId = postId,
                content = createRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "댓글 수정",
        description = "게시글 Id, 댓글 Id, 수정 내용을 입력받아 댓글 수정"
    )
    fun update(
        @PathVariable postId: Long,
        @PathVariable id: Long,
        @LoggedInUser user: User,
        @RequestBody updateRequest: UpdateCommentRequest,
    ): ResponseEntity<UpdateCommentResponse> {
        val comment =
            commentService.update(
                commentId = id,
                postId = postId,
                content = updateRequest.content,
                user = user,
            )
        return ResponseEntity.ok(comment)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "댓글 삭제",
        description = "게시글 Id와 댓글 Id를 입력받아 해당 댓글 삭제"
    )
    fun delete(
        @PathVariable postId: Long,
        @PathVariable id: Long,
        @LoggedInUser user: User,
    ): ResponseEntity<Unit> {
        commentService.delete(
            commentId = id,
            postId = postId,
            user = user,
        )
        return ResponseEntity.noContent().build()
    }
}
