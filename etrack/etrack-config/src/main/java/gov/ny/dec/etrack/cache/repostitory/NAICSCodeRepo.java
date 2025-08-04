package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.NAICSCode;

@Repository
public interface NAICSCodeRepo extends CrudRepository<NAICSCode, Long> {
  
  @Query(value="select s.sic_naics_id, s.sic_code, s.naics_code, n.naics_desc "
      + "from {h-schema}e_naics_code n, {h-schema}e_sic_naics s "
      + "where n.naics_code=s.naics_code and s.sic_code=?1", nativeQuery = true)
  public List<NAICSCode> findAllBySicCode(String sicCode);
}
