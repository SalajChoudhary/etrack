package gov.ny.dec.dart.district.model;

import java.util.List;
import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class Document {
  private Long documentId;
  private String accessByDepOnly;
  private String documentReleasableCode;
  private String documentReleasableDesc; 
  private Integer docCategory;
  private Integer docSubCategory;
  private String documentClassName;
  private String documentStateCode;
  private String description;
  private String documentName;
  private String history;
  private String otherDocSubCategory;
  private String createdBy;
  private String modifiedBy;
  private String uploadDateTime;
  private List<AttachedFile> files;
  private List<String> docNonRelReasonCodes;
  private String trackedApplicationId; 
  private Long projectId;
  private Long queryResultId;
  private Integer documentReviewedInd;
}
