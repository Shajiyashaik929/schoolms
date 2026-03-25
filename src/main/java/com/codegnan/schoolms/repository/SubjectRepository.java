package com.codegnan.schoolms.repository;

import com.codegnan.schoolms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    // Standard CRUD operations inherited from JpaRepository.
}
