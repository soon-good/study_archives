package com.sec.example.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserModelRepository extends JpaRepository<UserModel, Long> {

	Optional<UserModel> findByUsername(String username);
}
