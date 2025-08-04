package dec.ny.gov.etrack.dart.db.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class PublicAssociatedFacility implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  @Id
  @JsonProperty("applicantId")
  private Long publicId;
  @JsonProperty("firstName")
  private String fname;
  @JsonProperty("lastName")
  private String lname;
  @JsonProperty("middleName")
  private String mname;
  @JsonProperty("suffix")
  private String sfxname;
  private String name;
  private String roleTypeDesc;
  private Integer rolePrimaryInd;
  private Long districtId;
  private String districtName;
  private String decId;
  private String decIdFormatted;
  private String locationDirections;
  private String city;
  private String state;
  private String zip;
  private String country;
  private String zipExtension;
  private String phoneNumber;
  private String standardCode;
  private String longLat;
}
