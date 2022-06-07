package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);
	@Query("select user from User user where user.name like %:searchValue% or user.patronymic like %:searchValue% or user.surname like %:searchValue% or user.username like %:searchValue%")
	List<User> findAllBySearchValue(@Param(value = "searchValue") String searchValue);
	List<User> findAll();
}
