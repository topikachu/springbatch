package net.topikachu.sqlserver.jpa.repository;

import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by gongy on 2016/12/8.
 */
public interface SampleRepository extends JpaRepository<SampleEntity, String>, CustSample {

    @Query(value = "SELECT id  from sample\n" +
            "order by id \n" +
            "    OFFSET ?1 ROWS  \n" +
            "    FETCH NEXT 1 ROWS ONLY ", nativeQuery = true)
    String findIdAt(long offset);
}
