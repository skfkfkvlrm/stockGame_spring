package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.repository.MyPointHistoryRepository;
import com.skfkfkvlrm.stockgame_spring.service.MyPointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPointHistoryServiceImpl implements MyPointHistoryService {
    private final MyPointHistoryRepository myPointHistoryRepository;

    @Override
    public List<Map<String, Object>> getMyPointHistoryList(String studentId) {
        return myPointHistoryRepository.getMyPointHistoryList(studentId);
    }
}
