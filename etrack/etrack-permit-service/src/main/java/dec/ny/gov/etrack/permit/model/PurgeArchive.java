package dec.ny.gov.etrack.permit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurgeArchive {
	private Integer queryNameCode;
	private String resultSetName;
	private Integer regionId;
}
