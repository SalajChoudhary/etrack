package gov.ny.dec.dart.district.model;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class AttachedFile {
  private Integer fileNbr;
  private String fileName;
  private Timestamp fileDate;
}
