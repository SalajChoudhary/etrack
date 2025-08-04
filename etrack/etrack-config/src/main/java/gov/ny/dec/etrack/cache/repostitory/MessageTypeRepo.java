package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gov.ny.dec.etrack.cache.entity.MessageType;

@Repository
public interface MessageTypeRepo extends CrudRepository<MessageType, Integer> {

	List<MessageType> findAllByOrderByMessageTypeDescription();

}
