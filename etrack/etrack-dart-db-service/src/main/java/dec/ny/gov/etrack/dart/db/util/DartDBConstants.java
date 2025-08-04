package dec.ny.gov.etrack.dart.db.util;

/**
 * 
 * @author mxmahali
 */
public interface DartDBConstants {
  String PUBLICS_CURSOR = "p_publics_cur";
  String PUBLIC_CURSOR = "p_public_cur";
  String PUBLIC_HIST_CURSOR = "p_public_hist_cur";
  String ADDR_HIST_CURSOR = "p_addr_hist_cur";
  String FACILITY_CURSOR = "p_facility_cur";
  String FACILITY_HIST_CURSOR = "p_facility_hist_cur";
  String PROJ_CURR_STATUS_CURSOR = "p_current_status_cur";
  String PROJ_MILESTONE_STATUS_CURSOR = "p_milestone_cur";
  String FACILITY_ADDR_HIST_CURSOR = "p_facility_addr_hist_cur";
  String STATUS_CODE = "p_status_cd";
  String STATUS_MESSAGE = "p_status_msg";
  String PROJECT_ID = "p_project_id";
  String DISTRICT_ID = "p_district_id";
  String USER_ID = "p_user_id";
  String MAIL_IN_IND = "p_mail_in_ind";
  String APPLICANT_TYPE = "p_applicant_type";
  String PUBLIC_ID = "p_public_id";
  String PROGRAM_TYPE = "p_program_id_type";
  String PROGRAM_ID = "p_program_id_value";
  String DEC_ID = "p_dec_id";
  String REGION_ID = "p_region_id";
  String FACILITY_NAME = "p_facility_name";
  String TX_MAP = "p_tax_map";
  String COUNTY = "p_county";
  String MUNICIPALITY = "p_mun";
  String P_REGION_USER_CURSOR = "p_region_user_cur";
  String P_EMAIL_USER_CURSOR = "p_email_user_cur";
  String P_PENDING_APPS_CURSOR = "p_unissue_apps_cur";
  String P_REVIEW_PROJ_CURSOR = "p_reviewer_detail_cur";
  String P_CURRENT_STATUS = "p_current_status";
  String P_DUE_APPS_CURSOR =  "p_due_apps_cur";
  String P_SUSPENDED_APPS_CURSOR = "p_suspended_apps_cur";
  String P_REVIEW_APPS_CURSOR =  "p_review_apps_cur";
  String P_EDB_BIN_CURSOR =  "p_bin_cur";
  String P_SUPPORT_DOC_CURSOR = "p_doc_cur";
  String P_OUT_FOR_REVIEW_CURSOR = "p_out_for_review_cur";
  String P_DISPOSED_APPS_CURSOR = "p_disposed_apps_cur";
  String P_EMERGENCY_AUTH_APPS_CURSOR = "p_ea_apps_cur";

  // Public Type codes
  String INDIVIDUAL = "I";
  String SOLE_PROPRIETOR = "P";
  String INCORPORATED_BIZ = "X";
  String TRUST_OR_ASSOCIATION = "T";
  String CORPN_PARTNER = "C";
  String FEDERAL_AGENCY = "F";
  String STATE_AGENCY = "S";
  String MUNI_OR_COUNTY = "M";

  String LAST_NAME_SEARCH = "p_ln";
  String FIRST_NAME_SEARCH = "p_fn";
  String LAST_NAME_SEARCH_PATTERN = "p_ln_search_pattern";
  String FIRST_NAME_SEARCH_PATTERN = "p_fn_search_pattern";
  String P_RESULT_CURSOR = "p_results_cur";
  String PUBLIC_NAME_SEARCH_PATTERN = "p_search_pattern";
  String PUBLIC_NAME_SEARCH = "p_name";
  String ADDRESS_LINE = "p_address_ln";
  String CITY = "p_city";
  String MATCHING_FACILITY_CURSOR = "p_fac_cur";
  String EXISTING_APPS_CURSOR = "p_existing_apps_cur";
  String EXPIRED_APPS_CURSOR = "p_reissue_apps_cur";
//  String EXISTING_APPS_EXTEND_DATE_CURSOR = "p_extended_date_cur";
  String APPS_NARRATIVE_DATA_CURSOR = "p_narrative_html_cur";
  String REQD_PERMIT_FORMS_CURSOR = "p_reqd_permit_form_cur";
  Integer VALIDATED = 1;
  Integer APPLICANT_VALIDATED=7;
  Integer OWNER_VALIDATED=8;
  Integer CONTACT_AGENT_VALIDATED=9;
  String VALIDATED_IND = "validatedInd";
  String SYSTEM_USER_ID = "SYSTEM";
  String ENTERPRISE_SYSTEM_USER_ID = "ENTERPRISE";
  String PAYMENT_PENDING = "1";
  String PAID = "2";
  String PAYMENT_CANCELLED = "3";
  
  Integer SEL_PROJ_LOC = 1;
  Integer APPLICANT_INFO = 2;
  Integer PROJECT_INFO = 3;
  Integer UPLOAD_DOC = 4;
  Integer SIGNATURE = 5;
  Integer LOC_DETAIL_VAL = 6;
  Integer APPLICANT_VAL = 7;
  Integer PROP_OWNER_VAL = 8;
  Integer PERMIT_SUMMARY_VAL = 10;
  Integer PROJ_DESC_VAL = 11;
  Integer ASSIGN_CONTACT_VAL = 12;
  Integer SUPPORT_DOC_VAL = 13;
  Integer SUBMIT_PROJ_VAL = 14;
  String ROLE_TYPE_ID = "p_role_type_id";
  String EMAIL_RECIEVED = "Received";
  String EMAIL_SENT = "Sent";
  String STAFF_DETAILS_CURSOR = "P_USER_DETAIL_CUR";
  Integer REQUIRED_DOCUMENTS_NOT_RECEIVED = 17;
  Integer INVOICE_CANCELLED_NOTE = 16;
  String SPATIAL_INQ_CATG_CODE = "p_si_cat_code";
  String SPATIAL_INQUIRY_ID = "p_inquiry_id";
  String SPATIAL_INQUIRY_STATUS = "inq_status";
  String SPATIAL_INQ_CATG_DOCUMENT_CURSOR = "p_reqd_spatial_inq_doc_cur";
  String SPATIAL_INQ_REVIEW_CURSOR = "p_doc_review_cur";
  String QUERY_TYPE_ID = "p_query_tyep_id";
  String ARC_PRG_CUR = "p_arc_prg_doc_cur";
  String P_ARC_PRG_CUR = "P_ARC_PRG_CUR";
  String P_RESULT_ID = "p_result_id";
  String APPLICATION_CURSOR = "p_application_cur";
  String SEARCH_RESULT_CURSOR = "p_results_cur";
  String QUERY_ID ="p_query_id";
  String ATTRIBUTE_CURSOR="p_att_cur";
  String ATTRIBUTE_ID="P_ATTR_ID";
  String LAST_LOAD_TIME= "p_last_load_date";
}
