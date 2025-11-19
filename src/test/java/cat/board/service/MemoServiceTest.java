package cat.board.service;

import cat.board.dto.MemoRequest;
import cat.board.dto.MemoResponse;
import cat.board.entity.Memo;
import cat.board.repository.MemoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

    @Mock
    private MemoRepository memoRepository;

    @InjectMocks
    private MemoService memoService;

    @Test
    @DisplayName("메모 생성 성공")
    void createMemo_Success() {
        // given
        MemoRequest request = new MemoRequest("테스트 메모");

        Memo savedMemo = new Memo("테스트 메모");
        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // when
        MemoResponse response = memoService.createMemo(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("테스트 메모");
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    @DisplayName("전체 메모 조회 성공")
    void getAllMemos_Success() {
        // given
        Memo memo1 = new Memo("메모 1");
        Memo memo2 = new Memo("메모 2");
        List<Memo> memos = Arrays.asList(memo1, memo2);

        when(memoRepository.findAllByOrderByCreatedAtDesc()).thenReturn(memos);

        // when
        List<MemoResponse> responses = memoService.getAllMemos();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("메모 1");
        assertThat(responses.get(1).getContent()).isEqualTo("메모 2");
        verify(memoRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("메모 삭제 성공")
    void deleteMemo_Success() {
        // given
        Long memoId = 1L;
        Memo memo = new Memo("삭제할 메모");
        when(memoRepository.findById(memoId)).thenReturn(Optional.of(memo));

        // when
        memoService.deleteMemo(memoId);

        // then
        verify(memoRepository, times(1)).findById(memoId);
        verify(memoRepository, times(1)).delete(memo);
    }

    @Test
    @DisplayName("존재하지 않는 메모 삭제 시 예외 발생")
    void deleteMemo_NotFound_ThrowsException() {
        // given
        Long memoId = 999L;
        when(memoRepository.findById(memoId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memoService.deleteMemo(memoId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메모를 찾을 수 없습니다");

        verify(memoRepository, times(1)).findById(memoId);
        verify(memoRepository, never()).delete(any(Memo.class));
    }
}
