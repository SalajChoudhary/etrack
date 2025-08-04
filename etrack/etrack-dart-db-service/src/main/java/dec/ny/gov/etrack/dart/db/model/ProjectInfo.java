package dec.ny.gov.etrack.dart.db.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

public @Data class ProjectInfo {
  private Long projectId;
  private String briefDesc;
  private String proposedUse;
  private List<BridgeIdNumber> binNumbers;
  private List<BridgeIdNumber> binNumbersHistory;
  private List<Map<String, String>> sicCodeNaicsCode;
  private List<Integer> developmentType;
  private List<Integer> structureType;
  private Integer constrnType;
  private String proposedStartDate;
  private String estmtdCompletionDate;
  private String strWaterbodyName;
  private String wetlandIds;
  private String validatedInd;
  private List<SWFacilityType> swFacilityTypes;
  private String damType;
  private Integer classifiedUnderSeqr;
  private List<ProgramApplication> xtraIds;
  private List<ProgramDistrict> programIds;
  private List<String> splAttnCodes;
}
