package de.goldendeveloper.twitcher.twitch.events;

public class SendChatMessage implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i<20; i++)  {
            System.out.println("Hello from a thread!" + i);
        }
    }
}
