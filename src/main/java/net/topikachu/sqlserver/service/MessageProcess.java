package net.topikachu.sqlserver.service;

import net.topikachu.sqlserver.exception.InvalidDataException;
import net.topikachu.sqlserver.exception.InvalidFormatException;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import net.topikachu.sqlserver.jpa.entity.TargetEntity;
import org.springframework.stereotype.Component;

/**
 * Created by gongy on 2016/12/9.
 */
@Component
public class MessageProcess {
    public TargetEntity exchange(SampleEntity sampleEntity) {
        String message = sampleEntity.getMessage();
        String[] sections = message.split("\\s");
        if (sections.length < 2) {
            throw new InvalidFormatException(sampleEntity.getClass().getName(), sampleEntity.getId(), "no enough sections");
        }
        int number;
        try {
            number = Integer.parseInt(sections[1]);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException(sampleEntity.getClass().getName(), sampleEntity.getId(), "wrong number section format");
        }
        // a mock exception
        if (number % 100 == 0) {
            throw new InvalidDataException(sampleEntity.getClass().getName(), sampleEntity.getId(), "wrong number " + number);
        }

        TargetEntity targetEntity = new TargetEntity();
        targetEntity.setMessage(sampleEntity.getMessage() + "processed");
        return targetEntity;
    }
}
