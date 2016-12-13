package net.topikachu.sqlserver.service;

import net.topikachu.sqlserver.exception.InvalidRecordBaseException;
import net.topikachu.sqlserver.jpa.entity.ErrorEntity;
import net.topikachu.sqlserver.jpa.repository.ErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by gongy on 2016/12/12.
 */
@Component
public class SaveErrorService {
    @Autowired
    private ErrorRepository errorRepository;


    // @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void saveError(InvalidRecordBaseException error) {
        ErrorEntity errorEntity = new ErrorEntity();
        errorEntity.setErrorEntityId(error.getEntityId());
        errorEntity.setErrorEntityName(error.getEntityName());
        errorEntity.setErrorMessage(error.getMessage());
        errorRepository.save(errorEntity);

    }
}
