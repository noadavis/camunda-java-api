package org.example.camunda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.camunda.entity.ProcessDef;

import java.util.List;

public interface ProcessRepository extends JpaRepository<ProcessDef, Integer> {

    @Query(value = "SELECT m FROM ProcessDef m WHERE m.id = :id")
    ProcessDef getById(@Param("id") int id);

    @Query(value = "SELECT m FROM ProcessDef m WHERE m.processId = :id")
    ProcessDef getByProcessId(@Param("id") String id);

    @Query(value = "SELECT m FROM ProcessDef m WHERE m.processKey = :key")
    List<ProcessDef> getByProcessKey(@Param("key") String key);

    @Query(
            value = "SELECT p.* FROM ( SELECT max(version) AS version, process_key FROM bpmn_process GROUP BY process_key" +
            ") AS t LEFT JOIN bpmn_process AS p ON t.version = p.version AND t.process_key = p.process_key",
            nativeQuery = true
    )
    List<ProcessDef> getProcessListWithGroup();
}
