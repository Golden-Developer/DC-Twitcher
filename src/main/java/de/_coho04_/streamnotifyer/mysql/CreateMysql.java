package de._coho04_.streamnotifyer.mysql;

import de._Coho04_.mysql.MYSQL;
import de._Coho04_.mysql.entities.Database;
import de._Coho04_.mysql.entities.MysqlTypes;
import de._Coho04_.mysql.entities.Table;
import de._coho04_.streamnotifyer.Main;

import java.util.ArrayList;
import java.util.List;

public class CreateMysql {

    public static void createMYSQL(String hostname, int port, String username, String password) {
        Main.setMysql(new MYSQL(hostname, username, password, port));
        Main.getMysql().connect();
        if (!Main.getMysql().existsDatabase(Main.dbName)) {
            Main.getMysql().createDatabase(Main.dbName);
        }
        Database db = Main.getMysql().getDatabase(Main.dbName);
        if (!db.existsTable(Main.tableName)) {
            db.createTable(Main.tableName);
        }
        Table table = db.getTable(Main.tableName);

        List<String> l = new ArrayList<>();
        //Discord
        l.add("DcStreamNotifyChannelID");
        l.add("DcStreamNotifyRoleID");

        //TeamSpeak
        l.add("TsHostName");
        l.add("TsQueryPort");
        l.add("TsQueryUserName");
        l.add("TsQueryPassword");
        l.add("TsBotNickname");

        l.add("TsCountViewerChannelName");
        l.add("TsStreamInfoServerGruppeID");
        l.add("TsZuschauerCounterChannelName");
        l.add("TsStreamStatusChannelName");
        l.add("TsGameChannelName");
        l.add("TsCountViewerChannelID");
        l.add("TsBotServerGruppeID");
        l.add("TsZuschauerCounterChannelID");
        l.add("TsZuschauerCounterChannelID");
        l.add("TsGameChannelID");

        //Twitch
        l.add("TwitchChannel");

        for (String column : l) {
            if (!table.existsColumn(column)) {
                table.addColumn(column, MysqlTypes.VARCHAR, 80);
            }
        }
    }
}
