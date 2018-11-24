package reverseEngineering;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class TableFactory {

	public static void createTablesFromMetadata(DatabaseMetaData metaData, Set<Table> db) throws SQLException {
		ResultSet tableRs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
		
		while (tableRs.next()) {
			String tableName = tableRs.getString("TABLE_NAME");
			Table table = new Table(tableName);
			
			ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName);
			ResultSet fkRs = metaData.getImportedKeys(null, null, tableName);
			
			ColumnFactory.createColumnsFromMetadata(metaData, table);
			
			while (pkRs.next()) {
				table.getColumnByName(pkRs.getString("COLUMN_NAME")).setPk(true);
			}
			
			while (fkRs.next()) {
				Column column = table.getColumnByName(fkRs.getString("FKCOLUMN_NAME"));
				column.setFk(true);
				column.setFkReference(fkRs.getString("PKTABLE_NAME") + "(" + fkRs.getString("PKCOLUMN_NAME") + ")");
			}
			
			db.add(table);
		}
	}
}
