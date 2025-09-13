package server;


class PrivateRoom {
    private String user1;
    private String user2;
    private String id;

    PrivateRoom(String user1, String u2) {
        this.user1 = user1;
        this.user2 = u2;
        this.createId();
    }

    private String createId() {
        if (user1.compareToIgnoreCase(user2) < 0) {
            id = user1 + "_" + user2;
        } else {
            id = user2 + "_" + user1;
        }
        return id;
    }

    void sendMessage(String msg) {
        System.out.println(msg);
        System.out.println("Message has been sent in room with id: " + id);
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
