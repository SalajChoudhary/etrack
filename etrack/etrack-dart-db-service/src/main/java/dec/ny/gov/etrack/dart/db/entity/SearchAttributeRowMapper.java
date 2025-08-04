package dec.ny.gov.etrack.dart.db.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

public class SearchAttributeRowMapper implements RowMapper<SearchModel> {

	@Override
	public SearchModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		SearchModel searchByAttributes = new SearchModel();
		searchByAttributes.setSearchEntityCode(rs.getInt("SEARCH_ENTITY_CODE"));
		searchByAttributes.setSearchEntityDesc(rs.getString("SEARCH_ENTITY_DESC"));
//		searchByAttributes.setSearchAttributeName(rs.getString("ATTRIBUTE_NAME"));
//		searchByAttributes.setSearchAttributeId(rs.getInt("SEARCH_ATTRIBUTE_ID"));
//		searchByAttributes.setATTRIBUTEDATATYPE(rs.getInt("ATTRIBUTE_DATA_TYPE"));
		//searchByAttributes.setAttributeDataName(rs.getString("attribute_data_name"));
		return searchByAttributes;
	}

}
