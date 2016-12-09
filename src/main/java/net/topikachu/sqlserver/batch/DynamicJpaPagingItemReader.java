package net.topikachu.sqlserver.batch;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import net.topikachu.sqlserver.jpa.entity.QSampleEntity;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.AbstractJpaQueryProvider;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by gongy on 2016/12/9.
 */

public class DynamicJpaPagingItemReader<T> extends JpaPagingItemReader<T> {
    private ExecutionContext executionContext;


    public DynamicJpaPagingItemReader() {
        this.setQueryProvider(new AbstractJpaQueryProvider() {
            @Override
            public void afterPropertiesSet() throws Exception {

            }

            @Override
            public Query createQuery() {
                EntityManager em = super.getEntityManager();
                QSampleEntity sampleEntity = QSampleEntity.sampleEntity;
                JPAQuery<?> query = new JPAQuery<SampleEntity>(em);
                BooleanBuilder booleanBuilder = new BooleanBuilder();
                String startId = (String) executionContext.get("startId");
                if (startId != null) {
                    booleanBuilder.and(sampleEntity.id.goe(startId));
                }
                String endId = (String) executionContext.get("endId");
                if (startId != null) {
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
        });
    }

    @Override
    public void open(ExecutionContext executionContext) {
        super.open(executionContext);
        this.executionContext = executionContext;

    }


}
