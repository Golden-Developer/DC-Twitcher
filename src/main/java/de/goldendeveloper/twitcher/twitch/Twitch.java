package de.goldendeveloper.twitcher.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.ID;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import de.goldendeveloper.twitcher.twitch.events.TwitchEventHandler;

import java.util.HashMap;

public class Twitch {

    private TwitchClient twitchClient;
    private OAuth2Credential credential;

    public Twitch() {
        credential = new OAuth2Credential("twitch", ID.credinal);
        try {
            twitchClient = TwitchClientBuilder.builder()
                    .withClientId(ID.TwitchClientID)
                    .withClientSecret(ID.TwitchClientSecret)
                    .withChatAccount(credential)
                    .withDefaultAuthToken(credential)
                    .withEnableChat(true)
                    .withEnableKraken(true)
                    .withEnableExtensions(true)
                    .withEnableTMI(true)
                    .withEnableHelix(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler());

        if (Main.getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(CreateMysql.colmDcServer)) {
                    for (Object obj : table.getColumn(CreateMysql.colmDcServer).getAll()) {
                        HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), obj.toString());
                        if (row.containsKey(CreateMysql.colmTwitchChannel) && row.containsKey(CreateMysql.colmDcStreamNotifyRole) && row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && row.containsKey(CreateMysql.colmTwitchChannel)) {
                            String TwChannel = row.get(CreateMysql.colmTwitchChannel).toString();
                            String DcChannel = row.get(CreateMysql.colmDcStreamNotifyChannel).toString();
                            String DcRole = row.get(CreateMysql.colmDcStreamNotifyRole).toString();
                            if (!TwChannel.isEmpty()) {
                                if (!DcChannel.isEmpty()) {
                                    if (!DcRole.isEmpty()) {
                                        addChannel(TwChannel);
                                    }
                                }
                            }
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
