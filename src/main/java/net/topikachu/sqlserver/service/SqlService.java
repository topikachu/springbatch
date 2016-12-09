package net.topikachu.sqlserver.service;

import net.topikachu.sqlserver.jpa.repository.SampleRepository;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by gongy on 2016/12/8.
 */
@Component
public class SqlService {
    @Autowired
    private SampleRepository sampleRepository;

    @Transactional
    public void insert() {
//        sampleRepository.save(
//                IntStream.range(0, 1000)
//                        .mapToObj(i -> new SampleEntity("message " + i))
//                        .collect(Collectors.toList())
    }

    ;

    @Transactional
    public List<String> partitionIds(int partitionNumber) {
        long count = sampleRepository.count();
        long partitionSize = count / partitionNumber;

        return LongStream.iterate(partitionSize, i -> i + partitionSize)
                .limit(partitionNumber - 1)
                .mapToObj(i -> new Tuple2<String, Long>(sampleRepository.findIdAt(i), i))
                .map(t -> t.v1())
                .collect(Collectors.toList());


    }

}
