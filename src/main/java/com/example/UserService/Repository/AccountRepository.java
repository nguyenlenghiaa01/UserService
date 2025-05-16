package com.example.UserService.Repository;


import com.example.UserService.Enity.Account;
import com.example.UserService.Enum.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AccountRepository extends JpaRepository<Account, String> {
    Account findAccountByUuid(String id);
    Account findAccountByUserName(String userName);
    Account findAccountByEmail(String email);

    Page<Account> findAll(Pageable pageable);
    @Query("SELECT a FROM Account a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Account> searchAccount(@Param("name") String name, Pageable pageable);

}
