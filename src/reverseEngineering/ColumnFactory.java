package reverseEngineering;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ColumnFactory {

	public static void createColumnsFromMetadata(DatabaseMetaData metaData, Table table) throws SQLException {
		ResultSet columnRs = metaData.getColumns(null, null, table.getName(), "%");
		
		while (columnRs.next()) {
			Column column = new Column();
			column.setName(columnRs.getString("COLUMN_NAME"));
			
			
			switch(columnRs.getString("TYPE_NAME")) {
				case "CHAR":
				case "BINARY":
				case "VARCHAR":
				case "VARBINARY":
					column.setType(columnRs.getString("TYPE_NAME"));
					column.setSize(columnRs.getInt("COLUMN_SIZE"));
					break;
				
				case "ENUM":
				case "SET":
					Statement describeStmt = metaData.getConnection().createStatement();
					ResultSet describeRs = describeStmt.executeQuery("DESCRIBE " + table.getName() + " " + column.getName() + ";");
					describeRs.next();
					
					column.setType(describeRs.getString(2));
					break;
					
				case "DECIMAL":
					column.setType(columnRs.getString("TYPE_NAME"));
					column.setSize(columnRs.getInt("COLUMN_SIZE"));
					column.setDecimalDigits(columnRs.getInt("DECIMAL_DIGITS"));
					break;
				
				default:
					column.setType(columnRs.getString("TYPE_NAME"));
					break;
			}
			
			column.setIndex(columnRs.getInt("ORDINAL_POSITION"));
			column.setNullable(columnRs.getString("IS_NULLABLE").equals("YES") ? true : false);
			
			if(column.getType() != null && column.getName() != null) {
				table.addColumn(column);
			}
		}
	}
}
