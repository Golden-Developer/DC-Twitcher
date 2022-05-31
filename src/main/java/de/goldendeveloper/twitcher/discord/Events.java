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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class Events extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "OFFLINE"));
        embed.setColor(0xFF0000);
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        if (new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).isDone()) {
            System.exit(0);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        String cmd = e.getName();
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User zRazzer = e.getJDA().getUserById("428811057700536331");
        if (e.isFromGuild()) {
            if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
                String subCmd = e.getSubcommandName();
                if (subCmd != null) {
                    if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubRole)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubRoleOptionRole);
                        if (mapping != null) {
                            Role role = mapping.getAsRole();
                            if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                                if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
                                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
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
                                if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                                    if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
                                        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
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
                            if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
                                if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
                                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
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
            }
        }
        if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
            List<Command> commands = Main.getDiscord().getBot().retrieveCommands().complete();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("**Help Commands**");
            embed.setColor(Color.MAGENTA);
            embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
            for (Command cm : commands) {
                embed.addField("/" + cm.getName(), cm.getDescription(), true);
            }
            e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                    net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://wiki.Golden-Developer.de/", "Online Übersicht"),
                    Button.link("https://support.Golden-Developer.de", "Support Anfragen")
            ).queue();
        } else if (e.getName().equalsIgnoreCase(Discord.getCmdShutdown)) {
            if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                e.getJDA().shutdown();
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot inhaber sein!").queue();
            }
        } else if (e.getName().equalsIgnoreCase(Discord.getCmdRestart)) {
            if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                try {
                    e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                    Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar restart");
                    p.waitFor();
                    e.getJDA().shutdown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot inhaber sein!").queue();
            }
        }
    }

    public void isAvailable(SlashCommandInteractionEvent e, @Nullable String success) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
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
}
