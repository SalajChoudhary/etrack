package dec.ny.gov.etrack.permit.repo;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.UploadPolygonEntity;

@Repository
public interface UploadPolygonRepo extends CrudRepository<UploadPolygonEntity, Long> {
  @Query(value="select * from {h-schema}e_polygon_for_upload where (retry_counter is null or retry_counter <= 10) ", nativeQuery=true)
  List<UploadPolygonEntity> findAllUploadEligiblePolygon();
  
  @Transactional
  @Modifying
  @Query(value="delete {h-schema}e_polygon_for_upload where project_id=?1", nativeQuery=true)
  void deleteByProjectId(Long projectId);
  
  @Query(value="select * from {h-schema}e_polygon_for_upload where project_id=?1", nativeQuery=true)
  UploadPolygonEntity findByProjectId(Long projectId);

  @Transactional
  @Modifying
  @Query(value="update {h-schema}e_polygon_for_upload set retry_counter=?2 where project_id=?1", nativeQuery=true)
  void updateRetryCountByProjectId(Long projectId, int retryCounter);
}

