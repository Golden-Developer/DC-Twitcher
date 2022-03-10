package de._coho04_.streamnotifyer.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import java.util.ArrayList;
import java.util.List;

public class Twitch {

    private TwitchClient twitchClient;
    private OAuth2Credential credential;
    private List<String> channels = new ArrayList<>();

    public Twitch() {
        credential = new OAuth2Credential("twitch", "x7yrxq8nigh7pevju2h40ozankitlm");
        try {
            twitchClient = TwitchClientBuilder.builder()
                    .withClientId("gp762nuuoqcoxypju8c569th9wz7q5")
                    .withClientSecret("p1052d47v1g7ob28c0ejlr3tahrkfv")
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
        //twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new TwitchEventHandler());
        //twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new ChatEvent());

        channels.add("Coho04_");
        channels.add("ReyOa");

        for (String channel : channels) {
           addChannel(channel);
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
