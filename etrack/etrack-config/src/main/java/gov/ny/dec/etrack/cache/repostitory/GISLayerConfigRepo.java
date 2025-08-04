package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.GISLayerConfig;

@Repository
public interface GISLayerConfigRepo extends CrudRepository<GISLayerConfig, String>{
  List<GISLayerConfig> findByActiveInd(Integer activeInd);
  @Query(value="select * from {h-schema}e_gis_layer_config order by layer_name", nativeQuery=true)
  List<GISLayerConfig> findAllOrderByLayerName();
  @Query(value="select * from {h-schema}e_gis_layer_config where lower(layer_name)=lower(?1)", nativeQuery=true)
  List<GISLayerConfig> findByLayerName(String layerName);
  @Query(value="select * from e_gis_layer_config where lower(layer_name)!= ?1 and order_ind=?2", nativeQuery = true)
  List<GISLayerConfig> findByLayerNameAndOrder(String layerName, Integer order);
}
