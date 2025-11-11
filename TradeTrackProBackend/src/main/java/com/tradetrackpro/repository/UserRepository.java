package com.tradetrackpro.repository;

import com.tradetrackpro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
