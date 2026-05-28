package com.rideshare.repository;

import com.rideshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRollNumber(String rollNumber);
    boolean existsByRollNumber(String rollNumber);
}
