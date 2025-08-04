package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

public @Data class GeographicalInquirySummary implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long inquiryId;
  private String inquiryTypeCategory;
  private String requestIdentifier;
  private String assignedAnalystName;
  private List<GeographicalInquiryNoteView> geographicalInquiryNotes;
  private Map<String, Object> documents;
  private List<Document> reviewDocuments;
  private GeographicalInquiryResponse geographicalInquiryResponse;
  private InquiryContact contact;
  private String receivedDate;
  private String region;
  private String municipality;
  private String projectName;
}
