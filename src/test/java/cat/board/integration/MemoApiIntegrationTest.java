package cat.board.integration;

import cat.board.dto.MemoRequest;
import cat.board.dto.MemoResponse;
import cat.board.entity.Memo;
import cat.board.repository.MemoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemoRepository memoRepository;

    @AfterEach
    void tearDown() {
        memoRepository.deleteAll();
    }

    @Test
    @DisplayName("통합 테스트: 메모 생성")
    void createMemo_Integration() throws Exception {
        // given
        MemoRequest request = new MemoRequest("통합 테스트 메모");

        // when & then
        MvcResult result = mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value("통합 테스트 메모"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        // DB 검증
        assertThat(memoRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("통합 테스트: 메모 목록 조회")
    void getAllMemos_Integration() throws Exception {
        // given
        memoRepository.save(new Memo("메모 1"));
        memoRepository.save(new Memo("메모 2"));
        memoRepository.save(new Memo("메모 3"));

        // when & then
        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].content").exists())
                .andExpect(jsonPath("$[1].content").exists())
                .andExpect(jsonPath("$[2].content").exists());
    }

    @Test
    @DisplayName("통합 테스트: 메모 삭제")
    void deleteMemo_Integration() throws Exception {
        // given
        Memo savedMemo = memoRepository.save(new Memo("삭제할 메모"));
        Long memoId = savedMemo.getId();

        assertThat(memoRepository.count()).isEqualTo(1);

        // when & then
        mockMvc.perform(delete("/api/memos/{id}", memoId))
                .andExpect(status().isNoContent());

        // DB 검증
        assertThat(memoRepository.count()).isEqualTo(0);
        assertThat(memoRepository.findById(memoId)).isEmpty();
    }

    @Test
    @DisplayName("통합 테스트: 메모 생성 -> 조회 -> 삭제 플로우")
    void fullFlow_Integration() throws Exception {
        // 1. 메모 생성
        MemoRequest request = new MemoRequest("플로우 테스트 메모");

        MvcResult createResult = mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        MemoResponse createdMemo = objectMapper.readValue(responseBody, MemoResponse.class);
        Long memoId = createdMemo.getId();

        // 2. 메모 목록 조회
        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(memoId));

        // 3. 메모 삭제
        mockMvc.perform(delete("/api/memos/{id}", memoId))
                .andExpect(status().isNoContent());

        // 4. 삭제 후 조회
        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("통합 테스트: 존재하지 않는 메모 삭제 시 예외")
    void deleteMemo_NotFound_Integration() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(delete("/api/memos/{id}", nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("메모를 찾을 수 없습니다. id: " + nonExistentId));
    }
}
