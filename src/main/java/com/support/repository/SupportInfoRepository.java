package com.support.repository;

import com.support.domain.SupportInfoTable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportInfoRepository extends CrudRepository<SupportInfoTable, Long> {
    List<SupportInfoTable> findAll();

    SupportInfoTable findByCode(String code);
}
