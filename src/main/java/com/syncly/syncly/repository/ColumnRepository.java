package com.syncly.syncly.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.syncly.syncly.entity.ColumnEntity;

public interface ColumnRepository extends JpaRepository<ColumnEntity, UUID>, JpaSpecificationExecutor<ColumnEntity> {}
