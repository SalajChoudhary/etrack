package gov.ny.dec.dart.district.model;

import java.util.List;
import java.util.Set;
import lombok.Data;

public @Data class DistrictDetail {
  private Long decId;
  private Long districtId;
  private String facilityName;
  private List<Document> documents;
  private Set<Long> litigationHoldProjects;
}
