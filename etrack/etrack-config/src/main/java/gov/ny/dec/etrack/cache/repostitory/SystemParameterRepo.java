package gov.ny.dec.etrack.cache.repostitory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.SystemParameterEntity;

@Repository
public interface SystemParameterRepo extends CrudRepository<SystemParameterEntity, String>{

}
