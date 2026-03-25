package com.codegnan.schoolms.repository;

import com.codegnan.schoolms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // All standard CRUD operations are inherited from JpaRepository.
    // No custom queries needed for Students at repository level;
    // complex analytics are handled via MarkRepository.
}
