package com.example.cbox.repository;

import com.example.cbox.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByLink(String fileName);

    Page<File> findAllByCreatedBy(String createdBy, Pageable pageable);

    void deleteByLink(String fileName);
}
