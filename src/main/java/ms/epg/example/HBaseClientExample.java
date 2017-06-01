package ms.epg.example;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

/*
 * The example shows you how to create a table and write/read data. 
 * The example schema is formed as follows:
 *
 *  Table Name: hvac
 *  Row key: <BuildingID>-<RoomID>-<Event time>
 *  Column Family: cp: Product, ct: Temperature
 *  Column Qualifier <cp>: a: Product's age, id: Product ID.
 *  Column Qualifier <ct>: a: Actual temperature, t: Target temperature
 *
 *                                         +----------------+
 *                    row key              |   cp   |   ct  |
 *    +------------------------------------+----------------+
 *    | <BuildingID>-<RoomId>-<Event time> | a | id | a | t |
 *    +-----------------------------------------------------+
 */
public class HBaseClientExample {
    private final String ZOOKEEPER_QUORUM = "zk2-khbase.bodp3wmw03uebcky0gsybawyte.mx.internal.cloudapp.net,zk1-khbase.bodp3wmw03uebcky0gsybawyte.mx.internal.cloudapp.net,zk0-khbase.bodp3wmw03uebcky0gsybawyte.mx.internal.cloudapp.net";
    private final String COLUMN_FAMILY_CP = "cp";
    private final String COLUMN_FAMILY_CT = "ct";
    
    private Configuration config;
    private Connection connection;

    /*
     * Create a new table. If it is already existed, re-create a new one.
     */
    public void createTable(String tableName) throws IOException {
        try (Admin admin = connection.getAdmin()) {
            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
            table.addFamily(new HColumnDescriptor(COLUMN_FAMILY_CT).setCompressionType(Algorithm.NONE));
            table.addFamily(new HColumnDescriptor(COLUMN_FAMILY_CP).setCompressionType(Algorithm.NONE));
            if (admin.tableExists(table.getTableName())) {
                System.out.println("Table [" + table.getTableName().getNameAsString()
						+ "] is already existed. Deleting...");
                admin.disableTable(table.getTableName());
                admin.deleteTable(table.getTableName());
            }
            System.out.print("Creating new table... ");
            admin.createTable(table);
            System.out.println("Done.");
        }
    }

    public void setUp() throws IOException {
        config = HBaseConfiguration.create();
        config.set("zookeeper.znode.parent","/hbase-unsecure");
        config.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM);
        connection = ConnectionFactory.createConnection();
    }
	
    public void close() throws IOException {
		if(connection != null) connection.close();
    }

    // Write the given data to the table. Assuming the table is created in createTable().
    public void write(String tableName, String[][] data) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            for (int i = 0; i< data.length; i++) {
                Put buf = new Put(Bytes.toBytes(data[i][0]));
                buf.addColumn(Bytes.toBytes(COLUMN_FAMILY_CP), Bytes.toBytes("a"), Bytes.toBytes(data[i][1]));
                buf.addColumn(Bytes.toBytes(COLUMN_FAMILY_CP), Bytes.toBytes("id"), Bytes.toBytes(data[i][2]));
                buf.addColumn(Bytes.toBytes(COLUMN_FAMILY_CT), Bytes.toBytes("a"), Bytes.toBytes(data[i][3]));
                buf.addColumn(Bytes.toBytes(COLUMN_FAMILY_CT), Bytes.toBytes("t"), Bytes.toBytes(data[i][4]));
                table.put(buf);
            }
        }
    }

    // Scan all the data stored in specified table. 
    public void readAll(String tableName) throws IOException {
    	try (Table table = connection.getTable(TableName.valueOf(tableName));
            ResultScanner rs = table.getScanner(new Scan()) ) {
    		for(Result ret = rs.next(); ret != null; ret = rs.next()) {
    		    for (Cell cell : ret.listCells()) {
                    String rkey = Bytes.toString(ret.getRow());
    		        String cf = Bytes.toString(CellUtil.cloneFamily(cell));
    		        String qual = Bytes.toString(CellUtil.cloneQualifier(cell));
    		        String value = Bytes.toString(CellUtil.cloneValue(cell));
    		        System.out.println("Row key: " + rkey +
                                       ", Column Family: " + cf + 
                                       ", Qualifier: " + qual + 
                                       ", Value : " + value);
    		    } 
    		}
    	}
    }

    public static void main(String[] args) throws IOException {
        final String TABLE_NAME = "hvac";
        // define sample data
        final String[][] src = {
        { "B1234-32-1494133008", "9", "XE3025", "29", "25"},
        { "G4119-90-1494133009", "11", "RG8021", "32", "30"},
        { "E3039-21-1494133010", "14", "MU8367", "16", "23"}};
        
        HBaseClientExample hc = new HBaseClientExample();
        hc.setUp();
        hc.createTable(TABLE_NAME);
        hc.write(TABLE_NAME, src);
        hc.readAll(TABLE_NAME);
        hc.close();
    }
}
