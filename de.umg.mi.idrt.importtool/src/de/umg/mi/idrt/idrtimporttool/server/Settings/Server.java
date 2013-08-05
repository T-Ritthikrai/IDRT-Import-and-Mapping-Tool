package de.umg.mi.idrt.idrtimporttool.server.Settings;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.umi.mi.passwordcrypt.PasswordCrypt;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class Server implements Serializable {

	private static String ORACLEDRIVER = "oracle.jdbc.driver.OracleDriver";
	private static String MSSQLDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String MYSQLDRIVER = "com.mysql.jdbc.Driver";
	//con=
	private static String error;
	private static String[] comboItems = {"Oracle","MSSQL","MySQL"};
	private static final long serialVersionUID = 1L;
	private String uniqueID;
	private String ip;
	private String port;
	private String user;
	private String password;
	private String sid;
	private String schema;
	private String table;
	private String patients;
	private String concepts;
	private String projectName;
	private String databaseType;
	private boolean useWinAuth;

	public Server(String uniqueID, String ip, String port, String user,
			String passWord, String sid, String databaseType, boolean useWinAuth) {
		this.uniqueID = uniqueID;
		this.ip = ip;
		setPassword(passWord);
		this.port = port;
		this.user = user;
		this.sid = sid;
		this.databaseType = databaseType;
		this.setUseWinAuth(useWinAuth);
	}

	public Server(String uniqueID, String ip, String port, String user,
			String passWord, String sid, String databaseType,boolean useWinAuth, String schema) {
		this.uniqueID = uniqueID;
		this.ip = ip;
		setPassword(passWord);
		this.port = port;
		this.user = user;
		this.sid = sid;
		this.schema = schema;
		this.databaseType=databaseType;
		this.setUseWinAuth(useWinAuth);
	}

	public Server(String uniqueID, String ip, String port, String user,
			String passWord, String sid,String databaseType, String schema, boolean useWinAuth, String table) {
		this.uniqueID = uniqueID;
		this.ip = ip;
		setPassword(passWord);
		this.port = port;
		this.user = user;
		this.sid = sid;
		this.schema = schema;
		this.table = table;
		this.databaseType = databaseType;
		this.setUseWinAuth(useWinAuth);
	}
	/**
	 * @return Connection for JDBC
	 */
	public Connection getConnection() {
		try {
			//			System.out.println("DATABASETYPE: " + this.getDatabaseType());
			DriverManager.setLoginTimeout(2);
			if(this.getDatabaseType() == null) {
				//				System.out.println("DATABASE == NULL");
				Class.forName(ORACLEDRIVER);
				return DriverManager.getConnection("jdbc:oracle:thin:@" + this.getIp() + ":" + this.getPort() + ":"
						+ this.getSID(), this.getUser(), this.getPassword());
			}
			else if (this.getDatabaseType().equalsIgnoreCase("oracle")) {
				Class.forName(ORACLEDRIVER);
				return DriverManager.getConnection("jdbc:oracle:thin:@" + getIp() + ":" + getPort() + ":"
						+ getSID(), getUser(), getPassword());
			}
			else if (this.getDatabaseType().equalsIgnoreCase("mysql")) {
				Class.forName(MYSQLDRIVER);
				return DriverManager.getConnection("jdbc:mysql://" + getIp() + ":" + getPort() + "/"
						+ getSID(), getUser(), getPassword());	
			}
			else if (this.getDatabaseType().equalsIgnoreCase("mssql")) {
				Class.forName(MSSQLDRIVER);
				if (getPort().length()>0) {
					if (!isUseWinAuth()) {
						return DriverManager.getConnection("jdbc:sqlserver://" + getIp() + ":" 
								+ getPort(), getUser(), getPassword());
					}
					else {
						//						System.out.println("getSID: " + getSID());
						return DriverManager.getConnection("jdbc:sqlserver://"+getIp()+ ":" 
								+ getPort()+";databaseName="+getSID()+";integratedSecurity=true");

					}
				}
				else {
					if (!isUseWinAuth()) {
						return DriverManager.getConnection("jdbc:sqlserver://" + getIp(), getUser(), getPassword());
					}
					else {
						//						System.out.println("getSID: " + getSID());
						return DriverManager.getConnection("jdbc:sqlserver://"+getIp()
								+";databaseName="+this.getSID()+";integratedSecurity=true");

					}

				}
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			setError(e.getMessage());
			e.printStackTrace();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getConcepts(String user) {
		try {
			DriverManager.setLoginTimeout(2);
			Connection connect = getConnection();
			Statement statement = connect.createStatement();
			if (!user.toLowerCase().startsWith("i2b2")) {
				user = "i2b2" + user;
			}
			ResultSet resultSet = statement
					.executeQuery("select count(*) as concepts from " + user
							+ ".observation_fact");
			while (resultSet.next()) {
				concepts = resultSet.getString("concepts");
			}
			connect.close();
		} catch (SQLException e) {
			System.err.println("Not a valid i2b2 project: No Concepts found!");
			concepts = "";
			return concepts;
		}
		return concepts;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return uniqueID;
	}

	public String getPassword() {
		return PasswordCrypt.decrypt(password);
	}
	public void setPassword(String password) {
		this.password = PasswordCrypt.encrypt(password);
	}

	public String getProjectName(String project) {
		try {
			Connection connect = getConnection();
			DriverManager.setLoginTimeout(2);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement
					.executeQuery("select project_name from i2b2pm.pm_project_data where project_id='"
							+ project + "'");
			while (resultSet.next()) {
				projectName = resultSet.getString("project_name");
			}
			connect.close();
			return projectName;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Not a valid i2b2 project: No Patients found!");
			projectName = "";
			return projectName;
		}
	}

	public String getPatients(String user) {
		try {
			patients="0";
			Connection connect = getConnection();
			DriverManager.setLoginTimeout(2);
			Statement statement = connect.createStatement();
			if (!user.toLowerCase().startsWith("i2b2")) {
				user = "i2b2" + user;
			}
			ResultSet resultSet = statement
					.executeQuery("select count(*) as patients from " + user
							+ ".patient_dimension");
			while (resultSet.next()) {
				patients = resultSet.getString("patients");
			}
			connect.close();
		} catch (SQLException e) {
			//			e.printStackTrace();
			System.err.println("Not a valid i2b2 project: No Patients found!");
			patients = "";
			return patients;
		}
		return patients;
	}

	public String getPort() {
		return port;
	}

	public String getSchema() {
		return schema;
	}

	public String getSID() {
		return sid;
	}

	public String getTable() {
		return table;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public String getUser() {
		return user;
	}

	public void setConcecpts(String concecpts) {
		concepts = concecpts;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPatients(String patients) {
		this.patients = patients;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setSID(String sid) {
		this.sid = sid;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public boolean checkDatabaseType() {

		if (this.databaseType.equals("oracle")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return uniqueID + " " + ip;
	}

	/**
	 * @return
	 */
	public static String[] getComboItems() {
		return comboItems;
	}

	public static String getError() {
		return error;
	}

	public static void setError(String error) {
		Server.error = error;
	}

	public boolean isUseWinAuth() {
		return useWinAuth;
	}

	public void setUseWinAuth(boolean useWinAuth) {
		this.useWinAuth = useWinAuth;
	}
}