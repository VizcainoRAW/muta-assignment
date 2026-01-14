package com.muta.assessment.repository;

import com.muta.assessment.entity.OperationalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<OperationalEvent, Long> { }