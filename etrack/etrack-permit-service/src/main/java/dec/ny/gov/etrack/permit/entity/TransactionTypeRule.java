package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_TRANSACTION_TYPE_RULE")
public @Data class TransactionTypeRule implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  private Integer transactionTypeRuleId;
  private String permitTypeCode;
  private Integer chgOriginalProjectInd;
  private Integer userSelTransferInd;
  private Integer userSelExtInd;
  private Integer userSelModInd;
  private Integer userSelNewInd;
  private Integer userSelRenInd;
  private String transTypeCode;
  private String edbTransTypeCode;
  private String supportDocTransType;
}
