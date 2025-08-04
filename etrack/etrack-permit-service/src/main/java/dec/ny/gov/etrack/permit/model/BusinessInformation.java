package dec.ny.gov.etrack.permit.model;

import lombok.Data;

public @Data class BusinessInformation {
  private String activeStatus;
  private String dosId;
  private String legalName;
  private String county;
  private String jurisdiction;
  private String filingDate;
  private String entityType;
}
