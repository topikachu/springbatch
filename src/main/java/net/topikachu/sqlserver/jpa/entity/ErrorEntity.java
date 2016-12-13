package net.topikachu.sqlserver.jpa.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by gongy on 2016/12/12.
 */
@Entity
@Table(name = "error", indexes = {

})
public class ErrorEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String errorEntityName;

    private String errorEntityId;

    private String errorMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErrorEntityName() {
        return errorEntityName;
    }

    public void setErrorEntityName(String errorEntityName) {
        this.errorEntityName = errorEntityName;
    }

    public String getErrorEntityId() {
        return errorEntityId;
    }

    public void setErrorEntityId(String errorEntityId) {
        this.errorEntityId = errorEntityId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorEntity{" +
                "id='" + id + '\'' +
                ", errorEntityName='" + errorEntityName + '\'' +
                ", errorEntityId='" + errorEntityId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
