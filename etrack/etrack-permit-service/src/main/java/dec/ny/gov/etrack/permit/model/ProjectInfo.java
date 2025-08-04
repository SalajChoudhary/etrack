package dec.ny.gov.etrack.permit.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

public @Data class ProjectInfo {
  private Long projectId;
  private String briefDesc;
  private String proposedUse;
  private List<BridgeIdNumber> binNumbers;
  private List<Map<String, String>> sicCodeNaicsCode;
  private List<Integer> developmentType;
  private List<Integer> structureType;
  private Integer constrnType;
  private String proposedStartDate;
  private String estmtdCompletionDate;
  private String strWaterbodyName;
  private String wetlandIds;
  private Date proposedStartDateVal;
  private Date estmtdCompletionDateVal;
  private List<SWFacilityType> swFacilityTypes;
  private String damType;
  private Integer classifiedUnderSeqr;
  private Map<String, String> xtraIds;
  private Map<String, List<String>> programIds;
  private List<String> splAttnCodes;  
}
