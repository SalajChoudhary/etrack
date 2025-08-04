package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public @Data class Message implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
      
  @Id
  @Column(name="MESSAGE_TYPE_ID")
  private Integer messageTypeId;

  @Column(name="LANGUAGE_CODE")
  private String languageCode;
  
  @Column(name="MESSAGE_CODE")
  private String messageCode;
  
  @Column(name="MESSAGE_TYPE_DESC")
  private String messageTypeDesc;
  
  @Column(name="MESSAGE_DESC")
  private String messageDesc;
}
