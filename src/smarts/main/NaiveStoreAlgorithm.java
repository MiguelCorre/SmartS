package smarts.main;

import java.util.ArrayList;
import smarts.produtos.Produto;


public class NaiveStoreAlgorithm  {

    private StoreController controller; 

    public void setController(StoreController controller) {
        this.controller = controller;
    }


    public void move() {
        
        ArrayList people = controller.getAllPeopleWaiting();
        Cliente cliente = controller.getLongestWaitingPerson(people);
        int remainingPeople = controller.remainingPeopleInStore();
        Produto prod = new Produto();

        if (cliente != null || remainingPeople != 0) {
            controller.loadPerson(cliente);
            controller.unloadPerson();
        }
    }
}
