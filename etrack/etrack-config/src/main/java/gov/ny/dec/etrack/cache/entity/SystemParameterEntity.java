package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "E_SYSTEM_PARAMETER")
public @Data class SystemParameterEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Id
  @Column(name="URL_ID")
  private String urlId;
  @Column(name="URL_LINK")
  private String urlLink;
 


}
