package cat.board.controller;

import cat.board.dto.MemoRequest;
import cat.board.dto.MemoResponse;
import cat.board.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@RequestBody MemoRequest request) {
        MemoResponse response = memoService.createMemo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MemoResponse>> getAllMemos() {
        List<MemoResponse> memos = memoService.getAllMemos();
        return ResponseEntity.ok(memos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.noContent().build();
    }
}
