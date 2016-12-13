package net.topikachu.sqlserver.jpa.repository;

import net.topikachu.sqlserver.jpa.entity.ErrorEntity;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by gongy on 2016/12/8.
 */
public interface ErrorRepository extends JpaRepository<ErrorEntity, String> {


}
