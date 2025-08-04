package dec.ny.gov.etrack.permit.model;

import lombok.Data;

public @Data class Relationship {
  private Long roleId;
  private Integer roleTypeId;
  private Integer propertyRelCode;
  private Long edbRoleId;
}
