package com.aintopia.aingle.post.dto.Response;

import com.aintopia.aingle.character.dto.PostCharacter;
import com.aintopia.aingle.comment.dto.CommentDto;
import com.aintopia.aingle.member.dto.PostMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponseDto {
    private Long postId;
    private String content;
    private String image;
    private LocalDateTime createTime;
    private Long totalLike;
    private Long totalComment;
    private PostMember member;
    private PostCharacter character;
    private Boolean isLiked;
    private List<CommentDto> comments;
}
