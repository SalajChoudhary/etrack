package dec.ny.gov.etrack.dart.db.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.DartDbDAO;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddress;
import dec.ny.gov.etrack.dart.db.entity.FacilityAddressHistory;
import dec.ny.gov.etrack.dart.db.entity.FacilityDetail;
import dec.ny.gov.etrack.dart.db.entity.FacilityHistory;
import dec.ny.gov.etrack.dart.db.exception.NoDataFoundException;
import dec.ny.gov.etrack.dart.db.model.FacilityHistoryDto;
import dec.ny.gov.etrack.dart.db.service.DartFacilityService;
import dec.ny.gov.etrack.dart.db.service.TransformationService;
import dec.ny.gov.etrack.dart.db.util.DartDBConstants;

@Service
public class DartFacilityServiceImpl implements DartFacilityService {

  @Autowired
  private DartDbDAO dartDBDAO;
  @Autowired
  private TransformationService transformationService;
  private static final Logger logger = LoggerFactory.getLogger(DartFacilityServiceImpl.class.getName());
  
  /**
   * 
   */
  @Override
  public FacilityAddress getDECIDByProgramType(String userId, String contextId, String programId,
      String programType) {
    List<FacilityAddress> decIDlist =
        dartDBDAO.findDECIdByProgramType(userId, contextId, programId, programType);
    if (CollectionUtils.isEmpty(decIDlist)) {
      throw new NoDataFoundException("NO_FACILITY_FOUND",
          "There is no Facility data for the input program type and program id");
    }
    return decIDlist.get(0);
  }

  /**
   * 
   */
  @Override
  public List<FacilityAddress> getDECIDByTaxMap(String userId, String contextId, String txMap,
      final String county, final String municipality) {
    List<FacilityAddress> facilityDetails =
        dartDBDAO.findDECIdByTaxMap(userId, contextId, txMap, county, municipality);
    if (CollectionUtils.isEmpty(facilityDetails)) {
      throw new NoDataFoundException("NO_FACILITY_FOUND",
          "There is no data for the Tax Map, County and Municipality");
    }
    return facilityDetails;
  }

  /**
   * 
   */
  @Override
  public FacilityDetail getEtrackFacilityDetails(String userId, String contextId, Long projectId) {
    Map<String, Object> response = dartDBDAO.geETrackFacilityDetails(userId, contextId, projectId);
    return transformationService.getFacilityDetails(userId, contextId, response, projectId);
  }

  @Override
  public Object getAllMatchedFacilities(String userId, String contextId, String addrLine1,
      String city) {
    return dartDBDAO.getMatchedFacilityAddress(userId, contextId, addrLine1, city);
  }

  @Override
  public ResponseEntity<Object> retrieveFacilityHistory(String userId, String contextId,
      Long projectId) {
    logger.info("Entering into retrieveFacilityHistory. User Id {}. Context Id {}", userId, contextId);;
    Map<String, Object> facilityData =
        dartDBDAO.findFacilityHistoryDetail(userId, contextId, projectId);
    Map<String, Object> result = new LinkedHashMap<>();
    @SuppressWarnings("unchecked")
    List<FacilityDetail> facilityDetailsList =
        (List<FacilityDetail>) facilityData.get(DartDBConstants.FACILITY_CURSOR);
    FacilityDetail facilityDetail = null;
    if (!CollectionUtils.isEmpty(facilityDetailsList)) {
      facilityDetail = facilityDetailsList.get(0);
      if (StringUtils.hasText(facilityDetail.getDecId())) {
        facilityDetail.setDecIdFormatted(formatDECId(facilityDetail.getDecId()));
      }
      result.put("facility", facilityDetailsList.get(0));
    } else {
      result.put("facility", new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    List<FacilityHistory> facilityHistoryList =
        (List<FacilityHistory>) facilityData.get(DartDBConstants.FACILITY_HIST_CURSOR);
    @SuppressWarnings("unchecked")
    List<FacilityAddressHistory> facilityAddressHistoryDetails =
        (List<FacilityAddressHistory>) facilityData.get(DartDBConstants.FACILITY_ADDR_HIST_CURSOR);

    FacilityHistoryDto facilityHistoryDto = new FacilityHistoryDto();
    if (!CollectionUtils.isEmpty(facilityHistoryList)) {
      FacilityHistory faciliHistory = facilityHistoryList.get(0);
      facilityHistoryDto.setHProjectId(projectId);
      facilityHistoryDto.setHDecId(faciliHistory.getHDecId());
      facilityHistoryDto.setHFacilityName(faciliHistory.getHFacilityName());
      if (!CollectionUtils.isEmpty(facilityAddressHistoryDetails)) {
        FacilityAddressHistory facilityAddressHistory = facilityAddressHistoryDetails.get(0);
        facilityHistoryDto.setHLocationDirections(facilityAddressHistory.getHLocationDirections());
        facilityHistoryDto.setHCity(facilityAddressHistory.getHCity());
        if (StringUtils.hasLength(facilityAddressHistory.getHStreet1())) {
          String[] street1History = facilityAddressHistory.getHStreet1().split("\\|");
          facilityHistoryDto.setHStreet1(street1History[0]);
        }
        facilityHistoryDto.setHStreet2(facilityAddressHistory.getHStreet2());
        facilityHistoryDto.setHState(facilityAddressHistory.getHState());
        facilityHistoryDto.setHCountry(facilityAddressHistory.getHCountry());
        facilityHistoryDto.setHZip(facilityAddressHistory.getHZip());
        facilityHistoryDto.setHZipExtension(facilityAddressHistory.getHZipExtension());
      }
    }
    result.put("facilityHistory", facilityHistoryDto);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  private String formatDECId(String decId) {
    if (StringUtils.hasText(decId)) {
      StringBuilder sb = new StringBuilder();
      sb.append(decId.substring(0, 1)).append("-").append(decId.substring(1, 5)).append("-")
          .append(decId.substring(5));
      return sb.toString();
    }
    return decId;
  }
}
