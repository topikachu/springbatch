package net.topikachu.sqlserver.jpa.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gongy on 2016/11/16.
 */
@Entity
@Table(name = "sample", indexes = {

})
public class SampleEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SampleEntity(String message) {
        this.message = message;
    }

    public SampleEntity() {
    }

    @Override
    public String toString() {
        return "SampleEntity{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
