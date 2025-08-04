package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_MESSAGE")
public @Data class MessageEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name="MESSAGE_CODE")
  private String messageCode;
  
  @Column(name="MESSAGE_DESC")
  private String messageDesc;
  
  @Column(name="MESSAGE_TYPE_ID")
  private Integer messageTypeId;
 
  @Column(name="CREATED_BY_ID")
  private String createdById;
  
  @Column(name="CREATE_DATE")
  private Date createDate;
  
  @Column(name="MODIFIED_BY_ID")
  private String moifiedById;
  
  @Column(name="MODIFIED_DATE")
  private Date modifiedDate;


}
