package dec.ny.gov.etrack.dart.db.controller;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dec.ny.gov.etrack.dart.db.exception.BadRequestException;
import dec.ny.gov.etrack.dart.db.service.GenerateReportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/report")
public class ETrackReportGenerateController {

  private static Logger logger =
      LoggerFactory.getLogger(ETrackReportGenerateController.class.getName());

  @Autowired
  private GenerateReportService generateReportService;

  /**
   * Generate the Invoice report.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project id.
   * @param invoice - Invoice number.
   * 
   * @return - Invoice report.
   */
  @GetMapping(value = "/invoice-report/{invoice}", produces = "application/pdf")
  @ApiOperation(value="Generate the Invoice report.")
  public ResponseEntity<byte[]> generateInvoiceReportWithBeanCollection(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId,
      @PathVariable @ApiParam(example = "23498234", value="Invoice number") final String invoice) {

    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into Generating Invoice Report. Invoice Number {} User Id {}, Context Id {}",
        invoice, userId, contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJ_ID_EMPTY", "Project Id cannot be empty or zero",
          projectId);
    }
    byte[] report = generateReportService.retrieveInvoiceReport(userId, contextId, projectId, invoice);
    return new ResponseEntity<>(report, HttpStatus.OK);
  }
  
  /**
   * Generate the missing required documents report and share with the requested user.
   * 
   * @param userId - User who initiates this request.
   * @param contextId - Unique UUID to track this request.
   * @param projectId - Project Id.
   * 
   * @return - Missing Uploaded document report. 
   */
  @GetMapping(value = "/uploaded-document/{projectId}", produces = "application/pdf")
  @ApiOperation(value="Generate the list of documents missed to uploaded report.")
  public ResponseEntity<byte[]> generateUploadedDocumentsReportWithBeanCollection(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId, 
      @RequestHeader(required = false) @ApiParam(example="2af065da-9da3-11ee-8c90-0242ac120002", value="Unique UUID to track this request") String contextId,  
      @PathVariable @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    if (!StringUtils.hasLength(contextId)) {
      contextId = UUID.randomUUID().toString();
    }
    logger.info(
        "Entering into Uploaded Documents Report. Project Id {} User Id {}, Context Id {}",
        projectId, userId, contextId);
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJ_ID_EMPTY", "Project Id cannot be empty or zero",
          projectId);
    }
    byte[] report = generateReportService.generateDocumentsUploadedReport(userId, contextId, projectId);
    return new ResponseEntity<>(report, HttpStatus.OK);
  }
  
  /**
   * Generate the Permit Cover sheet report.
   * 
   * @param userId - User who initiates this request.
   * @param projectId - Project Id.
   * 
   * @return - Permit Cover sheet report.
   */
  @GetMapping(value="/permit-cover-sheet", produces = "application/pdf")
  @ApiOperation(value="Generate the Permit cover sheet report.")
  public ResponseEntity<Object> generatePermitCoversheetReport(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {
    
    String contextId = UUID.randomUUID().toString();
    logger.info(
        "Entering into Generating Permit Cover Sheet Report. Project Id {} User Id {}, Context Id {}",
        projectId, userId, contextId);
    
    if (projectId == null || projectId <= 0) {
      throw new BadRequestException("PROJ_ID_EMPTY", "Project Id cannot be empty or zero",
          projectId);
    }
    byte[] report = generateReportService.retrievePermitCoverSheetReport(userId, contextId, projectId);
    return new ResponseEntity<>(report, HttpStatus.OK);
  }
}
