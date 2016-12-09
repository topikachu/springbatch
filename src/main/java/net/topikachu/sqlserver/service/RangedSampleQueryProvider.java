package net.topikachu.sqlserver.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import net.topikachu.sqlserver.jpa.entity.QSampleEntity;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by gongy on 2016/12/9.
 */
public class RangedSampleQueryProvider extends AbstractJpaQueryProvider {

    private String startId;
    private String endId;

    public RangedSampleQueryProvider(String startId, String endId) {
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public Query createQuery() {
        EntityManager em = super.getEntityManager();
        QSampleEntity sampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<?> query = new JPAQuery<SampleEntity>(em);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (startId != null) {
            booleanBuilder.and(sampleEntity.id.goe(startId));
        }
        if (endId != null) {
            booleanBuilder.and(sampleEntity.id.lt(endId));
        }
        Query jpaQuery = query.select(sampleEntity)
                .from(sampleEntity)
                .where(
                        booleanBuilder
                )
                .orderBy(sampleEntity.id.asc())
                .createQuery();
        return jpaQuery;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
