package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.SearchResult;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;

public class Events extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getDiscord().getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        embed.setColor(0xFF0000);
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> {
            System.exit(0);
        });
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User zRazzer = e.getJDA().getUserById("428811057700536331");
        if (e.isFromGuild()) {
            String cmd = e.getName();
            if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
                String subCmd = e.getSubcommandName();
                if (subCmd != null) {
                    if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubRole)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubRoleOptionRole);
                        if (mapping != null) {
                            Role role = mapping.getAsRole();
                            if (Main.getMysqlConnection().getMysql().existsDatabase(Main.dbName)) {
                                if (Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                    Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                    if (table.existsColumn(MysqlConnection.colmDcServer)) {
                                        if (!table.getColumn(MysqlConnection.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                            table.insert(new RowBuilder()
                                                    .with(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId())
                                                    .with(table.getColumn(MysqlConnection.colmDcStreamNotifyChannel), "")
                                                    .with(table.getColumn(MysqlConnection.colmDcStreamNotifyRole), role.getId())
                                                    .with(table.getColumn(MysqlConnection.colmTwitchChannel), "")
                                                    .build()
                                            );
                                            isAvailable(e, "Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!");
                                        } else {
                                            HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).get();
                                            if (row.containsKey(MysqlConnection.colmDcStreamNotifyRole) && !row.get(MysqlConnection.colmDcStreamNotifyRole).getAsString().isEmpty()) {
                                                table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).set(table.getColumn(MysqlConnection.colmDcStreamNotifyRole), role.getId());
                                                isAvailable(e, "Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!");
                                            } else {
                                                e.getInteraction().reply("Die neue Stream Info Rolle kann nicht die alte Stream Info Rolle sein!").queue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubChannel)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubChannelOptionChannel);
                        if (mapping != null) {
                            TextChannel channel = mapping.getAsTextChannel();
                            if (channel != null) {
                                if (Main.getMysqlConnection().getMysql().existsDatabase(Main.dbName)) {
                                    if (Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                        Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                        if (table.existsColumn(MysqlConnection.colmDcServer)) {
                                            if (!table.getColumn(MysqlConnection.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                                table.insert(new RowBuilder()
                                                        .with(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId())
                                                        .with(table.getColumn(MysqlConnection.colmDcStreamNotifyChannel), channel.getId())
                                                        .with(table.getColumn(MysqlConnection.colmDcStreamNotifyRole), "")
                                                        .with(table.getColumn(MysqlConnection.colmTwitchChannel), "")
                                                        .build()
                                                );
                                                isAvailable(e, "Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!");
                                            } else {
                                                HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).get();
                                                if (row.containsKey(MysqlConnection.colmDcStreamNotifyChannel) && !row.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString().isEmpty()) {
                                                    table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).set(table.getColumn(MysqlConnection.colmDcStreamNotifyChannel), channel.getId());
                                                    isAvailable(e, "Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!");
                                                } else {
                                                    e.getInteraction().reply("Der neue Stream Info Channel kann nicht der alte Stream Info Channel sein!").queue();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubTwitchChannel)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubTwitchChannel);
                        if (mapping != null) {
                            String TwChannel = mapping.getAsString();
                            if (Main.getMysqlConnection().getMysql().existsDatabase(Main.dbName)) {
                                if (Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                    Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                    if (table.existsColumn(MysqlConnection.colmDcServer)) {
                                        if (!table.getColumn(MysqlConnection.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                            table.insert(new RowBuilder()
                                                    .with(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId())
                                                    .with(table.getColumn(MysqlConnection.colmDcStreamNotifyChannel), "")
                                                    .with(table.getColumn(MysqlConnection.colmDcStreamNotifyRole), "")
                                                    .with(table.getColumn(MysqlConnection.colmTwitchChannel), TwChannel)
                                                    .build()
                                            );
                                            isAvailable(e, "Der neue Twitch Channel ist nun " + TwChannel + "!");
                                        } else {
                                            HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).get();
                                            if (row.containsKey(MysqlConnection.colmTwitchChannel) && !row.get(MysqlConnection.colmTwitchChannel).getAsString().isEmpty()) {
                                                table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).set(table.getColumn(MysqlConnection.colmTwitchChannel), TwChannel);
                                                isAvailable(e, "Der neue Twitch Channel ist nun " + TwChannel + "!");
                                            } else {
                                                e.getInteraction().reply("Der neue Twitch Channel kann nicht der alte Twitch Channel sein!").queue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("**Help Commands**");
                embed.setColor(Color.MAGENTA);
                for (Command cm : Main.getDiscord().getBot().retrieveCommands().complete()) {
                    embed.addField("/" + cm.getName(), cm.getDescription(), true);
                }
                embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
                e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                        Button.link("https://wiki.Golden-Developer.de/", "Online Übersicht"),
                        Button.link("https://support.Golden-Developer.de", "Support Anfragen")
                ).queue();
            } else if (e.getName().equalsIgnoreCase(Discord.cmdShutdown)) {
                if (Main.getDeployment()) {
                    if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                        e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                        e.getJDA().shutdown();
                    } else {
                        e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                    }
                } else {
                    e.reply("Dieser Bot kann nicht heruntergefahren werden, da er sich im Entwickler Modus befindet!").queue();
                }
            } else if (e.getName().equalsIgnoreCase(Discord.cmdRestart)) {
                if (Main.getDeployment()) {
                    if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                        try {
                            e.getInteraction().reply("Der Discord Bot [" + e.getJDA().getSelfUser().getName() + "] wird nun neugestartet!").queue();
                            Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar restart");
                            p.waitFor();
                            e.getJDA().shutdown();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                    }
                } else {
                    e.reply("Dieser Bot kann nicht neugestartet werden, da er sich im Entwickler Modus befindet!").queue();
                }
            }
        }
    }

    public void isAvailable(SlashCommandInteractionEvent e, @Nullable String success) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(MysqlConnection.colmDcServer)) {
                    if (table.getColumn(MysqlConnection.colmDcServer).getAll().contains(e.getGuild().getId())) {
                        HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).get();
                        if (row.containsKey(MysqlConnection.colmTwitchChannel) && row.containsKey(MysqlConnection.colmDcStreamNotifyRole) && row.containsKey(MysqlConnection.colmDcStreamNotifyChannel) && row.containsKey(MysqlConnection.colmTwitchChannel)) {
                            String TwChannel = row.get(MysqlConnection.colmTwitchChannel).getAsString();
                            String DcChannel = row.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString();
                            String DcRole = row.get(MysqlConnection.colmDcStreamNotifyRole).getAsString();
                            if (!TwChannel.isEmpty()) {
                                if (!DcChannel.isEmpty()) {
                                    if (!DcRole.isEmpty()) {
                                        Main.getTwitch().addChannel(TwChannel);
                                    } else {
                                        if (success != null) {
                                            e.getInteraction().reply(success + "\nDie Stream Info Rolle fehlt bitte setzte eine mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubRole + "!").queue();
                                        } else {
                                            e.getInteraction().reply("Die Stream Info Rolle fehlt bitte setzte eine mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubRole + "!").queue();
                                        }
                                    }
                                } else {
                                    if (success != null) {
                                        e.getInteraction().reply(success + "\nDer Stream Info Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubChannel + "!").queue();
                                    } else {
                                        e.getInteraction().reply("Der Stream Info Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubChannel + "!").queue();
                                    }
                                }
                            } else {
                                if (success != null) {
                                    e.getInteraction().reply(success + "\nDer Twitch Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubTwitchChannel + "!").queue();
                                } else {
                                    e.getInteraction().reply("Der Twitch Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubTwitchChannel + "!").queue();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /*
     *  /settings discord-info-channel <TextChannel/NewsChannel>
     *  /settings twitch-info-channel <String>
     *  /settings twitch-info-role <Role>
     *
     * onTwitch
     * Twitch Channel Namens
     *
     * */
}
