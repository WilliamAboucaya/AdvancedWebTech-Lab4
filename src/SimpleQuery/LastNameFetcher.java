package SimpleQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import exceptions.ConfigurationException;
import utils.FileHelper;

public class LastNameFetcher {

	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				throw new Exception("Le programme attend un argument: le fichier de configuration");
			}
			
			Properties properties = configReader(new File(args[0]));
			
			String url = properties.getProperty("url");
			
			Class.forName(properties.getProperty("driver"));
			
			Connection connection = DriverManager.getConnection(url, properties);
			Statement statement = connection.createStatement();
			
			ResultSet lastNames = statement.executeQuery("SELECT last_name FROM actor ;");
			
			while (lastNames.next()) {
				System.out.println(lastNames.getString("last_name"));
			}
			
			connection.close();
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
}
