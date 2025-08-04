package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.PermitTypeCodeEntity;

@Repository
public interface PermitTypeCodeRepo  extends CrudRepository<PermitTypeCodeEntity, String>{

	List<PermitTypeCodeEntity> findAllByOrderByPermitTypeDescription();
}
