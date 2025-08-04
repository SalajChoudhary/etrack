package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "E_SIC_NAICS")
public @Data class SICNAICSMap {
  @Id
  private Long sicNaicsId;
  private String sicCode;
  private String naicsCode;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
}
