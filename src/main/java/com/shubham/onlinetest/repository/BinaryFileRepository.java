package com.shubham.onlinetest.repository;

import com.shubham.onlinetest.model.entity.BinaryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BinaryFileRepository extends JpaRepository<BinaryFile, UUID> {
}
