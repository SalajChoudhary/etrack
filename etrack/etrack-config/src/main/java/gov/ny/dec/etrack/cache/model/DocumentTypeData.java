package gov.ny.dec.etrack.cache.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DocumentTypeData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String id;
	private Integer documentTypeId;
	private String docDescription;
	
	private Integer documentSubTypeId;
	private String docSubTypeDescription;
	
	private Integer docTitleId;
	private String docTitle;
	
	private Integer documentClassId;
	private Integer activeInd;
	private String availToDepOnlyInd;
	private String layerType;
	private Integer orderInd;
	private String permitTypeCode;
	private Integer spatialInqCategoryId;
	private String spatialInqCategoryCode;
	private String spatialInqCategoryDesc;
	private Integer displayOrder;
	private String categoryAvailTo;
	private Integer newMessageCode;
	private String messageCode;
	private String messageDesc;
	private Integer messageTypeId;
}
