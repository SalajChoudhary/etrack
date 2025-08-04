package gov.ny.dec.etrack.cache.model;

import lombok.Data;

@Data
public  class PermitTypeCode {
  
  private String permitTypeCode;
  private String permitCategoryDesc;
  private String permitTypeDesc;
  private Integer permitCategoryId;
  private Integer generalPermitInd;
  private String relatedRegularPermitTypeDescForGp;
  private String relatedRegularPermitTypeCodeForGp;
  private String activeInd;
  private String natResInd;
}
