package com.support.repository;

import com.support.domain.Municipality;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Long> {
    boolean existsByCode(String code);

    boolean existsByRegion(String region);

    Municipality findByRegion(String region);
}
