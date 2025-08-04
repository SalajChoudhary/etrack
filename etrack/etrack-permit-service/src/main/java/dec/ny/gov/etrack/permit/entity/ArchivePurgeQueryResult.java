package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="E_ARC_PRG_QUERY_RESULT")
public @Data class ArchivePurgeQueryResult implements Serializable {

  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name = "ARC_PRG_QUERY_RESULT_ID")
  private Long resultId;
  @Column(name = "COMPLETED_IND")
  private Integer completedInd;
  @Column(name = "ANALYST_REVIEWED_IND")
  private Integer analystReviewedInd;
  @Column(name="ARC_PRG_QUERY_NAME_CODE")
  private Integer queryNameCode;
  private String reviewedAnalystId;
  private String reviewedAdminId;
}
