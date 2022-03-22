package de.goldendeveloper.twitcher.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.events.*;
import com.github.twitch4j.helix.domain.SubscriptionEvent;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

public class TwitchEventHandler {

    @EventSubscriber
    public void onChannelGoLive(ChannelGoLiveEvent e) {
        if (Main.getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(CreateMysql.colmDcServer) && table.existsColumn(CreateMysql.colmTwitchChannel)) {
                    HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmTwitchChannel), e.getChannel().getName());
                    if (row.containsKey(CreateMysql.colmTwitchChannel) && row.containsKey(CreateMysql.colmDcStreamNotifyRole) && row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && row.containsKey(CreateMysql.colmTwitchChannel)) {
                        TextChannel channel = Main.getDiscord().getBot().getTextChannelById(row.get(CreateMysql.colmDcStreamNotifyChannel).toString());
                        if (channel != null) {
                            Guild guild = Main.getDiscord().getBot().getGuildById(row.get(CreateMysql.colmDcServer).toString());
                            if (guild != null) {
                                Role role = guild.getRoleById(row.get(CreateMysql.colmDcStreamNotifyRole).toString());
                                if (role != null) {
                                    String LiveTitel = e.getStream().getTitle();
                                    MessageEmbed embed = new EmbedBuilder()
                                            .setAuthor(e.getChannel().getName() + " ist nun live auf Twitch!", "https://twitch.tv/" + e.getChannel().getName(), "https://cdn.discordapp.com/avatars/513306244371447828/b78a6a320298d2e068f1859d05036cfe.png")
                                            .setColor(Main.getDiscord().getEmbedColor())
                                            .setTitle(LiveTitel, "https://www.twitch.tv/" + e.getChannel().getName())
                                            .setImage("https://static-cdn.jtvnw.net/previews-ttv/live_user_" + e.getChannel().getName() + "-1920x1080.png")
                                            .setDescription("Spielt nun " + e.getStream().getGameName() + " für " + e.getStream().getViewerCount() + " Zuschauern! \n" +
                                                    "[Schau vorbei](https://twitch.tv/" + e.getChannel().getName() + ")")
                                            //.setTimestamp(DiscordBot.date)
                                            .setFooter("@Golden-Developer")
                                            .build();
                                    channel.sendMessage(role.getAsMention() + " ist nun Live auf Twitch!").setEmbeds(embed).queue();
                                }
                            }
                        }
                    }
                }
            }
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

    //@EventSubscriber
    //public void onChannelPointsRedemption(ChannelPointsRedemptionEvent e) {
    //}

    //@EventSubscriber
    //public static void onChannelFollowCountUpdate(ChannelFollowCountUpdateEvent e) {
    //}
}