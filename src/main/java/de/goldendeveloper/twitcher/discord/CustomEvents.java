package de.goldendeveloper.twitcher.discord;

import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.discord.commands.Settings;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class CustomEvents extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        if (e.getName().equalsIgnoreCase(Settings.cmdSettings)) {
            if (e.getSubcommandName().equalsIgnoreCase(Settings.cmdSettingsSubTwitchChannel)) {
                if (e.getFocusedOption().getName().equalsIgnoreCase(Settings.cmdSettingsSubTwitchChannelOptionAction)) {
                    e.replyChoices(
                            new Command.Choice("hinzufügen", "add"),
                            new Command.Choice("entfernen", "remove")
                    ).queue();
                } else if (e.getFocusedOption().getName().equalsIgnoreCase(Settings.cmdSettingsSubTwitchChannelOptionAction) && e.getOption(Settings.cmdSettingsSubTwitchChannelOptionAction) != null && e.getOption(Settings.cmdSettingsSubTwitchChannelOptionAction).getAsString().equalsIgnoreCase("remove")) {
                    List<String> channels = getGuildTwitchChannel(e.getGuild());
                    e.replyChoices(channels.stream().map(channel -> new Command.Choice(channel, channel)).toList()).queue();
                }
            }
        }
    }

    public List<String> getGuildTwitchChannel(Guild guild) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
        return table.getRows().stream().filter(row -> row.getData().get(MysqlConnection.colmDcServer).getAsString().equalsIgnoreCase(guild.getId()))
                .map(row -> row.getData().get(MysqlConnection.colmTwitchChannel).getAsString()).toList();
    }
}
