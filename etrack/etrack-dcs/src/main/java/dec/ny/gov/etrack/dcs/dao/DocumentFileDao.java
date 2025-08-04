package dec.ny.gov.etrack.dcs.dao;

import java.io.Serializable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import dec.ny.gov.etrack.dcs.model.DocumentFile;

@Repository
public interface DocumentFileDao extends CrudRepository<DocumentFile, Serializable> {
  
  /**
   * Retrieve the document file for the input document.
   * 
   * @param documentId - Document Id.
   * 
   * @return - Document File {@link DocumentFile}
   */
  DocumentFile findDocumentFileByDocumentId(Long documentId);
}
