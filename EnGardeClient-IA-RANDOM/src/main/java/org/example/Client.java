package org.example;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Client {
    private Board board;
    private Hand hand;
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        try {
            ServerMessageReader messageReader = new ServerMessageReader(socket);
            messageReader.start();
            envoyerNom("IA-RANDOM");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void traiterMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> leJson = mapper.readValue(message, HashMap.class);
            String id = leJson.get("Type");
            String contenu = leJson.get("Message");
            System.out.println(leJson);
            // Traiter les différents types de messages ici
            if ("ConnectionStart".equals(id)) {
                System.out.println("Connecté au serveur");
            } else if ("DoPlay".equals(id) || "Message fromat error.".equals(contenu)) {

                Random random = new Random();
                int randint = random.nextInt(3);

                if(randint == 0){
                    moveForward();

                } else if (randint == 1) {
                    moveBackward();

                } else if (randint ==2) {
                    attack();
                }

                
            } else if ("BoardInfo".equals(id)) {
                System.out.println("Nous avons reçu les infos du plateau");
                this.board = new Board(Integer.parseInt(leJson.get("PlayerPosition_0")),
                        Integer.parseInt(leJson.get("PlayerPosition_1")), Integer.parseInt(leJson.get("NumofDeck")));
            } else if ("HandInfo".equals(id)) {
                System.out.println("Nous avons reçu les infos de la main");
                this.hand = new Hand(Integer.parseInt(leJson.get("Hand1")), Integer.parseInt(leJson.get("Hand2")),
                        Integer.parseInt(leJson.get("Hand3")), Integer.parseInt(leJson.get("Hand4")),
                        Integer.parseInt(leJson.get("Hand5")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void envoyerMessage(String message) {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(message);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void moveForward() throws IOException, InterruptedException {
        sendEvaluateMessage();
        HashMap<String, String> response = new HashMap<>();
        response.put("From", "Client");
        response.put("To", "Server");
        response.put("Type", "Play");
        response.put("Direction", "F");
        response.put("MessageID", "101");
        Random random = new Random();
        int randint = random.nextInt(5) + 1;
        response.put("PlayCard", Integer.toString(randint));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        envoyerMessage(json);
    }

public void moveBackward() throws IOException, InterruptedException {
        sendEvaluateMessage();
        HashMap<String, String> response = new HashMap<>();

        response.put("From", "Client");
        response.put("To", "Server");
        response.put("Type", "Play");
        response.put("Direction", "B");
        response.put("MessageID", "101");
        Random random = new Random();
        int randint = random.nextInt(5) + 1; // Génère un nombre aléatoire entre 1 et 5
        response.put("PlayCard", Integer.toString(randint));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        envoyerMessage(json);
}

    public void attack() throws IOException, InterruptedException {
        sendEvaluateMessage();
        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","Play");
        response.put("MessageID","102");
        Random random = new Random();
        int randint1 = random.nextInt(1,5);
        int randint2 = random.nextInt(1 ,5);
        response.put("NumOfCard",Integer.toString(randint1));
        response.put("PlayCard",Integer.toString(randint2));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        envoyerMessage(json);
    }

     private void sendEvaluateMessage() throws IOException, InterruptedException, JsonProcessingException {
        //JSON構成
        //<json>{"Type":"Evaluation","PlayCard":"4","From":"Client","To":"Server","NumOfCard":"2","MessageID":"102"}</json>
        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","Evaluation");
        response.put("1F","0.1");
        response.put("2F","0.1");
        response.put("3F","0.1");
        response.put("4F","0.1");
        response.put("5F","0.1");
        response.put("1B","0.1");
        response.put("2B","0.1");
        response.put("3B","0.1");
        response.put("4B","0.1");
        response.put("5B","0.1");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        envoyerMessage(json);
    }
    public void envoyerNom(String nom) {
        try {
            HashMap<String, String> messageJson = new HashMap<>();
            messageJson.put("Type", "PlayerName");
            messageJson.put("From", "Client");
            messageJson.put("To", "Server");
            messageJson.put("Name", nom);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(messageJson);

            envoyerMessage(json);
            System.out.println("Nom envoyé au serveur : " + nom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Classe interne pour la lecture des messages du serveur dans un thread séparé
    private class ServerMessageReader extends Thread {
        private Socket socket;

        public ServerMessageReader(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    // Traiter le message reçu
                    traiterMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12052);
            Client client = new Client(socket);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
