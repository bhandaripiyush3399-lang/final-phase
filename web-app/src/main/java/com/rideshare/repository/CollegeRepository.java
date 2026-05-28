package com.rideshare.repository;

import com.rideshare.model.College;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findAllByOrderByCollegeNameAsc();
}
