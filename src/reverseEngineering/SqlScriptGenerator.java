package reverseEngineering;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import exceptions.ConfigurationException;
import utils.FileHelper;

public class SqlScriptGenerator {

	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				throw new Exception("Le programme attend deux arguments : le fichier de configuration et le nom de fichier dans lequel vous souhaitez écrire votre script");
			}
			
			Properties properties = configReader(new File(args[0]));
			
			String url = properties.getProperty("url");
			
			Class.forName(properties.getProperty("driver"));
			
			Connection connection = DriverManager.getConnection(url, properties);
			DatabaseMetaData metaData = connection.getMetaData();
			
			Set<Table> db = fetchDatabaseArchitecture(metaData);
			
			writeSQLScript(db, args[1]);
			
			connection.close();
			
			System.out.println("Script successfuly generated on " + args[1]);
		} catch (SQLException e) {
			System.err.println("Veuillez vous assurer que le fichier db.properties est configuré de manière à fonctionner avec votre base de données");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static Properties configReader(final File propertyFile) throws IOException, ConfigurationException {
		Properties configuration = new Properties();
		FileHelper.checkFileConfiguration(propertyFile);
		configuration.load(new FileInputStream(propertyFile));
		return configuration;
	}
	
	protected static Set<Table> fetchDatabaseArchitecture(DatabaseMetaData metaData) throws SQLException {
		Set<Table> db = new HashSet<>();
		
		ResultSet tableRs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
		
		while (tableRs.next()) {
			String tableName = tableRs.getString("TABLE_NAME");
			Table table = new Table(tableName);
			
			ResultSet columnRs = metaData.getColumns(null, null, tableName, "%");
			ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName);
			ResultSet fkRs = metaData.getImportedKeys(null, null, tableName);
			
			while (columnRs.next()) {
				Column column = new Column(columnRs.getString("COLUMN_NAME"));
				if(columnRs.getString("TYPE_NAME").equals("CHAR") || columnRs.getString("TYPE_NAME").equals("BINARY") || columnRs.getString("TYPE_NAME").equals("VARCHAR") || columnRs.getString("TYPE_NAME").equals("VARBINARY")) {
					column.setType(columnRs.getString("TYPE_NAME") + "(" + columnRs.getString("COLUMN_SIZE") + ")");
				} else if (columnRs.getString("TYPE_NAME").equals("ENUM") || columnRs.getString("TYPE_NAME").equals("SET")) {
					Statement describeStmt = metaData.getConnection().createStatement();
					ResultSet describeRs = describeStmt.executeQuery("DESCRIBE " + table.getName() + " " + column.getName() + ";");
					
					describeRs.next();
					
					column.setType(describeRs.getString(2));
				} else {
					column.setType(columnRs.getString("TYPE_NAME"));
				}
				column.setIndex(columnRs.getInt("ORDINAL_POSITION"));
				column.setNullable(columnRs.getString("IS_NULLABLE").equals("YES") ? true : false);
				
				table.addColumn(column);
			}
			
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
		
		return db;
	}
	
	protected static void writeSQLScript(Set<Table> db, String outputFileName) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(outputFileName))){
			StringBuilder foreignKeysScript = new StringBuilder("");
	    	for (Table table : db) {
				StringBuilder tableScript = new StringBuilder("CREATE TABLE ");
				tableScript .append(table.getName())
							.append(" (");
				
				for (Column column : table.getColumns()) {
					tableScript .append("\n")
								.append(column.getName())
								.append(" ")
								.append(column.getType());
					
					if (column.isUnique()) {
						tableScript .append(" UNIQUE");
					}
					
					if (!column.isNullable()) {
						tableScript .append(" NOT NULL");
					}
					tableScript.append(",");
					
					if (column.isFk()) {
						foreignKeysScript.append("ALTER TABLE ")
										 .append(table.getName())
										 .append(" ADD FOREIGN KEY (")
										 .append(column.getName())
										 .append(") REFERENCES ")
										 .append(column.getFkReference())
										 .append(";\n\n");
					}
				}
				
				Column primary = table.getPrimaryKey();
				
				if (primary != null) {
					tableScript .append("\nPRIMARY KEY (")
								.append(primary.getName())
								.append("),");
				}
				
				tableScript.delete(tableScript.length() - 1, tableScript.length());
				tableScript.append("\n);\n");
				
				pw.println(tableScript.toString());
				pw.flush();
			}
	    	
	    	pw.print(foreignKeysScript);
	    	pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
