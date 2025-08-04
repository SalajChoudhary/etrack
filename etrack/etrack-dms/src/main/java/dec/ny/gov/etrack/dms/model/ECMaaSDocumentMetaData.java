package dec.ny.gov.etrack.dms.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dec.ny.gov.etrack.dms.request.AttachmentMetaData;
import dec.ny.gov.etrack.dms.request.MetadataProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class ECMaaSDocumentMetaData {
  private String documentId;
  private Integer attachedCECount;
  private List<MetadataProperty> returnPropertyDefinitionIdList;
  private List<AttachmentMetaData> attachmentMetaDatas;
}
