package dec.ny.gov.etrack.dcs.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SpatialInquiryDocRequest {

  private Integer attachmentFilesCount;
  private String userId;
  private String guid;
  @JsonProperty("fileDates")
  private Map<String, String> fileDates;
  private String documentTitle;
  private Integer documentTitleId;
  private String documentSubType;
  private String documentType;
}
