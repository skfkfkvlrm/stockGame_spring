package com.skfkfkvlrm.stockgame_spring.repository.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<String> getNewsList();
}
