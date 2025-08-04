package gov.ny.dec.etrack.cache.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.SequenceGenerator;
import javax.persistence.StoredProcedureParameter;
import lombok.Data;


@NamedStoredProcedureQuery(name = "getDocTypesAndSubTypes",
    procedureName = "CACHE_PKG.GET_DOCTYPE_SUBTYPE_P",
    resultClasses = {DocTypeSubType.class},
    parameters = {@StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class)})

@Entity
public @Data class DocTypeSubType implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "DOCUMENT_TYPE_ID")
  private Integer documentTypeId;

  @Column(name = "LANGUAGE_CODE")
  private String languageCode;

  @Column(name = "DOCUMENT_TYPE_DESC")
  private String documentTypeDesc;

  @Column(name = "DOCUMENT_SUB_TYPE_ID")
  private Integer documentSubTypeId;

  @Column(name = "DOC_SUB_TYPE_DESC")
  private String docSubTypeDesc;

  @Column(name = "DOCUMENT_CLASS_ID")
  private Integer documentClassId;

  @Column(name = "DOCUMENT_CLASS_NM")
  private String documentClassNm;
}
