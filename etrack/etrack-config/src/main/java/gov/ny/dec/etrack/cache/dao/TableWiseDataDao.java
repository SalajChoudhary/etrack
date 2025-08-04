package gov.ny.dec.etrack.cache.dao;

import java.util.List;

import gov.ny.dec.etrack.cache.model.UrlValues;

public interface TableWiseDataDao {

	public List<UrlValues> getSelectedTableData(String tableName);
}
