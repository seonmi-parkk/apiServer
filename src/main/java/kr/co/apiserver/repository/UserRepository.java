package kr.co.apiserver.repository;

import jakarta.persistence.Entity;
import kr.co.apiserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("select u from User u join fetch u.userRoleList where u.email = :email")
    Optional<User> getWithRoles(@Param("email") String email);
}
