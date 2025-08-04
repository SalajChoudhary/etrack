package dec.ny.gov.etrack.dart.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.dart.db.entity.SearchEntity;

@Repository
public interface SearchByAttributeRepo extends JpaRepository<SearchEntity, Integer> {

}
