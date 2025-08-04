package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.PermitCategoryEntity;

@Repository
public interface PermitCategoryRepo extends JpaRepository<PermitCategoryEntity, Integer> {

	Optional<PermitCategoryEntity> findByPermitCategoryDescription(String permitCategoryDesc);

	List<PermitCategoryEntity> findAllByOrderByPermitCategoryDescription();

}
