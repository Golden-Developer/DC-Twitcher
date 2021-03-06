package de.goldendeveloper.twitcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class Config {

    private String DiscordToken;
    private String DiscordWebhook;
    private String MysqlHostname;
    private String MysqlUsername;
    private String MysqlPassword;
    private String TwitchClientID;
    private String TwitchClientSecret;
    private String TwitchCredinal;
    private int MysqlPort;

    public Config() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        readXML(classloader.getResourceAsStream("Login.xml"));
    }

    private void readXML(InputStream inputStream) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);
            doc.getDocumentElement().normalize();

            /* *** MYSQL *** */
            NodeList list = doc.getElementsByTagName("MYSQL");
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) list.item(i);
                    String hostname = element.getElementsByTagName("Hostname").item(0).getTextContent();
                    String username = element.getElementsByTagName("Username").item(0).getTextContent();
                    String password = element.getElementsByTagName("Password").item(0).getTextContent();
                    String port = element.getElementsByTagName("Port").item(0).getTextContent();
                    if (!hostname.isEmpty() || !hostname.isBlank()) {
                        this.MysqlHostname = hostname;
                    }
                    if (!username.isEmpty() || !username.isBlank()) {
                        this.MysqlUsername = username;
                    }
                    if (!password.isEmpty() || !password.isBlank()) {
                        this.MysqlPassword = password;
                    }
                    if (!port.isEmpty() || !port.isBlank()) {
                        this.MysqlPort = Integer.parseInt(port);
                    }
                }
            }

            /* *** Discord *** */
            list = doc.getElementsByTagName("Discord");
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) list.item(i);
                    String webhook = element.getElementsByTagName("Webhook").item(0).getTextContent();
                    String token = doc.getElementsByTagName("Token").item(0).getTextContent();
                    if (!webhook.isEmpty() || !webhook.isBlank()) {
                        this.DiscordWebhook = webhook;
                    }
                    if (!token.isEmpty() || !token.isBlank()) {
                        this.DiscordToken = token;
                    }
                }
            }

            /* *** Twitch *** */
            list = doc.getElementsByTagName("Twitch");
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) list.item(i);
                    String TwitchClientID = element.getElementsByTagName("ClientID").item(0).getTextContent();
                    String TwitchClientSecret = doc.getElementsByTagName("ClientSecret").item(0).getTextContent();
                    String TwitchCredinal = doc.getElementsByTagName("Credinal").item(0).getTextContent();
                    if (!TwitchClientID.isEmpty() || !TwitchClientID.isBlank()) {
                        this.TwitchClientID = TwitchClientID;
                    }
                    if (!TwitchCredinal.isEmpty() || !TwitchCredinal.isBlank()) {
                        this.TwitchCredinal = TwitchCredinal;
                    }
                    if (!TwitchClientSecret.isEmpty() || !TwitchClientSecret.isBlank()) {
                        this.TwitchClientSecret = TwitchClientSecret;
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getDiscordWebhook() {
        return DiscordWebhook;
    }

    public int getMysqlPort() {
        return MysqlPort;
    }

    public String getDiscordToken() {
        return DiscordToken;
    }

    public String getMysqlHostname() {
        return MysqlHostname;
    }

    public String getMysqlPassword() {
        return MysqlPassword;
    }

    public String getMysqlUsername() {
        return MysqlUsername;
    }

    public String getTwitchClientID() {
        return TwitchClientID;
    }

    public String getTwitchClientSecret() {
        return TwitchClientSecret;
    }

    public String getTwitchCredinal() {
        return TwitchCredinal;
    }
}