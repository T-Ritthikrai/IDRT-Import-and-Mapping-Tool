package de.umg.mi.idrt.idrtimporttool.server.Settings;

/**
 * @author Benjamin Baum <benjamin(dot)baum(at)med(dot)uni-goettingen(dot)de>
 *         Department of Medical Informatics Goettingen
 *         www.mi.med.uni-goettingen.de
 */
public class ServerTable {

	private String table;
	private String databaseUser;
	private Server server;
	private String databaseSchema;

	public ServerTable(Server server, String schema, String table) {
		this.table = table;
		this.databaseUser = schema;
		this.server = server;
		// TODO Auto-generated constructor stub
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getName() {
		return table;
	}

	public Server getServer() {
		return server;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	@Override
	public String toString() {
		return table;
	}
}
