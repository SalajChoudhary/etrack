package gov.ny.dec.etrack.cache.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_TRANSACTION_TYPE_RULE")
public @Data class TransactionTypeRuleEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_TRANSACTION_TYPE_RULE_S")
  @SequenceGenerator(name = "E_TRANSACTION_TYPE_RULE_S", sequenceName = "E_TRANSACTION_TYPE_RULE_S", allocationSize = 1)
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
  private String edbTransTypeCode;
  private Integer activeInd;
  private Long documentSubTypeTitleId;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
}
