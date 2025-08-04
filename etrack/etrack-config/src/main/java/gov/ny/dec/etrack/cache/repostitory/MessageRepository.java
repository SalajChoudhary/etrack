package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.MessageEntity;
import gov.ny.dec.etrack.cache.entity.PermitCategoryEntity;

@Repository
public interface MessageRepository  extends CrudRepository<MessageEntity, String>{
	List<MessageEntity> findAllByOrderByMessageCode();
	Optional<MessageEntity> findByMessageCode(String messageCode);
}
