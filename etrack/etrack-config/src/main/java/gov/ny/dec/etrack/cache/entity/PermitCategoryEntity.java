package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_PERMIT_CATEGORY")
public @Data class PermitCategoryEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "e_permit_category_s")
  @SequenceGenerator(name = "e_permit_category_s", sequenceName = "e_permit_category_s", allocationSize = 1)
  @Column(name="PERMIT_CATEGORY_ID")
  private Integer permitCategoryId;
  
  @Column(name="PERMIT_CATEGORY_DESC")
  private String permitCategoryDescription;
  @Column(name="ACTIVE_IND")
  private Integer activeInd;
  
  @Column(name="CREATED_BY_ID")
  private String createdById;
  
  @Column(name="CREATE_DATE")
  private Date createDate;
  
  @Column(name="MODIFIED_BY_ID")
  private String modifiedById;
  
  @Column(name="MODIFIED_DATE")
  private Date modifiedDate;


}
