package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.permit.entity.SearchLoad;

@Repository
public interface SearchLoadRepo extends CrudRepository<SearchLoad, Integer>{

}
