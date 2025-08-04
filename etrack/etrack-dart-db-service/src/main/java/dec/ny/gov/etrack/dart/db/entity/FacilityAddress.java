package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class FacilityAddress implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  private String decIdFormatted;
  private String standardCode;
  private String districtName;
  private String locationDirections;
  private String city;
  private String state;
  private String zip;
  private String zipExtension;
  private String longLat;
  private String districtId;
  private String country;
  private String munic;
  private String lastKnownAppl;
}
