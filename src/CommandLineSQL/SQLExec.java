package CommandLineSQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import exceptions.ConfigurationException;
import utils.FileHelper;

public class SQLExec {
	
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				throw new Exception("Le programme attend deux argument: le fichier de configuration et la requète que vous souhaitez executer");
			}
			
			Properties properties = configReader(new File(args[0]));
			String query = args[1];
			
			String url = properties.getProperty("url");
			
			Class.forName(properties.getProperty("driver"));
			
			Connection connection = DriverManager.getConnection(url, properties);
			Statement statement = connection.createStatement();
			System.out.println(query);
			if(query.toUpperCase().startsWith("SELECT")) {
				ResultSet result = statement.executeQuery(query);
				displayResult(result);
			} else {
				System.out.println(statement.executeUpdate(query) + " row(s) updated");
			}
			
		} catch (SQLException e) {
			System.err.println("Veuillez vous assurer que la syntaxe de votre requète est correcte et que le fichier db.properties est configuré de manière à fonctionner avec votre base de données");
			e.printStackTrace();
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
	
	protected static void displayResult(ResultSet result) throws SQLException {
		ResultSetMetaData metaData = result.getMetaData();
		int columnCount = metaData.getColumnCount();
		
		for (int i = 1 ; i <= columnCount ; i++) {
			System.out.print("|" + metaData.getColumnName(i));
		}
		System.out.println("|");
		System.out.println("———————————————————————————————————————————————————————");
		
		while (result.next()) {
			for (int i = 1 ; i <= columnCount ; i++) {
				System.out.print("|" + result.getString(i));
			}
			System.out.println("|");
		}
	}
}
