package de._coho04_.streamnotifyer.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.*;
import com.github.twitch4j.helix.domain.SubscriptionEvent;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import de._coho04_.streamnotifyer.Main;

public class TwitchEventHandler {

    @EventSubscriber
    public void onChannelGoLive(ChannelGoLiveEvent e) {
   /*     TextChannel channel = Main.getDiscord().getBot().getTextChannelById(DcID.loTS);
        if (channel != null) {
            Guild guild = Main.getDiscord().getBot().getGuildById(DcID.DcS);
            if (guild != null) {
                Role role = guild.getRoleById(DcID.StreamNotify);
                if (role != null) {
                    String LiveTitel = e.getStream().getTitle();
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor(e.getChannel().getName() + " ist nun live auf Twitch!", "https://twitch.tv/" + e.getChannel().getName(), "https://cdn.discordapp.com/avatars/513306244371447828/b78a6a320298d2e068f1859d05036cfe.png")
                            .setColor(Main.getDiscord().getEmbedColor())
                            .setTitle(LiveTitel, "https://www.twitch.tv/" + e.getChannel().getName())
                            .setImage("https://static-cdn.jtvnw.net/previews-ttv/live_user_" + e.getChannel().getName() + "-1920x1080.png")
                            .setDescription("Spielt nun " + e.getStream().getGameName() + " für " + e.getStream().getViewerCount() + " Zuschauern! \n" +
                                    "[Schau vorbei](https://twitch.tv/" + e.getChannel().getName() + ")")
                            .setTimestamp(DiscordBot.date)
                            .setFooter("@_Coho04_")
                            .build();
                    channel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!").setEmbeds(embed).queue();
                }
            }
        }*/
/*        if (e.getChannel().getName().equalsIgnoreCase("coho04_")) {
            if (!Main.getTeamSpeak().getBot().getChannelInfo(20).getName().equalsIgnoreCase(TsID.StreamStatusRoom + " Online")) {
                Main.getTeamSpeak().getBot().editChannel(20, ChannelProperty.CHANNEL_NAME, TsID.StreamStatusRoom + " Online");
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(20).getName().equalsIgnoreCase(TsID.GameRoom + " " + e.getStream().getGameName())) {
                Main.getTeamSpeak().getBot().editChannel(21, ChannelProperty.CHANNEL_NAME, TsID.GameRoom + " " + e.getStream().getGameName());
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(20).getName().equalsIgnoreCase(TsID.ZuschauerCounterRoom + " " + e.getStream().getViewerCount())) {
                Main.getTeamSpeak().getBot().editChannel(22, ChannelProperty.CHANNEL_NAME, TsID.ZuschauerCounterRoom + " " + e.getStream().getViewerCount());
            }
        }
        for (Client client : Main.getTeamSpeak().getBot().getClients()) {
            if (!Listen.NoNotify.contains(client.getId())) {
                Main.getTeamSpeak().getBot().sendPrivateMessage(client.getId(), e.getChannel().getName() + " Ist nun Live auf Twitch![https://twitch.tv/" + e.getChannel().getName() + "]");
            }
        }*/
    }

    @EventSubscriber
    public void onChannelGoOffline(ChannelGoOfflineEvent e) {
        if (e.getChannel().getName().equalsIgnoreCase("coho04_")) {
      /*      if (!Main.getTeamSpeak().getBot().getChannelInfo(20).getName().equalsIgnoreCase(TsID.StreamStatusRoom + " Offline")) {
                Main.getTeamSpeak().getBot().editChannel(20, ChannelProperty.CHANNEL_NAME, TsID.StreamStatusRoom + " Offline");
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(21).getName().equalsIgnoreCase(TsID.GameRoom + " -")) {
                Main.getTeamSpeak().getBot().editChannel(21, ChannelProperty.CHANNEL_NAME, TsID.GameRoom + " -");
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(22).getName().equalsIgnoreCase(TsID.ZuschauerCounterRoom + " -")) {
                Main.getTeamSpeak().getBot().editChannel(22, ChannelProperty.CHANNEL_NAME, TsID.ZuschauerCounterRoom + " -");
            }*/
        }
    }

    @EventSubscriber
    public void onFollow(FollowEvent e) {
        String message = String.format("%s ist nun teil der Community %s!", e.getUser().getName(), e.getChannel().getName());
        e.getTwitchChat().sendMessage(e.getChannel().getName(), message);
    }

    @EventSubscriber
    public void onCheer(CheerEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage("coho04_", "Vielen dank, " + e.getUser().getName() + " für deinen Cheer mit " + e.getBits() + " Bits ! <3");
    }

    @EventSubscriber
    public void onSubscription(SubscriptionEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage("coho04_", "Vielen dank für deinen Abo " + e.getEventData().getUserName() + "! <3");
    }

    @EventSubscriber
    public void onGiftSubscription(GiftSubscriptionsEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage("coho04_", "Herzlichen Glückwunsch, " + e.getUser().getName() + " zu deinem Abo! Vielen Dank!<3");
    }

    @EventSubscriber
    public static void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent e) {
/*        if (Main.getTeamSpeak().getBot().getChannelInfo(22).getName().equalsIgnoreCase(TsID.ZuschauerCounterRoom + e.getViewerCount())) {
            Main.getTeamSpeak().getBot().editChannel(22, ChannelProperty.CHANNEL_NAME, TsID.ZuschauerCounterRoom + e.getViewerCount());
        }*/
    }

    @EventSubscriber
    public void onChannelChangeGame(ChannelChangeGameEvent e) {
        if (e.getChannel().getName().equalsIgnoreCase("coho04_")) {
            Main.getTeamSpeak().getBot().editChannel(21, ChannelProperty.CHANNEL_NAME, "[cspacer][Game]: " + e.getStream().getGameName());
        }
    }

    @EventSubscriber
    public void onDonation(DonationEvent event) {
        String message = String.format("%s hat gespendet %s, Vielen Dank! <3", event.getUser().getName(), event.getAmount());
        event.getTwitchChat().sendMessage(event.getChannel().getName(), message);
    }

    @EventSubscriber
    public void onChannelPointsRedemption(ChannelPointsRedemptionEvent e) {

    }

    @EventSubscriber
    public static void onChannelFollowCountUpdate(ChannelFollowCountUpdateEvent e) {
        if (!Main.getTeamSpeak().getBot().getChannelInfo(80).getName().equals("[cspacer][Follower]: " + e.getFollowCount())) {
            Main.getTeamSpeak().getBot().editChannel(80, ChannelProperty.CHANNEL_NAME, "[cspacer][Follower]: " + e.getFollowCount());
        }
    }
}