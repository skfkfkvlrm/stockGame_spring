package com.skfkfkvlrm.stockgame_spring.controller;

import com.skfkfkvlrm.stockgame_spring.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/")
    public String getNewsList(Model model) {
        List<String> newsList = newsService.getNewsList();
        model.addAttribute("newsList", newsList);
        return "NewsList";
    }
}
