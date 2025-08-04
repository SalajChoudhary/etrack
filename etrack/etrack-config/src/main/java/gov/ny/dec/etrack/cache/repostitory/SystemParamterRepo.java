package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.SystemParameterEntity;

@Repository
public interface SystemParamterRepo  extends CrudRepository<SystemParameterEntity, String>{

	List<SystemParameterEntity> findAllByOrderByUrlId();

}
