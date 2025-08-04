package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.SWFacilityTypeSubEntity;

@Repository
public interface SWFacilitySubTypeRepo  extends CrudRepository<SWFacilityTypeSubEntity, Integer> {
	
	@Query(value="FROM SWFacilityTypeSubEntity t where lower(t.subTypeDescription)=?1 AND lower(t.subReg)=?2")
	Optional<SWFacilityTypeSubEntity> findByDescriptionAndRegulationCode(String description, String regulationCode);

	List<SWFacilityTypeSubEntity> findAllBySwFacilityTypeIdOrderBySubTypeDescription(Integer swFacilityTypeId);

	Optional<SWFacilityTypeSubEntity> findBySubRegIgnoreCase(String facilitySubTypeRegulationCode);

	Optional<SWFacilityTypeSubEntity> findBySubTypeDescriptionIgnoreCase(String facilitySubTypeDescription);
}
