package dec.ny.gov.etrack.dms.model;

import java.util.List;
import java.util.Map;
import dec.ny.gov.etrack.dms.request.AttachmentMetaData;
import lombok.Data;

public @Data class DMSDocumentMetaData {
  private String documentId;
  private Integer attachedCECount;
  private Map<String, String> metaDataProperties;
  private List<AttachmentMetaData> attachmentMetaDatas;
}
