package dec.ny.gov.etrack.dart.db.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="E_PROJECT_SIC_NAICS")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSICNAICSCode {
  @Id
  private Long projectSicNaicsId;
  private String sicCode;
  private String naicsCode;
  private Date createDate;
  private String createdById;
  private Date modifiedDate;
  private String modifiedById;
//  @JsonBackReference
  @ManyToOne
  @JoinColumn(name="PROJECT_ID") 
  private Project project;
}
