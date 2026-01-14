package com.muta.assessment.repository;

import com.muta.assessment.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {

    Optional<License> findByIdAndStatus(Long id, String status);
}
