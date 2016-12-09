package net.topikachu.sqlserver.jpa.repository;

import net.topikachu.sqlserver.jpa.entity.SampleEntity;

import java.util.List;

/**
 * Created by gongy on 2016/12/9.
 */
public interface CustSample {
    public List<SampleEntity> findSamplesAt(String startId, String partitionEndId);
}
