package org.example.repository;

import java.util.List;
import org.example.entity.Cipher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CipherRepository extends JpaRepository<Cipher, Integer> {

	List<Cipher> findAll();

	@Query("select cipher from Cipher cipher where cipher.id in :cipherIds")
	List<Cipher> findAllByIds(@Param("cipherIds") List<Integer> cipherIds);

	@Query("select cipher from Cipher cipher where cipher.name like %:searchValue% or cipher.description like %:searchValue%")
	List<Cipher> findAllBySearchValue(@Param(value = "searchValue") String searchValue);
}
