package org.example.camunda.repository;

import org.example.camunda.entity.TaskDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DictionaryRepository extends JpaRepository<TaskDictionary, Integer> {
    @Query(value = "SELECT d FROM TaskDictionary d WHERE d.alias = :alias")
    List<TaskDictionary> getDictionaryList(@Param("alias") String alias);
}
