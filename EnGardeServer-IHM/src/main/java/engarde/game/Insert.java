package engarde.game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Insert {
    private String nom1;
    private Integer score1;
    private String nom2;
    private Integer score2;
    private Integer totalGame;
    
    public Insert(String nom1, Integer score1, String nom2, Integer score2) {
        this.nom1 = nom1;
        this.score1 = score1;
        this.nom2 = nom2;
        this.score2 = score2;
        this.totalGame = score1 + score2;
    }


    public int insert() {
        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://localhost:5432/";
            String nom = "illies";
            String mdp = "moi";
            Connection con = DriverManager.getConnection(url, nom, mdp);
        
            Statement stmt = con.createStatement();
            System.out.println("Connexion etablie");
            //stmt.executeUpdate("drop table if exists engarde");
            //stmt.executeUpdate("create table engarde"+
            //    "(CNO int PRIMARY KEY, NOM varchar(10), PRENOM varchar(10), AGE int)");
            String req = "Insert INTO engarde (name1,score1,name2,score2,toalgame) VALUES ('"+ this.nom1 +"'," + this.score1 + ",'" +this.nom2 + "'," + this.score2 + "," + this.totalGame + ");";
            System.out.println(req);
            int res = stmt.executeUpdate(req);


            con.close();
            System.out.println("All is ok !");

            return res;
        } catch (Exception e) {
            System.err.print("Une erreur est survenu (c'est a cause de Xavier)");
        }
        return -1;
    }


    public static void main(String[] args) {
        Insert i = new Insert("toto", 10, "titi", 20);
        int res = i.insert();
        System.out.println("Insertion  : "+ res);
    }

}
