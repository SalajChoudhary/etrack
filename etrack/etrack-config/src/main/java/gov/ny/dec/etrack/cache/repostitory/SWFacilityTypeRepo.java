package gov.ny.dec.etrack.cache.repostitory;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.SWFacilityTypeEntity;

@Repository
public interface SWFacilityTypeRepo  extends CrudRepository<SWFacilityTypeEntity, Integer>{
  
//    @Query(value="FROM SWFacilityTypeEntity t where lower(t.facilityTypeDescription)=?1")
	Optional<SWFacilityTypeEntity> findByFacilityTypeDescriptionIgnoreCase(String facilityTypeDesc);
	
	@Query(value="FROM SWFacilityTypeEntity t where lower(t.facilityTypeDescription)=?1 and t.ftReg=?2")
	Optional<SWFacilityTypeEntity> findByDescriptionAndRegulationCode(String description, String regulationCode);

	@Query(value="FROM SWFacilityTypeEntity t where lower(t.ftReg)=?1 and t.swFacilityTypeId!=?2")
	Optional<SWFacilityTypeEntity> findByFtRegForUpdateValidation(String regulationCode, Integer facilityTypeId );
	
	@Query(value="FROM SWFacilityTypeEntity t where lower(t.facilityTypeDescription)=?1 and t.swFacilityTypeId!=?2")
	Optional<SWFacilityTypeEntity> findByFacilityTypeDescriptionForUpdateValidation(String facilityTypeDesc, Integer facilityTypeId );

	Optional<SWFacilityTypeEntity> findByFtRegIgnoreCase(String regulationCode);
}
