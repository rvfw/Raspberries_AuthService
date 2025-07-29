package com.example.raspberriesAuthService.repository;

import com.example.raspberriesAuthService.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findByProcessedFalse();
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.processed=true WHERE e.id=:id")
    void markAsProcessed(@Param("id") Long id);
}
