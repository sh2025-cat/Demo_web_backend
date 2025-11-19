package cat.board.controller;

import cat.board.dto.MemoRequest;
import cat.board.dto.MemoResponse;
import cat.board.service.MemoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemoService memoService;

    @Test
    @DisplayName("POST /api/memos - 메모 생성 성공")
    void createMemo_Success() throws Exception {
        // given
        MemoRequest request = new MemoRequest("테스트 메모");
        MemoResponse response = new MemoResponse(1L, "테스트 메모", LocalDateTime.now());

        when(memoService.createMemo(any(MemoRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("테스트 메모"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(memoService, times(1)).createMemo(any(MemoRequest.class));
    }

    @Test
    @DisplayName("GET /api/memos - 메모 목록 조회 성공")
    void getAllMemos_Success() throws Exception {
        // given
        List<MemoResponse> responses = Arrays.asList(
                new MemoResponse(1L, "메모 1", LocalDateTime.now()),
                new MemoResponse(2L, "메모 2", LocalDateTime.now())
        );

        when(memoService.getAllMemos()).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("메모 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].content").value("메모 2"));

        verify(memoService, times(1)).getAllMemos();
    }

    @Test
    @DisplayName("DELETE /api/memos/{id} - 메모 삭제 성공")
    void deleteMemo_Success() throws Exception {
        // given
        Long memoId = 1L;
        doNothing().when(memoService).deleteMemo(memoId);

        // when & then
        mockMvc.perform(delete("/api/memos/{id}", memoId))
                .andExpect(status().isNoContent());

        verify(memoService, times(1)).deleteMemo(memoId);
    }

    @Test
    @DisplayName("DELETE /api/memos/{id} - 존재하지 않는 메모 삭제 시 예외")
    void deleteMemo_NotFound() throws Exception {
        // given
        Long memoId = 999L;
        doThrow(new IllegalArgumentException("메모를 찾을 수 없습니다. id: " + memoId))
                .when(memoService).deleteMemo(memoId);

        // when & then
        mockMvc.perform(delete("/api/memos/{id}", memoId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("메모를 찾을 수 없습니다. id: " + memoId));

        verify(memoService, times(1)).deleteMemo(memoId);
    }
}
