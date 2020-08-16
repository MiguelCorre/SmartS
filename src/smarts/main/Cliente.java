package smarts.main;

import java.util.Random;

public class Cliente  {
    
    private static int lastUsedPersonID = -1;
    public int clienteID;
    private final int timeEntered;
    private int timeLeft;
    public int shopTime;
    public String name;
    private static final String FirstName [] = {"Verinha", "Miguel", "Armindo", "Odete", "Sara", "Raul", "Ines", "Ana", "Ricardo", "Paulo", "Rodrigo", "Gonçalo", "Abdul", "Alberto", "Alice", "Alfreda", "Alfredo", "Joao", "Adolf", "Alma", "Aisha", "Bernardo", "Beatriz", "Caetana", "Caetano", "Diego", "Dora"};
    private static final String LastName [] = {"Correia", "Rodrigues", "Lafayette", "Nogueira", "Amaral", "Fernandes", "Azevedo", "dos Santos", "Sousa", "Pinto", "Silva", "Santos", "Costa", "Oliveira", "Ferreira", "Gomes", "Marques", "Almeida", "Barbosa", "Tavares", "Guerreiro", "Pinheiro"};
    private Random rand = new Random();
    public String compra;
    
    public Cliente(String name, String compra, int enteringTime, int shopTime) {
        this.name = name;
        this.clienteID = nextPersonID();
        this.shopTime = shopTime;
        this.compra = compra;
        timeEntered = enteringTime;
        timeLeft = -1;
  }
    public String generateName() {
        return FirstName[rand.nextInt(FirstName.length)] + " " + LastName[rand.nextInt(LastName.length)];
    }

    public void setCompra(String compra) {
        this.compra = compra;
    }

    public String getCompra() {
        return compra;
    }
    
    
    
    @Override
    public String toString() {
        return "Cliente "+ name + " shopTime"+shopTime;
    }
    
    public int nextPersonID() {
    lastUsedPersonID++;
    return lastUsedPersonID;
  }

    public int getEnteringTime() {
        return timeEntered;
    }
    
    public void setLeavingTime(int leavingTime) {
    timeLeft=leavingTime;
  }  

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
    
    

  }    
    

