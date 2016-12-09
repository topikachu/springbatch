package net.topikachu.sqlserver.jpa.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import net.topikachu.sqlserver.jpa.entity.QSampleEntity;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by gongy on 2016/12/9.
 */
public class SampleRepositoryImpl implements CustSample {

    @Autowired
    private EntityManager em;

    @Override
    public List<SampleEntity> findSamplesAt(String startId, String partitionEndId) {
        JPAQuery<?> query = new JPAQuery<SampleEntity>(em);
        QSampleEntity sampleEntity = QSampleEntity.sampleEntity;
        BooleanBuilder builder = new BooleanBuilder();
        if (startId != null) {
            builder.and(sampleEntity.id.goe(startId));
        }
        if (partitionEndId != null) {
            builder.and(sampleEntity.id.lt(partitionEndId));
        }
        return query.select(sampleEntity)
                .from(sampleEntity)
                .where(builder)
                .orderBy(sampleEntity.id.asc())
                .limit(10)
                .fetch();


    }
}
