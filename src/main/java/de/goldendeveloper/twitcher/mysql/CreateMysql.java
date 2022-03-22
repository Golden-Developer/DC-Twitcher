package de.goldendeveloper.twitcher.mysql;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.MysqlTypes;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;

import java.util.ArrayList;
import java.util.List;

public class CreateMysql {

    public static String colmDcServer = "DiscordServer";
    public static String colmDcStreamNotifyChannel = "DcStreamNotifyChannelID";
    public static String colmDcStreamNotifyRole = "DcStreamNotifyRoleID";
    public static String colmTwitchChannel = "TwitchChannel";

    public static void createMYSQL(String hostname, int port, String username, String password) {
        Main.setMysql(new MYSQL(hostname, username, password, port));
        if (!Main.getMysql().existsDatabase(Main.dbName)) {
            Main.getMysql().createDatabase(Main.dbName);
        }
        Database db = Main.getMysql().getDatabase(Main.dbName);
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
}
