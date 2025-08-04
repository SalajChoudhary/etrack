package dec.ny.gov.etrack.dart.db.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurgeArchive implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String docName;
	private String type;
	private String subType;
	private String projectId;
	private String decId;
	private String facilityName;
	private String municipality;
	private boolean markForReview;
	
	

}
