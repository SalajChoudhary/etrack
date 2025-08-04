package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_PERMIT_TYPE_CODE")
public @Data class PermitTypeCodeEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name="PERMIT_TYPE_CODE")
  private String permitTypeCode;
  @Column(name="PERMIT_TYPE_DESC")
  private String permitTypeDesc;
}
