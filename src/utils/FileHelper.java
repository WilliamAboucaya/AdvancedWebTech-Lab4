package utils;

import java.io.File;

import exceptions.ConfigurationException;

public class FileHelper {

	public static void checkFileConfiguration(final File file) throws ConfigurationException {
		if (!file.exists()) {
			throw new ConfigurationException(file + " does not exists");
		}
		if (!file.canRead()) {
			throw new ConfigurationException(file + " is not readable");

		}
	}
}
