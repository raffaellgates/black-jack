package jogo;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rafae
 */
import java.util.Random;
import java.io.*;
import java.net.*;

public class GameServer {
    
    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int numbermagic;
    Random random = new Random();
    private int player1ButtonNum;
    private int player2ButtonNum;
    
    
    public GameServer(){
        System.out.println("------Game Server------");
        numPlayers = 0;
        numbermagic = random.nextInt(12) + 1;
        try{
            ss = new ServerSocket(51734);
            
        }catch(IOException ex){
            System.out.println("IOException from GameServer Constructor");
            
        }
    }
    public void mandaScore (Socket socket,int placar) throws IOException{
        if(socket == player1.socket){
            player2.dataOut.writeInt(placar);
        }else{
            player1.dataOut.writeInt(placar);
        }
        
        
    }
    
    public void acceptConnections(){
        try{
            System.out.println("waiting for connections...");
            while(numPlayers < 2){
                Socket s = ss.accept();
                numPlayers ++;
                System.out.println("Player #" + numPlayers + "has connected");
                ServerSideConnection ssc  = new ServerSideConnection(s, numPlayers);
                if(numPlayers == 1){
                    player1 = ssc;
                }else{
                    player2 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("We noe have 2 players. No longer accepting connections.");
        }catch (IOException ex){
            System.out.println("IOException from acceptConnections()");
            
        }
    }
    private class ServerSideConnection implements Runnable{
        
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        
        public ServerSideConnection(Socket s, int id){
            socket = s;
            playerID =  id;
            try{
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex){
                System.out.println("IOException from SSC constructor");
            }
        }
        @Override
        public void run(){
            try{
                dataOut.writeInt(playerID);
                dataOut.writeInt(numbermagic);
                dataOut.flush();
                
                while (true){
                    if(playerID == 1){
                        player1ButtonNum = dataIn.readInt();
                        player2.sendButtonNum(player1ButtonNum);
                    }else{
                        player2ButtonNum = dataIn.readInt();
                        player1.sendButtonNum(player2ButtonNum);
                       

                    }
                    
                }
            }catch(IOException ex){
                System.out.println("IOException from run() SSC");
            }
            
        }
        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            }catch(IOException ex){
                System.out.println("IOExcepition from sendButtonNum() ssc");
            }
        }
    }
    
    public static void main(String[] args){
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
