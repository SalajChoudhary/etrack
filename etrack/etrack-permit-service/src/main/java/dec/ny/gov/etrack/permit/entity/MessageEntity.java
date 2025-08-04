package dec.ny.gov.etrack.permit.entity;

import java.io.Serializable;

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
 


}
