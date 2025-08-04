package dec.ny.gov.etrack.dart.db.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import dec.ny.gov.etrack.dart.db.dao.ETrackQueryReportDAO;
import dec.ny.gov.etrack.dart.db.entity.CandidateKeywordDetail;
import dec.ny.gov.etrack.dart.db.entity.SubmittedProjectDetail;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.model.CandidateKeywordReport;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittalReport;
import dec.ny.gov.etrack.dart.db.model.ProjectSubmittedRetrievalCriteria;
import dec.ny.gov.etrack.dart.db.repo.ETrackQueryReportRepo;
import dec.ny.gov.etrack.dart.db.service.ETrackQueryReportService;

@Service
public class ETrackQueryReportServiceImpl implements ETrackQueryReportService {

  @Autowired
  private ETrackQueryReportDAO eTrackQueryReportDAO;
  @Autowired
  private ETrackQueryReportRepo eTrackQueryReportRepo;
  
  private static final Logger logger = LoggerFactory.getLogger(ETrackQueryReportServiceImpl.class.getName());
  private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
  
  @Override
  public ProjectSubmittalReport retrieveProjectSubmittalDetails(final String userId,
      final String contextId, final ProjectSubmittedRetrievalCriteria queryCriteria) {
    
    logger.info("Entering into retrieveProjectSubmittalDetails. User Id {}, Context Id {}", userId, contextId);
    
    if (!(StringUtils.hasLength(queryCriteria.getStartDate()) 
        && StringUtils.hasLength(queryCriteria.getEndDate()))) {
      throw new BadRequestException("DATE_RANGE_NOT_PASSED", 
          "Date range is not passed to pull the report.", queryCriteria);
    }
    ProjectSubmittalReport projectSubmittalReport = new ProjectSubmittalReport();
    List<SubmittedProjectDetail> submittedProjectDetails = eTrackQueryReportDAO.retrieveSubmittedProjectDetails(
        userId, contextId, queryCriteria);
    BigDecimal totalProjects = new BigDecimal(0);
    if (!CollectionUtils.isEmpty(submittedProjectDetails)) {
      for (SubmittedProjectDetail submittedProjectDetail : submittedProjectDetails) {
        if (submittedProjectDetail.getMailInInd().equals(0)) {
          projectSubmittalReport.setPaperProjects(submittedProjectDetail.getTotal());
        } else if (submittedProjectDetail.getMailInInd().equals(1)) {
          projectSubmittalReport.setEmailedProjects(submittedProjectDetail.getTotal());
        }
        totalProjects = totalProjects.add(submittedProjectDetail.getTotal());
      }
      projectSubmittalReport.setTotalProjects(totalProjects);
      
      if (projectSubmittalReport.getPaperProjects() != null 
          &&  projectSubmittalReport.getPaperProjects().longValue() > 0) {
        projectSubmittalReport.setPercentageOfPaperProjects(
            projectSubmittalReport.getPaperProjects().divide(totalProjects, 2, RoundingMode.HALF_UP).multiply(ONE_HUNDRED)); 
      }
      if (projectSubmittalReport.getEmailedProjects() != null 
          && projectSubmittalReport.getEmailedProjects().longValue() > 0) {
        
        projectSubmittalReport.setPercentageOfEmailedProjects(
            projectSubmittalReport.getEmailedProjects().divide(totalProjects, 2, RoundingMode.HALF_UP).multiply(ONE_HUNDRED)); 
      }
    }
    logger.info("Exiting from retrieveProjectSubmittalDetails. User Id {}, Context Id {}", userId, contextId);
    return projectSubmittalReport;
  }

  @Override
  public Object retrieveCandidateKeywordDetailsReport(String userId, String contextId) {
    List<CandidateKeywordDetail> candidateKeywordDetails = eTrackQueryReportRepo.retrieveCandidateKeywordDetails();
    if (!CollectionUtils.isEmpty(candidateKeywordDetails)) {
      Map<String, Map<Integer, Integer>> candidateKeywordWithRegionMap = new HashMap<>();
      candidateKeywordDetails.forEach(candidateKeywordDetail -> {
        Map<Integer, Integer> regionAndCandidateKeywordsCount = new HashMap<>();
        regionAndCandidateKeywordsCount.put(candidateKeywordDetail.getRegion(), candidateKeywordDetail.getCount());
        if (CollectionUtils.isEmpty(candidateKeywordWithRegionMap.get(candidateKeywordDetail.getKeywordText()))) {
          candidateKeywordWithRegionMap.put(candidateKeywordDetail.getKeywordText(), regionAndCandidateKeywordsCount);
        } else {
          candidateKeywordWithRegionMap.get(candidateKeywordDetail.getKeywordText()).put(
              candidateKeywordDetail.getRegion(), candidateKeywordDetail.getCount());
        }
      });
      
      List<CandidateKeywordReport> candidateKeywordReports = new ArrayList<>();
      candidateKeywordWithRegionMap.keySet().forEach(keywordText -> {
        CandidateKeywordReport candidateKeywordReport = new CandidateKeywordReport();
        candidateKeywordReport.setKeyword(keywordText);
        Integer totalUsage = 0;
        for (Integer count : candidateKeywordWithRegionMap.get(keywordText).values()) {
          totalUsage += count;
        }
        candidateKeywordReport.setTotalUsage(totalUsage);
        LinkedHashMap<Integer, Integer> numberOfKeywordsInRegion = new LinkedHashMap<>();
        for (int region = 0; region < 10 ; region++) {
          numberOfKeywordsInRegion.put(region, candidateKeywordWithRegionMap.get(keywordText).get(region));
        }
        candidateKeywordReport.setRegions(numberOfKeywordsInRegion);
        candidateKeywordReports.add(candidateKeywordReport);
      });
      return candidateKeywordReports;
    } else {
      return new ArrayList<>();
    }
  }
}
