package com.projectmanagement.multitenantprojectmanagement.core.comment;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentReplyRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.CreateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.request.UpdateCommentRequest;
import com.projectmanagement.multitenantprojectmanagement.core.comment.dto.response.CommentResponse;
import com.projectmanagement.multitenantprojectmanagement.core.comment.mapper.CommentMapper;
import com.projectmanagement.multitenantprojectmanagement.organizations.dto.response.PaginatedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/v1/comment/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        Comment comment = commentService.getCommentById(id);

        return ResponseEntity.ok(CommentMapper.toCommentResponse(comment));
    }

    @GetMapping("/v1/comment/{id}/issue")
    public ResponseEntity<PaginatedResponseDto<CommentResponse>> getCommentsByIssueId(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<CommentResponse> comments = commentService.getCommentsByIssueId(id, pageable);

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/v1/comment/{id}/replies")
    public ResponseEntity<PaginatedResponseDto<CommentResponse>> getCommentReplies(@PathVariable UUID id, Pageable pageable,@AuthenticationPrincipal Jwt jwt) {
        PaginatedResponseDto<CommentResponse> comments = commentService.getRepliesByCommentId(id, pageable);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/v1/comment")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CreateCommentRequest createCommentRequest,@AuthenticationPrincipal Jwt jwt) {
        CommentResponse commentResponse = commentService.createComment(createCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PostMapping("/v1/comment/reply")
    public ResponseEntity<CommentResponse> createCommentReply(@RequestBody CreateCommentReplyRequest createCommentReplyRequest,@AuthenticationPrincipal Jwt jwt) {
        CommentResponse commentResponse = commentService.createCommentReply(createCommentReplyRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/v1/comment")
    public ResponseEntity<CommentResponse> updateComment(@RequestBody UpdateCommentRequest updateCommentRequest,@AuthenticationPrincipal Jwt jwt) {
        CommentResponse commentResponse = commentService.updateComment(updateCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/v1/comment/{id}")
    public ResponseEntity<CommentResponse> deleteCommentById(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        CommentResponse commentResponse = commentService.deleteComment(id);

        return ResponseEntity.ok(commentResponse);
    }

}
