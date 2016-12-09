package net.topikachu.sqlserver.service;

import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import net.topikachu.sqlserver.jpa.entity.TargetEntity;
import org.springframework.stereotype.Component;

/**
 * Created by gongy on 2016/12/9.
 */
@Component
public class MessageProcess {
    public TargetEntity exchange(SampleEntity sampleEntity) {
        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setMessage(sampleEntity.getMessage() + "processed");
        return targetEntity;
    }
}
