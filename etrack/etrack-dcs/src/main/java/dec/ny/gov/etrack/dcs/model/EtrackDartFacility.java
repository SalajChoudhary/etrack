package dec.ny.gov.etrack.dcs.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="E_DART_FACILITY")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EtrackDartFacility {
  @Id
  @Column(name = "EDB_DISTRICT_ID")   
  private Long districtId;
  @Column(name = "CREATE_DATE")
  private Date createDate;
  @Column(name = "MODIFIED_DATE")
  private Date modifiedDate;
  @Column(name = "CREATED_BY_ID")
  private String createdById;
  @Column(name = "MODIFIED_BY_ID")
  private String modifiedById;
}
