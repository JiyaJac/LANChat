package server;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;



public class ChatServer {
    Set<String> s = new TreeSet<>();
    HashMap<String, PrivateRoom> mappings = new HashMap<>();


    public void login(String user, String password) {
        System.out.println(user + " is authenticated");
        s.add(user);
    }

    public Set getUsers() {
        return (this.s);
    }

    public void logout(String user) {
        s.remove(user);
//        for (var entry : mappings.entrySet()) {
//            String id = entry.getKey();
//            PrivateRoom room = entry.getValue();
//
//            if (room.getUser1().equals(user) || room.getUser2().equals(user)) {
//                mappings.remove(id);
//            }
//        }
    }

    public String createPrivate(String u1, String u2) {
        if (!(s.contains(u1) && s.contains(u2))) {
            System.out.println("Both of the users are not present");
            return null;
        }
        PrivateRoom pr = new PrivateRoom(u1, u2);
        if (!(mappings.containsKey(pr.getId()))) {
            mappings.put(pr.getId(), pr);
        }
        return pr.getId();
    }

    public void sendMessage(String id, String msg) {
        PrivateRoom room = mappings.get(id);
        if (room != null) {
            room.sendMessage(msg);
        } else {
            System.out.println("Room with id " + id + " does not exist");
        }
    }
}
