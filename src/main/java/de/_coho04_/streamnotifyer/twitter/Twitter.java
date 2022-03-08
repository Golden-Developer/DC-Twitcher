package de._coho04_.streamnotifyer.twitter;

import de._coho04_.streamnotifyer.Main;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;

public class Twitter {

    private static TwitterClient twitterClient;

    public Twitter() {
        try {
            twitterClient = new TwitterClient(TwitterCredentials.builder()
                    .accessToken("1367920079241936896-mYlXNIZmtErifVjEXT4SMQVx3UmcEc")
                    .accessTokenSecret("Lv8DUBc5ZA6pbnVSW1eSD7sNvvdzoX1W5gl8f4GlX4w5G")
                    .apiKey("hkcTmLEOgyERP8McmEhn9EebD")
                    .apiSecretKey("wumCuLQw9V7nFKifz8xqqCqU3RSG2RshARLRwdN1FK6zH3fbf5")
                    .build());
        } catch (Exception e) {
            TextChannel channel = Main.getDiscord().getBot().getGuildById(817500165866782770L).getTextChannelById("854740410189742150");
            MessageEmbed embed = new EmbedBuilder().addField("[ERROR]","Twitter: " + e.getMessage(), true).setColor(new Color(250, 0, 0)).build();
            assert channel != null;
            channel.sendMessageEmbeds(embed).queue();
        }
    }

    public TwitterClient getClient() {
        return twitterClient;
    }
}
