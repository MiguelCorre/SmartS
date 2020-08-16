package smarts.main;

import smarts.produtos.Produto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import smarts.storeinterface.InterfaceController;

public class StoreController implements Runnable {

    private static final int defaultHowManyPeopleMax = 5;         //default number of maximum people
    private static final int defaultDelayBetweenStepsMilli = 600; //default delay in milliseconds
    private static final int incrementInterval = 10;                 //clock ticks after which people are randomly generated
    private static final int maxPeopleGenerated = 5;
    
    private int clockTicks;             //clock ticks passed till now (time expired during simulation)
    private int howManyPeopleMax;       //maximum number of people in simulation
    private final int delayBetweenStepsMilli; //delay for clock ticks
    private final NaiveStoreAlgorithm algorithm;
    public ArrayList allPeopleList;    //list of all the people entered in simulation till now 
    public ArrayList peopleGenerated;    //an arraylist to keep account of people generated, but not yet entered the store
    private ArrayList storeList; // list of all people that are inside store
    private int n;
    private String clienteName;
    private String compra;

    Cliente newArrival = new Cliente(clienteName, compra, getClockTicks(), n);
    Produto produto = new Produto();

    public StoreController(int howManyPeopleMax, NaiveStoreAlgorithm ea) {
        this.howManyPeopleMax = howManyPeopleMax;
        delayBetweenStepsMilli = defaultDelayBetweenStepsMilli;
        peopleGenerated = new ArrayList();
        storeList = new ArrayList();
        allPeopleList = new ArrayList(); 
        clockTicks = 0;
        algorithm = ea;
        ea.setController(this);
    }

    public int getClockTicks() {
        return clockTicks;
    }

    public synchronized boolean loadPerson(Cliente person) {
        ArrayList people = (ArrayList) peopleGenerated;
        if (!people.contains(person)) {
            return false;
        }
        people.remove(person);
        storeList.add(person);
        System.out.println("CLIENTE " + person.name + " ENTROU");
        Platform.runLater(() -> {
            InterfaceController.names.add("CLIENTE " + person.name + " ENTROU");
        });
        incrementClock();
        System.out.println("(" + getClockTicks() + ")");
        Platform.runLater(() -> {
            InterfaceController.names.add("(" + getClockTicks() + ")");
        });

        delay(1);
        return true;
    }

    public int getRemainingPeopleCount() {
        int count = storeList.size();
        ArrayList people = (ArrayList) (peopleGenerated);
        count += people.size();

        return count;
    }

    public int remainingPeopleInStore() {
        int count = storeList.size();
        return count;
    }

    public synchronized ArrayList getAllPeopleWaiting() {
        ArrayList waiting = new ArrayList();

        Iterator itr = ((ArrayList) peopleGenerated).iterator();
        while (itr.hasNext()) {
            waiting.add((Cliente) itr.next());
        }

        return waiting;
    }

    public Cliente getLongestWaitingPerson(ArrayList waitingPeople) {
        Cliente mostWaiting = null;
        int time;
        int lowestEnteringTime = getClockTicks();
        Iterator itr = waitingPeople.iterator();
        while (itr.hasNext()) {
            Cliente cliente = (Cliente) itr.next();
            time = cliente.getEnteringTime();
            if (time <= lowestEnteringTime) {
                mostWaiting = cliente;
            }
        }
        return mostWaiting;
    }

    public synchronized boolean unloadPerson() {
        Iterator itr = storeList.iterator();
        if (storeList.isEmpty()) {
            return false;
        }
        while (itr.hasNext()) {
            Cliente next = (Cliente) itr.next();
            if (next.shopTime <= getClockTicks()) {
                itr.remove();
                next.setLeavingTime(getClockTicks());
                System.out.println("CLIENTE " + next.name + " SAIU.");
                Platform.runLater(() -> {
                    InterfaceController.names.add("CLIENTE " + next.name + " SAIU");
                });
                produto.loop(next);               
            }
        }
        incrementClock();
        System.out.println("(" + getClockTicks() + ")");
        Platform.runLater(() -> {
            InterfaceController.names.add("(" + getClockTicks() + ")");
        });
        delay(1);
        
        if (peopleGenerated.size() == 0 && storeList.size() == 0 && allPeopleList.size() == howManyPeopleMax) {
            System.out.println("A LOJA FECHOU");
            Platform.runLater(() -> {
                InterfaceController.names.add("\t\t\t\t\t\t\t\tA LOJA FECHOU");
                InterfaceController.names.add(produto.facturaTotal());
                
            });
        }
        return true;
    }

    void incrementClock() {
        clockTicks++;
        if (getClockTicks() % incrementInterval == 0) {
            generatePeople();
        }
    }

    public synchronized void generatePeople() {
        if (allPeopleList.size() < howManyPeopleMax) {
            if (peopleGenerated.size() == maxPeopleGenerated) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(StoreController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Random rand = new Random();
            Random rand2 = new Random();
            int newPeopleCount = 1;

            for (int i = 1; i <= newPeopleCount; i++) {
                int n3 = rand.nextInt(20) + 2 + newArrival.getEnteringTime();
                int n2 = rand2.nextInt(20) + 2;
                newArrival = new Cliente(newArrival.generateName(), newArrival.getCompra(), getClockTicks() + n2, n3);
                ArrayList people = (peopleGenerated);

                allPeopleList.add(newArrival);
                people.add(newArrival);
            }
        }
        delay(1);
    }

    @Override
    public void run() {
        generatePeople();

        while (getRemainingPeopleCount() > 0 || allPeopleList.size() < howManyPeopleMax) {
            synchronized (this) {
                incrementClock();
                System.out.println("(" + getClockTicks() + ")");
                algorithm.move();
            }
        }     
    }

    public void delay(int i) {
        try {
            Thread.sleep(i * this.delayBetweenStepsMilli);
        } catch (InterruptedException e) {
        }
    }
}
