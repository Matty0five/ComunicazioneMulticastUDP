import java.net.*;
import java.io.*;

public class MulticastClient{
    //colore del prompt del Server
    public static final String ANSI_BLUE = "\u001B[34m";
    //colore del prompt del Client
    public static final String RED_BOLD = "\033[1;31m";
    //colore del prompt del gruppo
    public static final String GREEN_UNDERLINED = "\033[4;32m";
    //colore reset
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        int port = 2000;                            // numero di porta del server
        int portGroup = 1900;                       // numero di porta del gruppo
        InetAddress serverAddress;                  // indirizzo del server
        DatagramSocket datagramSocket = null;       // socket UDP
        MulticastSocket multicastSocket = null;     // socket Multicast UDP
        InetAddress group;                          // indirizzo gruppo multicast UDP

        DatagramPacket outPacket;       // Datagramma UDP con la richiesta da inviare al server
        DatagramPacket inPacket;        // Datagramma UDP di risposta ricevuto dal server

        // buffer in lettura
        byte[] inBuffer = new byte[256];
        byte[] inBufferGroup = new byte[1024];

        String messageOut = "Richiesta comunicazione";      // messaggio di richiesta
        String messageIn;                                   // messaggio di risposta

        try{
            System.out.println(RED_BOLD + "CLIENT UDP" + RESET);
            
            // 1) RICHIESTA DAL SERVER
            // si recupera l'IP del server UDP
            serverAddress = InetAddress.getLocalHost();
            System.out.println(RED_BOLD + "Indirizzo del server trovato!" + RESET);

            // istanza del socket UDP per la prima comunicazione con il server
            datagramSocket = new DatagramSocket();

            // si prepara il datagramma con i dati da inviare
            outPacket = new DatagramPacket(inBufferGroup, port, serverAddress, port);

            // si inviano i dati
            datagramSocket.send(outPacket);
            System.out.println(RED_BOLD + "Richiesta al server inviata!" + RESET);

            // 2) RISPOSTA DAL SERVER
            // si prepara il datagramma per ricevere i dati dal server
            inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            datagramSocket.receive(inPacket);

            // lettura del messaggio ricevuto e sua visualizzazione
            messageOut = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Lettura dei dati ricevuti dal server" + RESET);

            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Messaggio ricevuto dal server " + serverAddress + ":" + port + "\n\t" + messageIn + RESET);

            //3) RICEZIONE MESSAGGIO DEL GRUPPO
            //istanza del Multicast socket e unione al gruppo
            multicastSocket = new MulticastSocket(portGroup);
            group = InetAddress.getByName("239.255.255.250");
            multicastSocket.joinGroup(group);
            
            //si prepara il datagramma per ricevere dati dal gruppo
            inPacket = new DatagramPacket(inBufferGroup,inBufferGroup.length);
            multicastSocket.receive(inPacket); 
            
            //lettura del messaggio ricevuto e sua visualizzazione
            messageIn = new String(inPacket.getData(),0, inPacket.getLength());
            
            System.out.println(GREEN_UNDERLINED + "Lettura dei dati ricevuti dai partecipanti al gruppo" + RESET);
            System.out.println(GREEN_UNDERLINED + "Messaggio ricevuto dal gruppo " + group +
                ":" + portGroup + "\n\t" + messageIn + RESET);
            
            //uscita dal gruppo
            multicastSocket.leaveGroup(group);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.err.println("Errore di risoluzione");
        } catch (SocketException ex) {
            ex.printStackTrace();
            System.err.println("Errore di creazione socket");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Errore di I/O");
        }
        finally{
            if (datagramSocket != null)
                datagramSocket.close();
            if (multicastSocket != null)
                multicastSocket.close();
        }
    }
}