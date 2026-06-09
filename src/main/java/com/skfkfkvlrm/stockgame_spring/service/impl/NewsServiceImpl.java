package com.skfkfkvlrm.stockgame_spring.service.impl;

import com.skfkfkvlrm.stockgame_spring.repository.NewsMapper;
import com.skfkfkvlrm.stockgame_spring.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsMapper newsMapper;

    @Override
    public List<String> getNewsList() {
        List<String> newsList = newsMapper.getNewsList();

        return newsList;
    }
}
