package org.example.camunda.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.example.camunda.entity.CustomTask;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<CustomTask, Integer> {
    @EntityGraph(value = "fetch-without-history")
    @Query(value = "SELECT c FROM CustomTask c WHERE c.id = :id")
    CustomTask getCustomTaskById(@Param("id") int id);

    @EntityGraph(value = "fetch-without-history")
    @Query(value = "SELECT c FROM CustomTask c WHERE (:closed is null OR c.closed = :closed)")
    Page<CustomTask> getCustomTaskList(@Param("closed") Boolean closed, Pageable pageable);
}
