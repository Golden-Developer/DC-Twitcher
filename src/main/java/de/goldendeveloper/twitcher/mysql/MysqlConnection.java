package de.goldendeveloper.twitcher.mysql;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.MysqlTypes;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;

import java.util.ArrayList;
import java.util.List;

public class MysqlConnection {

    private final MYSQL mysql;

    public static String colmDcServer = "DiscordServer";
    public static String colmDcStreamNotifyChannel = "DcStreamNotifyChannelID";
    public static String colmDcStreamNotifyRole = "DcStreamNotifyRoleID";
    public static String colmTwitchChannel = "TwitchChannel";

    public MysqlConnection(String hostname, int port, String username, String password) {
        mysql = new MYSQL(hostname, username, password, port);
        if (!mysql.existsDatabase(Main.dbName)) {
            mysql.createDatabase(Main.dbName);
        }
        Database db = mysql.getDatabase(Main.dbName);
        if (!db.existsTable(Main.tableName)) {
            db.createTable(Main.tableName);
        }
        Table table = db.getTable(Main.tableName);
        List<String> l = new ArrayList<>();
        l.add(colmDcStreamNotifyChannel);
        l.add(colmDcServer);
        l.add(colmDcStreamNotifyRole);
        l.add(colmTwitchChannel);
        for (String column : l) {
            if (!table.existsColumn(column)) {
                table.addColumn(column, MysqlTypes.VARCHAR, 80);
            }
        }
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
