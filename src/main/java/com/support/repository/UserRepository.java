package com.support.repository;

import com.support.domain.UserTable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserTable, String> {

}
