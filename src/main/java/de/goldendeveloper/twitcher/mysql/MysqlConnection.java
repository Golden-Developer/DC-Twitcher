package de.goldendeveloper.twitcher.mysql;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.mysql.errors.ExceptionHandler;
import de.goldendeveloper.twitcher.Main;

import java.util.ArrayList;
import java.util.List;

public class MysqlConnection {

    private final MYSQL mysql;

    public static String dbName = "GD-Twitcher";
    public static String tableName = "Twitcher";

    public static String colmDcServer = "DiscordServer";
    public static String colmDcStreamNotifyChannel = "DcStreamNotifyChannelID";
    public static String colmDcStreamNotifyRole = "DcStreamNotifyRoleID";
    public static String colmTwitchChannel = "TwitchChannel";

    public MysqlConnection(String hostname, int port, String username, String password) {
        mysql = new MYSQL(hostname, username, password, port, new ExceptionHandler());
        if (!mysql.existsDatabase(dbName)) {
            mysql.createDatabase(dbName);
        }
        Database db = mysql.getDatabase(dbName);
        if (!db.existsTable(tableName)) {
            db.createTable(tableName);
        }
        Table table = db.getTable(tableName);
        List<String> l = new ArrayList<>();
        l.add(colmDcStreamNotifyChannel);
        l.add(colmDcServer);
        l.add(colmDcStreamNotifyRole);
        l.add(colmTwitchChannel);
        l.forEach(column -> {
            if (!table.existsColumn(column)) {
                table.addColumn(column);
            }
        });
        System.out.println("[" + Main.getConfig().getProjektName() +  "] Mysql Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
