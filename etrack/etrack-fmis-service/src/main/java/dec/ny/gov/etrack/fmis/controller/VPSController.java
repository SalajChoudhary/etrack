package dec.ny.gov.etrack.fmis.controller;

import java.util.Arrays;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import dec.ny.gov.etrack.fmis.exception.BadRequestException;
import dec.ny.gov.etrack.fmis.model.OAuthToken;
import dec.ny.gov.etrack.fmis.model.VPSAcknowledgement;
import dec.ny.gov.etrack.fmis.model.VPSResponse;
import dec.ny.gov.etrack.fmis.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/vps")
public class VPSController {

  private static Logger logger = LoggerFactory.getLogger(VPSController.class.getName());

  @Autowired
  private PaymentService paymentService;
  @Autowired
  private RestTemplate akanaOAuthRestTemplate;
  @Value("${okta.oauth.client.id}")
  private String akanaOAuthClientId;
  @Value("${okta.oauth.client.secret}")
  private String akanaOAuthClientSecret;
  
  /**
   * Generate the new transaction id by invoking VPS system for the input project Id and invoice number.
   * 
   * @param userId - User who initiates this request
   * @param invoiceNumber - Invoice number to create a transaction id.
   * @param projectId - Project id associated with this invoice number.
   * 
   * @return - Transaction details.
   */
  @PostMapping("/transaction")
  @ApiOperation(value="Request VPS system to provide the transaction id for the real time payment request. "
      + "This transaction id will be passed when the portal redirect the user to VPS system to make real time payment.")
  public Object generateTransactionId(
      @RequestHeader @ApiParam(example = "shortname", value="User id of the logged in user") final String userId,
      @RequestHeader @ApiParam(example = "123213", value="FMIS Invoice number") final String invoiceNumber, 
      @RequestHeader @ApiParam(example = "123213", value="Project Id") final Long projectId) {

    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into Transaction Id request method User Id {}, Context Id {} ", userId,
        contextId);
    if (StringUtils.isEmpty(invoiceNumber) || projectId == null || projectId <= 0) {
      throw new BadRequestException("INVOICE_PROJECT_ID_MISSING", "Invoice number or Project id is missing or blank.");
    }
    ResponseEntity<OAuthToken> oauthTokenResponseEntity = retrieveAkanaOAuthToken();
    if (HttpStatus.OK.equals(oauthTokenResponseEntity.getStatusCode())) {
      Object response =
          paymentService.requestTransactionId(userId, contextId, projectId, invoiceNumber, oauthTokenResponseEntity.getBody());
      logger.info("Exiting from Transaction Id request method User Id {}, Context Id {} ", userId,
          contextId);
      return response;
    } else {
      return new ResponseEntity<>(oauthTokenResponseEntity.getStatusCode());
    }
  }

  /**
   * End point will be invoked by VPS system once the payment process is completed by the user in the VPS web page.
   * This can be either successful or failure too.
   * 
   * @param receipt - VPS response details.
   * 
   * @return - Status and acknowledge details share with VPS as an receipt.
   */
  @PostMapping(value = "/receipt",
      consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value="This end point will be called by the VPS system once the payment is made successful by the user. "
      + "System will update the payment receipt details and share the acknowleged details to VPS system.")
  public VPSAcknowledgement paymentReceipt(@RequestBody VPSResponse receipt) {
    String contextId = UUID.randomUUID().toString();
    logger.info("Entering into payment Receipt method Context Id {} ", contextId);
    logger.debug("VPS Confirmation response body {}, Context Id {} ", receipt, contextId);

    String invoiceNum = receipt.getRequestID();
    String transactionId = receipt.getTransactionID();
    String confirmationNumber = receipt.getConfirmationNumber();
    VPSAcknowledgement vpsAcknowledgement = new VPSAcknowledgement();
    vpsAcknowledgement.setTransactionId(receipt.getTransactionID());
    if (StringUtils.isEmpty(transactionId) || StringUtils.isEmpty(invoiceNum)
        || StringUtils.isEmpty(confirmationNumber)) {
      logger.error("Invoice response contains one fo the field (transaction Id {}, Invoice number {} , Or Confirmation number {})"
          + " is blank Transaction Id {}, Context id {}", transactionId, contextId);
    } else {
      paymentService.updateConfirmationNumber(contextId, receipt);
      vpsAcknowledgement.setAcknowledgeInd(1);
      logger.info("Existing from payment Receipt method Context Id {} ", contextId);
    }
    return vpsAcknowledgement;

  }

  private ResponseEntity<OAuthToken> retrieveAkanaOAuthToken() {
    logger.info("Entering into retrieve Akana OAuth token");
    String credentials = akanaOAuthClientId+":"+akanaOAuthClientSecret;
    String encodedCredential = new String(Base64.encodeBase64(credentials.getBytes()));
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredential);
    MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
    requestEntity.add("grant_type", "client_credentials");
    requestEntity.add("scope", "ITS");
    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(requestEntity, headers);
    String url = UriComponentsBuilder.fromUriString("/token").toUriString();
    ResponseEntity<OAuthToken> responseEntity =
        akanaOAuthRestTemplate.exchange(url, HttpMethod.POST, request, OAuthToken.class);
    logger.info("Existing from retrieve Akana OAuth token");
    return responseEntity;
  }
}
