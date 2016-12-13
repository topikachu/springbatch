package net.topikachu.sqlserver.exception;

/**
 * Created by gongy on 2016/12/12.
 */
public class InvalidRecordBaseException extends RuntimeException {
    private String entityName;
    private String entityId;

    public InvalidRecordBaseException(String entityName, String entityId, String message) {
        super(message);
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
