package de._coho04_.streamnotifyer.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.*;
import com.github.twitch4j.helix.domain.SubscriptionEvent;
import com.github.twitch4j.pubsub.events.ChannelPointsRedemptionEvent;
import de._coho04_.streamnotifyer.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class TwitchEventHandler {

    @EventSubscriber
    public void onChannelGoLive(ChannelGoLiveEvent e) {
        TextChannel channel = Main.getDiscord().getBot().getTextChannelById(DcID.loTS);
        if (channel != null) {
            Guild guild = Main.getDiscord().getBot().getGuildById(DcID.DcS);
            if (guild != null) {
                Role role = guild.getRoleById(DcID.StreamNotify);
                if (role != null) {
                    String LiveTitel = e.getStream().getTitle();
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor("_Coho04_ ist nun live auf Twitch!", "https://twitch.tv/coho04_", "https://cdn.discordapp.com/avatars/513306244371447828/b78a6a320298d2e068f1859d05036cfe.png")
                            .setColor(Main.getDiscord().getEmbedColor())
                            .setTitle(LiveTitel, "https://www.twitch.tv/coho04_")
                            .setImage("https://static-cdn.jtvnw.net/previews-ttv/live_user_coho04_-1920x1080.png")
                            .setDescription("Spielt nun " + e.getStream().getGameName() + " für " + e.getStream().getViewerCount() + " Zuschauern! \n" +
                                    "[Schau vorbei](https://twitch.tv/coho04_)")
                            .setTimestamp(DiscordBot.date)
                            .setFooter("@_Coho04_")
                            .build();
                    channel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!").setEmbeds(embed).queue();
                }
            }
        }

        if (e.getStream().getGameName().equalsIgnoreCase("Just Chatting")) {
            Main.getTwitter().getClient().postTweet("Ich bin nun Live Auf Twitch schau gerne vorbei \n" +
                    "Wir reden über Zeuges ;-) \n" +
                    "https://twitch.tv/" + e.getChannel().getName());
        } else {
            Main.getTwitter().getClient().postTweet("Ich bin nun Live Auf Twitch schau gerne vorbei \n" +
                    "Es Wird gespielt: " + e.getStream().getGameName() + "\n" +
                    "https://twitch.tv/" + e.getChannel().getName());
        }

        Client cl = Main.getTeamSpeak().getBot().getClientByUId(TsID._Coho04_ID);
        if (cl != null) {
            if (cl.getChannelId() != 16) {
                Main.getTeamSpeak().getBot().moveClient(cl.getId(), 16);
            }
        }

        String[] StreamTitel = e.getStream().getTitle().replace(",", "").split(" ");
        for (int b = 0; b < StreamTitel.length; b++) {
            if (Listen.Mates.containsKey(StreamTitel[b].toLowerCase())) {
                String Mate = Listen.Mates.get(StreamTitel[b].toLowerCase());
                Client client = Main.getTeamSpeak().getBot().getClientByUId(Mate);
                if (client.getChannelId() != 16) {
                    Main.getTeamSpeak().getBot().moveClient(client.getId(), 16);
                }
            }
        }

        if (e.getChannel().getName().equalsIgnoreCase("coho04_")) {
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
                Main.getTeamSpeak().getBot().sendPrivateMessage(client.getId(), "_Coho04_ Ist nun Live auf Twitch![https://twitch.tv/coho04_]");
            }
        }
    }

    @EventSubscriber
    public void onChannelGoOffline(ChannelGoOfflineEvent e) {
        for (Client c : Main.getTeamSpeak().getBot().getClients()) {
            Channel onAir = Main.getTeamSpeak().getBot().getChannelByNameExact("╚- On - Air", true);
            if (!onAir.isEmpty()) {
                if (c.getChannelId() == 16) {
                    Main.getTeamSpeak().getBot().moveClient(c.getId(), 12);
                }
            }
        }
        if (e.getChannel().getName().equalsIgnoreCase("coho04_")) {
            if (!Main.getTeamSpeak().getBot().getChannelInfo(20).getName().equalsIgnoreCase(TsID.StreamStatusRoom + " Offline")) {
                Main.getTeamSpeak().getBot().editChannel(20, ChannelProperty.CHANNEL_NAME, TsID.StreamStatusRoom + " Offline");
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(21).getName().equalsIgnoreCase(TsID.GameRoom + " -")) {
                Main.getTeamSpeak().getBot().editChannel(21, ChannelProperty.CHANNEL_NAME, TsID.GameRoom + " -");
            }
            if (!Main.getTeamSpeak().getBot().getChannelInfo(22).getName().equalsIgnoreCase(TsID.ZuschauerCounterRoom + " -")) {
                Main.getTeamSpeak().getBot().editChannel(22, ChannelProperty.CHANNEL_NAME, TsID.ZuschauerCounterRoom + " -");
            }
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
        if (Main.getTeamSpeak().getBot().getChannelInfo(22).getName().equalsIgnoreCase(TsID.ZuschauerCounterRoom + e.getViewerCount())) {
            Main.getTeamSpeak().getBot().editChannel(22, ChannelProperty.CHANNEL_NAME, TsID.ZuschauerCounterRoom + e.getViewerCount());
        }
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
        if (e.getRedemption().getReward().getTitle().equals("Change the Color")) {
            System.out.println("SI");
        }
    }

    @EventSubscriber
    public static void onChannelFollowCountUpdate(ChannelFollowCountUpdateEvent e) {
        if (!Main.getTeamSpeak().getBot().getChannelInfo(80).getName().equals("[cspacer][Follower]: " + e.getFollowCount())) {
            Main.getTeamSpeak().getBot().editChannel(80, ChannelProperty.CHANNEL_NAME, "[cspacer][Follower]: " + e.getFollowCount());
        }
    }
}