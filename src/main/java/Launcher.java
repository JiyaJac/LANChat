import server.ChatServer;

public class Launcher {
    public static void main(String[] args) {
        ChatServer ch = new ChatServer();
        ch.login("ba", "123");
        ch.login("a", "123");
        //Set t = ch.getUsers();
        System.out.println(ch.getUsers());

        String id = ch.createPrivate("a", "ba");
        ch.sendMessage(id, "Omgggg");

        ch.logout("a");
        ch.sendMessage(id, "no");
        //Set r = ch.getUsers();
        System.out.println(ch.getUsers());

    }
}
