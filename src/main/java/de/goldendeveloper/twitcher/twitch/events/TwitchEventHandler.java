package de.goldendeveloper.twitcher.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.*;
import com.github.twitch4j.helix.domain.SubscriptionEvent;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.List;

public class TwitchEventHandler {

    @EventSubscriber
    public void onChannelGoLive(ChannelGoLiveEvent e) {
        System.out.println("New Stream");
        if (Main.getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(CreateMysql.colmDcServer) && table.existsColumn(CreateMysql.colmTwitchChannel)) {
                    HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmTwitchChannel), e.getChannel().getName());
                    if (row.containsKey(CreateMysql.colmTwitchChannel) && row.containsKey(CreateMysql.colmDcStreamNotifyRole) && row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && row.containsKey(CreateMysql.colmTwitchChannel)) {
                        System.out.println("ERROR 5");
                        Guild guild = Main.getDiscord().getBot().getGuildById(row.get(CreateMysql.colmDcServer).toString());
                        Channel channel = guild.getGuildChannelById(row.get(CreateMysql.colmDcStreamNotifyChannel).toString());
                        if (guild != null) {
                            if (channel != null) {
                                System.out.println("ERROR 6");
                                Role role = guild.getRoleById(row.get(CreateMysql.colmDcStreamNotifyRole).toString());
                                if (role != null) {
                                    if (channel.getType().equals(ChannelType.NEWS)) {
                                        NewsChannel newsChannel = guild.getNewsChannelById(channel.getId());
                                        if (newsChannel != null) {
                                            newsChannel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!")
                                                    .setEmbeds(sendTwitchNotifyEmbed(e.getStream().getTitle(), e.getChannel().getName(), e.getStream().getGameName(), e.getStream().getViewerCount()))
                                                    .queue();
                                        }
                                    } else if (channel.getType().equals(ChannelType.TEXT)) {
                                        TextChannel textChannel = guild.getTextChannelById(channel.getId());
                                        if (textChannel != null) {
                                            textChannel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!")
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
        }
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
        Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
        if (table.getColumn(CreateMysql.colmTwitchChannel).getAll().contains(channel)) {
            HashMap<String, Object> row = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName).getRow(table.getColumn(CreateMysql.colmTwitchChannel), channel);
            String DcID = row.get(CreateMysql.colmDcServer).toString();
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