package cat.board.dto;

import cat.board.entity.Memo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemoResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static MemoResponse from(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getContent(),
                memo.getCreatedAt()
        );
    }
}
