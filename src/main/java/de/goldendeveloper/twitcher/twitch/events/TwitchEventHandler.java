package de.goldendeveloper.twitcher.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.*;
import com.github.twitch4j.helix.domain.SubscriptionEvent;
import de.goldendeveloper.mysql.entities.Row;
import de.goldendeveloper.mysql.entities.SearchResult;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.List;

public class TwitchEventHandler {

    @EventSubscriber
    public void onChannelGoLive(ChannelGoLiveEvent e) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(MysqlConnection.colmDcServer) && table.existsColumn(MysqlConnection.colmTwitchChannel)) {
                    HashMap<Channel, Role> notify = getMessageChannel(table, e.getChannel().getName());
                    for (Channel channel : notify.keySet()) {
                        Role role = notify.get(channel);
                        if (role != null) {
                            if (channel.getType().equals(ChannelType.TEXT)) {
                                TextChannel textChannel = Main.getDiscord().getBot().getTextChannelById(channel.getId());
                                if (textChannel != null) {
                                    textChannel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!")
                                            .setEmbeds(sendTwitchNotifyEmbed(e.getStream().getTitle(), e.getChannel().getName(), e.getStream().getGameName(), e.getStream().getViewerCount()))
                                            .queue();
                                }
                            } else if (channel.getType().equals(ChannelType.NEWS)) {
                                NewsChannel newsChannel = Main.getDiscord().getBot().getNewsChannelById(channel.getId());
                                if (newsChannel != null) {
                                    newsChannel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!")
                                            .setEmbeds(sendTwitchNotifyEmbed(e.getStream().getTitle(), e.getChannel().getName(), e.getStream().getGameName(), e.getStream().getViewerCount()))
                                            .queue();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static HashMap<Channel, Role> getMessageChannel(Table table, String TwitchName) {
        HashMap<Channel, Role> channels = new HashMap<>();
        for (Row row : table.getRows()) {
            HashMap<String, SearchResult> sr = row.get();
            if (sr.get(MysqlConnection.colmTwitchChannel).getAsString().equalsIgnoreCase(TwitchName)) {
                if (!sr.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString().equalsIgnoreCase("0")) {
                    Channel channel = Main.getDiscord().getBot().getGuildChannelById(sr.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString());
                    Role role = Main.getDiscord().getBot().getRoleById(sr.get(MysqlConnection.colmDcStreamNotifyRole).getAsString());
                    if (role != null) {
                        channels.put(channel, role);
                    }
                }
            }
        }
        return channels;
    }

    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent e) {
        if (e.getMessage().startsWith("!dc") || e.getMessage().startsWith("!discord")) {
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "Um auf meinen Discord zu Joinen klicke auf den Link: " + sendDiscordInvite(e.getChannel().getName()));
        }
    }

    @EventSubscriber
    public void onFollow(FollowEvent e) {
        e.getTwitchChat().sendMessage(e.getChannel().getName(), String.format("%s ist nun teil der Community %s!", e.getUser().getName(), e.getChannel().getName()));
    }

    @EventSubscriber
    public void onCheer(CheerEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage(e.getChannel().getName(), "Vielen dank, " + e.getUser().getName() + " für deinen Cheer mit " + e.getBits() + " Bits ! <3");
    }

    @EventSubscriber
    public void onSubscription(SubscriptionEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage(e.getEventData().getBroadcasterName(), "Vielen dank für deinen Abo " + e.getEventData().getUserName() + "! <3");
    }

    @EventSubscriber
    public void onGiftSubscription(GiftSubscriptionsEvent e) {
        Main.getTwitch().getBot().getChat().sendMessage(e.getChannel().getName(), "Herzlichen Glückwunsch, " + e.getUser().getName() + " zu deinem Abo! Vielen Dank!<3");
    }

    @EventSubscriber
    public void onDonation(DonationEvent e) {
        e.getTwitchChat().sendMessage(e.getChannel().getName(), String.format("%s hat gespendet %s, Vielen Dank! <3", e.getUser().getName(), e.getAmount()));
    }

    private String sendDiscordInvite(String channel) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
        if (table.getColumn(MysqlConnection.colmTwitchChannel).getAll().contains(channel)) {
            HashMap<String, SearchResult> row = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName).getRow(table.getColumn(MysqlConnection.colmTwitchChannel), channel).get();
            long DcID = row.get(MysqlConnection.colmDcServer).getAsLong();
            List<Invite> invites = Main.getDiscord().getBot().getGuildById(DcID).retrieveInvites().complete();
            if (getValidInvite(invites) != null) {
                return getValidInvite(invites);
            } else {
                return Main.getDiscord().getBot().getGuildById(DcID).getDefaultChannel().createInvite().complete().getUrl();
            }
        }
        return "";
    }

    private String getValidInvite(List<Invite> invites) {
        for (Invite invite : invites) {
            if (!invite.isTemporary()) {
                return invite.getUrl();
            }
        }
        return null;
    }

    private MessageEmbed sendTwitchNotifyEmbed(String StreamTitle, String ChannelName, String GameName, int ViewerCount) {
        return new EmbedBuilder()
                .setAuthor(ChannelName + " ist nun live auf Twitch!", "https://twitch.tv/" + ChannelName, "https://cdn.discordapp.com/avatars/513306244371447828/b78a6a320298d2e068f1859d05036cfe.png")
                .setColor(Main.getDiscord().getEmbedColor())
                .setTitle(StreamTitle, "https://www.twitch.tv/" + ChannelName)
                .setImage("https://static-cdn.jtvnw.net/previews-ttv/live_user_" + ChannelName + "-1920x1080.png")
                .setDescription("Spielt nun " + GameName + " für " + ViewerCount + " Zuschauern! \n" +
                        "[Schau vorbei](https://twitch.tv/" + ChannelName + ")")
                .setFooter("@Golden-Developer")
                .build();
    }
}