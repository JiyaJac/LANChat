package database;


import java.util.Scanner;

public class Data {
    public static void main(String[] args)
    {

        Connection c =new Connection();
        Scanner sc=new Scanner(System.in);
        c.fetchUsers();

        System.out.println("Enter username: ");
        String u=sc.nextLine();
        System.out.println("Enter password: ");
        String p=sc.nextLine();

        if (c.users_list.contains(u) && c.password_list.contains(p)){
            System.out.println("Login successful");
        }
        else{
            System.out.println("Incorrect username or password");
        }
    }
}
