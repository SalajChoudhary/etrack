package gov.ny.dec.etrack.cache.model;

import javax.persistence.Id;
import lombok.Data;

public @Data class TransactionTypeRule {
  @Id
  private Long transactionTypeRuleId;
  private String permitTypeCode;
  private String transTypeCode;
  private Integer userSelNewInd;
  private Integer userSelModInd;
  private Integer userSelExtInd;
  private Integer userSelTransferInd;
  private Integer userSelRenInd;
  private Integer chgOriginalProjectInd;
  private String supportDocTransType;
  private Integer modExtFormInd;
  private Integer activeInd;
}
