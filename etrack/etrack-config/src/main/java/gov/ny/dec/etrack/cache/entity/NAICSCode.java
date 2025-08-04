package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
//@Table(name = "E_SIC_NAICS")
public @Data class NAICSCode implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  private Long sicNaicsId;
  private String sicCode;
  private String naicsCode;
  private String naicsDesc;
}
