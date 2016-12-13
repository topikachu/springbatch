package net.topikachu.sqlserver.exception;

/**
 * Created by gongy on 2016/12/12.
 */
public class InvalidFormatException extends InvalidRecordBaseException {

    public InvalidFormatException(String entityName, String entityId, String message) {
        super(entityName, entityId, message);
    }
}
