package com.wxsdk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyManager {

	private static PropertyManager manager = null;

	private static Object managerLock = new Object();

	private static String propsName = "/wechat.properties";

	private Properties properties = null;

	private Object propertiesLock = new Object();

	private String resourceURI;

	/**
	 * Singleton access only.
	 */
	private PropertyManager(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	/**
	 * Returns a Jive property
	 * 
	 * @param name
	 *            the name of the property to return.
	 * @returns the property value specified by name.
	 */
	public static String getProperty(String name) {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		return manager.getProp(name);
	}
	/**
	 * 根据文件名获取文件
	 * @author san
	 * @param propsName
	 * @param name
	 * @return String
	 */
	public static String getProperty(String propsName, String name) {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		return manager.getProp(name);
	}

	/**
	 * Sets a Jive property.
	 * 
	 * @param name
	 *            the name of the property being set.
	 * @param value
	 *            the value of the property being set.
	 */
	public static void setProperty(String name, String value) {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		manager.setProp(name, value);
	}

	/**
	 * Returns true if the properties are readable. This method is mainly
	 * valuable at setup time to ensure that the properties file is setup
	 * correctly.
	 */
	public static boolean propertyFileIsReadable() {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		return manager.propFileIsReadable();
	}

	/**
	 * Returns true if the properties are readable. This method is mainly
	 * valuable at setup time to ensure that the properties file is setup
	 * correctly.
	 */
	public boolean propFileIsReadable() {
		try {
			InputStream in = getClass().getResourceAsStream(resourceURI);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns true if the properties are writable. This method is mainly
	 * valuable at setup time to ensure that the properties file is setup
	 * correctly.
	 */
	public static boolean propertyFileIsWritable() {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		return manager.propFileIsWritable();
	}

	/**
	 * Returns true if the properties are writable. This method is mainly
	 * valuable at setup time to ensure that the properties file is setup
	 * correctly.
	 */
	public boolean propFileIsWritable() {
		String path = getProp("path");
		File file = new File(path);
		if (file.isFile()) {
			// See if we can write to the file
			if (file.canWrite()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Returns true if the jive.properties file exists where the path property
	 * purports that it does.
	 */
	public static boolean propertyFileExists() {
		if (manager == null) {
			synchronized (managerLock) {
				if (manager == null) {
					manager = new PropertyManager(propsName);
				}
			}
		}
		return manager.propFileExists();
	}

	/**
	 * Returns true if the jive.properties file exists where the path property
	 * purports that it does.
	 */
	public boolean propFileExists() {
		String path = getProp("path");
		File file = new File(path);
		if (file.isFile()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets a Jive property. Jive properties are stored in jive.properties. The
	 * properties file should be accesible from the classpath. Additionally, it
	 * should have a path field that gives the full path to where the file is
	 * located. Getting properties is a fast operation.
	 */
	public String getProp(String name) {
		if (properties == null) {
			synchronized (propertiesLock) {
				// Need an additional check
				if (properties == null) {
					loadProps();
				}
			}
		}
		return properties.getProperty(name);
	}

	/**
	 * Sets a Jive property. Because the properties must be saved to disk every
	 * time a property is set, property setting is relatively slow.
	 */
	public void setProp(String name, String value) {
		// Only one thread should be writing to the file system at once.
		synchronized (propertiesLock) {
			// Create the properties object if necessary.
			if (properties == null) {
				loadProps();
			}
			properties.setProperty(name, value);
			// Now, save the properties to disk. In order for this to work, the
			// user
			// needs to have set the path field in the properties file. Trim
			// the String to make sure there are no extra spaces.
			String path = properties.getProperty("path").trim();
			OutputStream out = null;
			try {
				out = new FileOutputStream(path);
				properties.store(out, "jive.properties -- "
						+ (new java.util.Date()));
			} catch (Exception ioe) {
				System.err
						.println("There was an error writing jive.properties to "
								+ path
								+ ". "
								+ "Ensure that the path exists and that the Jive process has permission "
								+ "to write to it -- " + ioe);
				ioe.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Loads Jive properties from the disk.
	 */
	private void loadProps() {
		properties = new Properties();
		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(resourceURI);
			properties.load(in);
		} catch (IOException ioe) {
			System.err.println("Error reading Pcc properties" + ioe);
			ioe.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(PropertyManager.getProperty("mail.content"));

	}

}
