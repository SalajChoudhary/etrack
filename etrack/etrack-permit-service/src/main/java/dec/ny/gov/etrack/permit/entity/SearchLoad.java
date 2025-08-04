package dec.ny.gov.etrack.permit.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="E_SEARCH_LOAD")
public @Data class SearchLoad {
  @Id
  private Integer searchLoadId;
  private Date lastLoadDate;
}
