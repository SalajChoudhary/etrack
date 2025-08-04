package dec.ny.gov.etrack.fmis.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.fmis.entity.FMISInvoice;

@Repository
public interface InvoiceRepo extends CrudRepository<FMISInvoice, Long> {
  @Query("select inv from FMISInvoice inv where inv.fmisInvoiceNum=:invoiceNumber and inv.projectId=:projectId")
  List<FMISInvoice> findByFmisInvoiceNumAndProjectId(final String invoiceNumber, final Long projectId);

  @Query("select inv from FMISInvoice inv where inv.fmisInvoiceNum=:invoiceNumber and deleteInd=0 and inv.projectId=:projectId")
  List<FMISInvoice> findActiveByFmisInvoiceNumAndProjectId(final String invoiceNumber, final Long projectId);

  
  @Query("select inv from FMISInvoice inv where inv.fmisInvoiceNum=:invoiceNumber and inv.deleteInd=0 order by inv.invoiceId desc")
  List<FMISInvoice> findByFmisInvoiceNum(final String invoiceNumber);

  @Query("select inv from FMISInvoice inv where inv.fmisInvoiceNum=:invoiceNumber and deleteInd=0 and inv.vpsTxnId=:transactionId")
  List<FMISInvoice> findByFmisInvoiceNumAndTransactionId(final String invoiceNumber, final String transactionId);

  @Query("select inv from FMISInvoice inv where inv.projectId=:projectId and deleteInd=0")
  List<FMISInvoice> findByProjectId(final Long projectId);

  @Query("select inv from FMISInvoice inv where inv.publicId=:publicId and inv.projectId=:projectId and deleteInd=0")
  List<FMISInvoice> findByPublicIdAndProjectId(final Long publicId, final Long projectId);
}
