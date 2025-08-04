package dec.ny.gov.etrack.dcs.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentNameView implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long documentId;
  private String refDisplayName;
//  private Long refSupportDocRefId;
  private Integer documentTitleId;
  private String displayName;
  private Long supportDocRefId;
  private String referenceText;
  private Integer docCategory;
  private Integer docSubCategory;
  private List<DocumentFileView> files;
}
