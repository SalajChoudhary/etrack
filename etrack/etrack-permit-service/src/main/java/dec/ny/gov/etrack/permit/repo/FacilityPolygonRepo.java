package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.FacilityPolygon;

@Repository
public interface FacilityPolygonRepo extends CrudRepository<FacilityPolygon, Long> {
  
  List<FacilityPolygon> findByProjectIdOrderByFacilityPolygonIdAsc(Long projectId);
  
  @Query(value="select * from {h-schema}e_facility_polygon where project_id=?1 and polygon_type_code=?2", nativeQuery=true)
  FacilityPolygon findAnalystScratchPolygonByProjectIdAndPolygonTypeCode(Long projectId, Integer polygonTypeCode);
  
  @Modifying
  @Query(value = "update {h-schema}e_facility_polygon set polygon_type_code=?1 where project_id=?2", nativeQuery = true)
  void updateFacilityPolygonDetail(Integer polygonTypeCode, Long projectId);
  
}
