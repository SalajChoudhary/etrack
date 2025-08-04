package dec.ny.gov.etrack.dart.db.config;

import java.time.Duration;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DartDbServiceConfig {

  @Value("${spring.datasource.jndi-name}")
  private String jndiName;
  @Value("${etrack.facilities.package.name}")
  private String packageName;
  @Value("${etrack.dart.db.service.schema.name}")
  private String schemaName;
//  @Value("${etrack.populate.facility.proc.name}")
//  private String populateFacilityProc;
  @Value("${etrack.facility.proc.name}")
  private String eTrackFacilityInfoProc;
  @Value("${etrack.public.proc.name}")
  private String eTrackGetPublicProc;
  @Value("${etrack.decid.proc.name}")
  private String eTrackGetDECIdProc;
  @Value("${etrack.applicant.search.proc.name}")
  private String eTrackApplicantSearchProc;
  @Value("${etrack.org.search.proc.name}")
  private String eTrackOrgSearchProc;
  @Value("${etrack.dart.matched.facilities}")
  private String matchedFacilityProc;
  @Value("${etrack.dart.public.info.retrieval}")
  private String dartPublicInfoRetrieval;
  @Value("${etrack.dart.renewal.permit.retrieval}")
  private String dartExistingPermitsRetrievalProc;
  @Value("${etrack.dart.expired.permit.retrieval}")
  private String dartExpiredPermitsRetrievalProc;
  @Value("${etrack.dart.decid.by.taxmap.proc}")
  private String eTrackGetDECIdByTxMapProc;
  @Value("${etrack.dart.facility.info.proc}")
  private String eTrackFacilityProc;
  @Value("${etrack.dart.region.id.user.proc}")
  private String eTrackFetchRegionIdByUserProc;
  @Value("${etrack.dart.users.role.type.proc}")
  private String eTrackFetchUserRoleTypeIdProc;
  @Value("${etrack.dart.users.with.valid.email.proc}")
  private String eTrackFetchUserWithValidEmailProc;
  @Value("${etrack.dart.unissue.proc}")
  private String eTrackDartUnIssuedProc;
  
  @Value("${etrack.dart.due.apps.proc}")
  private String eTrackDartDueAppsProc;

  @Value("${etrack.dart.review.apps.proc}")
  private String eTrackDartReviewAppsProc;

//  @Value("${etrack.dart.retrieve.edbbin.proc}")
//  private String eTrackDartRetrieveEdbBinProc;
  
//  @Value("${etrack.dart.retrieve.milestone.proc}")
//  private String eTrackDartMilestoneProc;
  
  @Value("${etrack.get.review.details.proc}")
  private String eTrackGetReviewDetailsProc;
  
  @Value("${etrack.dart.aplct.due.apps.proc}")
  private String eTrackDartAplctDueAppsProc;
  
  @Value("${etrack.support.document.proc}")
  private String eTrackSupportDocumentProc;
  
  @Value("${etrack.out.for.review.apps.proc}")
  private String eTrackOutForReviewAppsProc;
  
  @Value("${etrack.get.staff.details.info}")
  private String eTrackStaffDetailsProc;

  @Value("${etrack.dart.suspended.apps.proc}")
  private String eTrackDartSuspendedAppsProc;

  @Value("${etrack.dart.dimsr.apps.proc}")
  private String eTrackDartDIMSRSupportDetailProc;

  @Value("${etrack.dart.narrative.desc.proc}")
  private String eTrackDartPermitNarrativeDescProc;
  @Value("${etrack.dart.disposted.apps.proc}")
  private String eTrackDartDisposedAppsProc;
  @Value("${etrack.permit.forms.proc}")
  private String eTrackPermitFormsProc;
  @Value("${etrack.get.emergency.auth.apps}")
  private String eTrackDartEmergencyAppsProc;
  @Value("${etrack.enterprise.vw.summary.proc}")
  private String enterpriseSupportDetailsProc;

  @Value("${etrack.archive.purge.package.name}")
  private String archivePurgePackageName;
  @Value("${etrack.purge.archive.result.proc}")
  private String purgeArchiveResultDetailsProcCall;
  @Value("${etrack.purge.archive.result.document.proc}")
  private String purgeArchiveResultDocumentsProcCall;

  @Value("${etrack.dart.search.attribute.data.name}")
  private String retriveAttributeDataNameProc;
  
  @Value("${etrack.dart.search.table.result}")
  private String retriveSearchResultsProc;
  
  @Value("${etrack.dart.search.package.name}")
  private String searchPackageName;


  
  @Bean
  public DataSource dataSource() throws NamingException {
    DataSource dataSource = null;
    Context context = new InitialContext();
    dataSource = (DataSource) context.lookup(jndiName);
    return dataSource;
  }

  @Bean(name = "dartStoredProcCall")
  public SimpleJdbcCall dartStoredProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  
//  @Bean(name = "populateFacilityProcCall")
//  public SimpleJdbcCall populateFacilityProc() throws NamingException {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
//    SimpleJdbcCall simpleJdbcCall =
//        new SimpleJdbcCall(jdbcTemplate).withProcedureName(populateFacilityProc)
//            .withSchemaName(schemaName).withCatalogName(packageName);
//    return simpleJdbcCall;
//  }

  @Bean(name = "eTrackFacilityInfoProc")
  public SimpleJdbcCall getETrackFacitlityInfoProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFacilityInfoProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackGetPublicInfoProc")
  public SimpleJdbcCall getGetPublicInfoProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackGetPublicProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackGetFacilityInfoProc")
  public SimpleJdbcCall getGetFacilityInfoProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFacilityProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackGetDECIdProc")
  public SimpleJdbcCall getDECIdByProgramTypeProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackGetDECIdProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackGetDECIdByTxMapCall")
  public SimpleJdbcCall getDECIdByTaxMapProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackGetDECIdByTxMapProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackApplicantSearchProc")
  public SimpleJdbcCall eTrackApplicantSearchProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackApplicantSearchProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackOrgSearchProc")
  public SimpleJdbcCall eTrackOrgSearchProc() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackOrgSearchProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "matchedFacilityAddress")
  public SimpleJdbcCall matchedFacilityAddress()
      throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    return new SimpleJdbcCall(jdbcTemplate).withSchemaName(
        schemaName).withCatalogName(packageName).withProcedureName(matchedFacilityProc);
  }
  
  @Bean(name = "getPublicInfoFromDart")
  public SimpleJdbcCall getPublicInfoFromDart() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(dartPublicInfoRetrieval)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "getExpiredPermitsFromDart")
  public SimpleJdbcCall getExpiredPermitsFromDart() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(dartExpiredPermitsRetrievalProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "getExistingPermitsFromDart")
  public SimpleJdbcCall getExistingPermitsFromDart() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(dartExistingPermitsRetrievalProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "getRegionIdByUserIdProcCall")
  public SimpleJdbcCall getRegionIdByUserIdProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFetchRegionIdByUserProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "getUsersByRoleTypeIdProcCall")
  public SimpleJdbcCall getUsersByRoleTypeIdProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFetchUserRoleTypeIdProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "getUsersWithValidEmailProcCall")
  public SimpleJdbcCall getUsersWithValidEmailProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackFetchUserWithValidEmailProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  
  @Bean(name = "eTrackDartUnIssuedProcCall")
  public SimpleJdbcCall eTrackDartUnIssuedProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartUnIssuedProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackDartDueAppsProcCall")
  public SimpleJdbcCall eTrackDartDueAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartDueAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackDartAplctDueAppsProcCall")
  public SimpleJdbcCall eTrackDartAplctDueAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartAplctDueAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackDartReviewAppsProcCall")
  public SimpleJdbcCall eTrackDartReviewAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartReviewAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

//  @Bean(name = "eTrackDartRetrieveEdbBinProcCall")
//  public SimpleJdbcCall eTrackDartRetrieveEdbBinProcCall() throws NamingException {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
//    SimpleJdbcCall simpleJdbcCall =
//        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartRetrieveEdbBinProc)
//            .withSchemaName(schemaName).withCatalogName(packageName);
//    return simpleJdbcCall;
//  }
  
//  @Bean(name = "eTrackDartMilestoneProcCall")
//  public SimpleJdbcCall eTrackDartMilestoneProcCall() throws NamingException {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
//    SimpleJdbcCall simpleJdbcCall =
//        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartMilestoneProc)
//            .withSchemaName(schemaName).withCatalogName(packageName);
//    return simpleJdbcCall;
//  }
  
  @Bean(name = "eTrackGetReviewDetailsProcCall")
  public SimpleJdbcCall eTrackGetReviewDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackGetReviewDetailsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackSupportDocumentProcCall")
  public SimpleJdbcCall eTrackSupportDocumentProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackSupportDocumentProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

//  @Bean(name = "eTrackSpatialInquiryDocumentProcCall")
//  public SimpleJdbcCall eTrackSpatialInquiryDocumentProcCall() throws NamingException {
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
//    SimpleJdbcCall simpleJdbcCall =
//        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackSpatialInquiryDocumentProc)
//            .withSchemaName(schemaName).withCatalogName(packageName);
//    return simpleJdbcCall;
//  }

  @Bean(name = "eTrackOutForReviewAppsProcCall")
  public SimpleJdbcCall eTrackOutForReviewAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackOutForReviewAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackStaffDetailsProcCall")
  public SimpleJdbcCall eTrackStaffDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackStaffDetailsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackDartSuspendedAppsProcCall")
  public SimpleJdbcCall eTrackDartSuspendedAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartSuspendedAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackDartDIMSRSupportDetailProcCall")
  public SimpleJdbcCall eTrackDartDIMSRSupportDetailProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartDIMSRSupportDetailProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackDartPermitNarrativeDescProcCall")
  public SimpleJdbcCall eTrackDartPermitNarrativeDescProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartPermitNarrativeDescProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackDartDisposedAppsProcCall")
  public SimpleJdbcCall eTrackDartDisposedAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartDisposedAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackPermitFormsProcCall")
  public SimpleJdbcCall eTrackPermitFormsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackPermitFormsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name = "eTrackDartEmergencyAppsProcCall")
  public SimpleJdbcCall eTrackDartEmergencyAppsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(eTrackDartEmergencyAppsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "eTrackOtherServiceRestTemplate")
  public RestTemplate eTrackOtherServiceRestTemplate(RestTemplateBuilder restTemplateBuilder,
      @Value("${etrack.other.service.uri}") String eTrackServiceURL,
      @Value("${etrack.connection.timeout}") String connectionTimeout,
      @Value("${etrack.read.timeout}") String readTimeout) {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(Long.valueOf(connectionTimeout)))
        .setReadTimeout(Duration.ofSeconds(Long.valueOf(readTimeout))).rootUri(eTrackServiceURL).build();
  }

  @Bean(name = "enterpriseSupportDetailsProcCall")
  public SimpleJdbcCall enterpriseSupportDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(enterpriseSupportDetailsProc)
            .withSchemaName(schemaName).withCatalogName(packageName);
    return simpleJdbcCall;
  }

  @Bean(name="submitProjectRetrievalTemplate")
  public JdbcTemplate submitProjectRetrievalTemplate() throws NamingException {
    return new JdbcTemplate(dataSource());
  }

  @Bean(name="namedParameterProjectRetrievalTemplate")
  public NamedParameterJdbcTemplate namedParameterProjectRetrievalTemplate() throws NamingException {
    return new NamedParameterJdbcTemplate(dataSource());
  }
  
  
  @Bean(name = "purgeArchiveResultDetailsProcCall")
  public SimpleJdbcCall purgeArchiveResultDetailsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(purgeArchiveResultDetailsProcCall)
            .withSchemaName(schemaName).withCatalogName(archivePurgePackageName);
    return simpleJdbcCall;
  }
  
  @Bean(name = "purgeArchiveResultDocumentsProcCall")
  public SimpleJdbcCall purgeArchiveResultDocumentsProcCall() throws NamingException {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
    SimpleJdbcCall simpleJdbcCall =
        new SimpleJdbcCall(jdbcTemplate).withProcedureName(purgeArchiveResultDocumentsProcCall)
            .withSchemaName(schemaName).withCatalogName(archivePurgePackageName);
    return simpleJdbcCall;
  }

  @Bean(name="eTrackAttributeDataNameProcCall")
  public SimpleJdbcCall eTrackAttributeDataNameProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(retriveAttributeDataNameProc)
    .withSchemaName(schemaName).withCatalogName(searchPackageName);
  }


  @Bean(name="retriveSearchResultsProcCall")
  public SimpleJdbcCall etrackRetriveSearchResultsProcCall() throws NamingException {
    return new SimpleJdbcCall(new JdbcTemplate(dataSource())).withProcedureName(retriveSearchResultsProc)
    .withSchemaName(schemaName).withCatalogName(searchPackageName);
  }


}
