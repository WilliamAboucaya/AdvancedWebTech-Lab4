package reverseEngineering;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
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
			
			Class.forName(properties.getProperty("driver"));
			
			Set<Table> db = fetchDatabaseArchitecture(properties);
			
			writeSQLScript(db, args[1]);
			
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
	
	protected static Set<Table> fetchDatabaseArchitecture(Properties properties) throws SQLException {
		Set<Table> db = new HashSet<>();
		String url = properties.getProperty("url");
		
		Connection connection = DriverManager.getConnection(url, properties);
		DatabaseMetaData metaData = connection.getMetaData();
		
		TableFactory.createTablesFromMetadata(metaData, db);
		connection.close();
		
		return db;
	}
	
	protected static void writeSQLScript(Set<Table> db, String outputFileName) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(outputFileName))){
			StringBuilder foreignKeysScript = new StringBuilder("");
	    	for (Table table : db) {
	    		foreignKeysScript.append(table.toSqlFKScript());
	    		
				pw.println(table.toTableSqlString());
				pw.flush();
			}
	    	
	    	pw.print(foreignKeysScript.toString());
	    	pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
