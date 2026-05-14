package org.example.smartunipro.repository;

import org.example.smartunipro.entity.User;
import org.example.smartunipro.model.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends FilterableRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByAcademicNumber(String academicNumber);
    List<User> findByRole(Role role);
}