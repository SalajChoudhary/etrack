package dec.ny.gov.etrack.permit.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dec.ny.gov.etrack.permit.entity.MessageEntity;
import dec.ny.gov.etrack.permit.entity.SystemParameterEntity;

@Repository
public interface MessageRepository  extends JpaRepository<MessageEntity, String>{

}
