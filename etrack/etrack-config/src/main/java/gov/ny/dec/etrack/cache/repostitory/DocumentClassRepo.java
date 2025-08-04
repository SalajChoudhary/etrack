package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.DocumentClassEntity;

@Repository
public interface DocumentClassRepo extends CrudRepository<DocumentClassEntity, String>{

	List<DocumentClassEntity> findAllByOrderByDescription();

}