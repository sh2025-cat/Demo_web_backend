package cat.board.service;

import cat.board.dto.MemoRequest;
import cat.board.dto.MemoResponse;
import cat.board.entity.Memo;
import cat.board.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;

    @Transactional
    public MemoResponse createMemo(MemoRequest request) {
        Memo memo = new Memo(request.getContent());
        Memo savedMemo = memoRepository.save(memo);
        return MemoResponse.from(savedMemo);
    }

    public List<MemoResponse> getAllMemos() {
        return memoRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(MemoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다. id: " + id));
        memoRepository.delete(memo);
    }
}
