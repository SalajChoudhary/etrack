package gov.ny.dec.etrack.cache.model;

import lombok.Data;

public @Data class SupportDocumentMaintenance {
  private Long uniqueId;
  private Long documentTitleId;
  private Integer swFacilityTypeId;
  private Integer swFacilitySubTypeId;
  private String permitTypeCode;
  private String reqdNew;
  private String reqdMod;
  private String reqdExt;
  private String reqdMnm;
  private String reqdMtn;
  private String reqdRen;
  private String reqdRtn;
  private String reqdXfer;
  private Integer activeInd;
}
