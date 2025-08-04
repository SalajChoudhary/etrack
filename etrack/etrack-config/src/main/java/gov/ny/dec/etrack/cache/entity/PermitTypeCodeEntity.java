package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
  private String permitTypeDescription; 

  @Column(name="PERMIT_CATEGORY_ID")
  private Integer permitCategoryId;
  
  @Column(name="ACTIVE_IND")
  private String activeInd;
  
  @Column(name="NATURAL_RESOURCE_TYPE_IND")
  private String natResInd;
  
  @Column(name="MODIFIED_BY_ID")
  private String moifiedById;
  
  @Column(name="MODIFIED_DATE")
  private Date modifiedDate;
  
  
  
  @Transient
  private String permitCategoryDescription;
  
  @ManyToOne
  @JoinColumn(name="PERMIT_CATEGORY_ID", insertable= false,updatable=false)
  private PermitCategoryEntity permitCategory;

}
