package com.skfkfkvlrm.stockgame_spring.repository.jpa;

import com.skfkfkvlrm.stockgame_spring.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyAssetRepository extends JpaRepository<Student, String> {
}
