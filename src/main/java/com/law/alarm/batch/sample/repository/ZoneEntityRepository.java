package com.law.alarm.batch.sample.repository;

import com.law.alarm.batch.sample.entity.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneEntityRepository extends JpaRepository<ZoneEntity, Long> {
}
