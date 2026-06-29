package com.skfkfkvlrm.stockgame_spring.repository;

import com.skfkfkvlrm.stockgame_spring.domain.MarketSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketSettingsRepository extends JpaRepository<MarketSettings, Integer> {
}
