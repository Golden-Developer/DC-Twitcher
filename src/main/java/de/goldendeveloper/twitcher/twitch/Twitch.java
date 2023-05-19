package de.goldendeveloper.twitcher.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import de.goldendeveloper.mysql.entities.SearchResult;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import de.goldendeveloper.twitcher.twitch.events.TwitchEventHandler;
import io.sentry.Sentry;

import java.util.HashMap;

public class Twitch {

    private TwitchClient twitchClient;

    public Twitch() {
        OAuth2Credential credential = new OAuth2Credential("twitch", Main.getConfig().getTwitchCredential());
        try {
            twitchClient = TwitchClientBuilder.builder()
                    .withClientId(Main.getConfig().getTwitchClientID())
                    .withClientSecret(Main.getConfig().getTwitchClientSecret())
                    .withChatAccount(credential)
                    .withDefaultAuthToken(credential)
                    .withEnableChat(true)
                    .withEnableKraken(true)
                    .withEnableExtensions(true)
                    .withEnableTMI(true)
                    .withEnableHelix(true)
                    .build();
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
        twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler());

        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName) && Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
            Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
            if (table.existsColumn(MysqlConnection.colmDcServer)) {
                for (String obj : table.getColumn(MysqlConnection.colmDcServer).getAll().getAsString()) {
                    HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), obj.toString()).getData();
                    if (row.containsKey(MysqlConnection.colmTwitchChannel) && row.containsKey(MysqlConnection.colmDcStreamNotifyRole) && row.containsKey(MysqlConnection.colmDcStreamNotifyChannel) && row.containsKey(MysqlConnection.colmTwitchChannel)) {
                        String twChannel = row.get(MysqlConnection.colmTwitchChannel).getAsString();
                        String dcChannel = row.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString();
                        String dcRole = row.get(MysqlConnection.colmDcStreamNotifyRole).getAsString();
                        if (!twChannel.isEmpty() && !dcChannel.isEmpty() && !dcRole.isEmpty()) {
                            addChannel(twChannel);
                        }
                    }
                }
            }
        }
    }

    public void addChannel(String channel) {
        twitchClient.getClientHelper().enableStreamEventListener(channel);
        if (!twitchClient.getChat().isChannelJoined(channel)) {
            twitchClient.getChat().joinChannel(channel);
        }
    }

    public TwitchClient getBot() {
        return twitchClient;
    }
}
