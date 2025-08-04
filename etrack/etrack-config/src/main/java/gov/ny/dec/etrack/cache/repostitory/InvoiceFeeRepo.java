package gov.ny.dec.etrack.cache.repostitory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import gov.ny.dec.etrack.cache.entity.InvoiceFeeTypeEntity;

@Repository
public interface InvoiceFeeRepo extends CrudRepository<InvoiceFeeTypeEntity, String> {
	Optional<InvoiceFeeTypeEntity> findByInvoiceFeeTypeIgnoreCase(String invoiceFeeType);
	List<InvoiceFeeTypeEntity> findByActiveInd(Integer activeInd);

}
