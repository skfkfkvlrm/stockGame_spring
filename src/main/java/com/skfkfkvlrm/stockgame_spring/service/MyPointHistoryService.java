package com.skfkfkvlrm.stockgame_spring.service;

import java.util.List;
import java.util.Map;

public interface MyPointHistoryService {
    List<Map<String, Object>> getMyPointHistoryList(String studentId);
}
