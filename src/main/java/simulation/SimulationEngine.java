package simulation;

import elements.Animal;
import elements.Genome;
import elements.Vector2d;
import gui.SimulationWindow;
import interfaces.IGeneChoice;
import interfaces.IMutation;
import javafx.application.Platform;

import maps.AbstractWorldMap;


import java.util.*;

public class SimulationEngine implements Runnable {


    private AbstractWorldMap map;
    private final SimulationWindow app;
    private int delay ;
    private boolean flag=false;

    Random random = new Random();

    //genomy,mutacje
    public SimulationEngine(SimulationWindow app, AbstractWorldMap map, int numberOfAnimals, int startingEnergy,
                            double neededEnergy, int minNumberOfMutations, int maxNumberOfMutations,
                            IMutation mutation, int genomeLength, IGeneChoice geneChoice, int fullEnergy, int delay) {

        this.map = map;
        this.app = app;
        this.delay=delay;
        for (int i = 0; i < numberOfAnimals; i++) {
            Vector2d newPosition = new Vector2d(random.nextInt(this.map.getWidth()-1), random.nextInt(this.map.getHeight()-1));

            Genome genome = new Genome(genomeLength, mutation);

            Animal animalToSpawn = new Animal(map, newPosition, startingEnergy, genome, mutation, geneChoice,
                    genomeLength, neededEnergy, fullEnergy, map.getDay(),minNumberOfMutations,maxNumberOfMutations);

            this.map.place(animalToSpawn);


        }


    }
    public void end(){
        this.flag=true;

    }
    public void stop() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException err) {
                System.out.println("Watek został przerwany");
            }
        }
    }
    public void resume(){
        synchronized (this){
            notify();
        }
    }
    public AbstractWorldMap getMap(){
        return this.map;
    }

    @Override
    public void run() {

        try{
            Thread.sleep(2000);
        }catch (InterruptedException err){
            System.out.println(err.getMessage());
        }
        while ((this.map.getAnimals().size())>0 && !flag)
         {
            this.map.removeDead();
            this.map.moveAnimals();
            this.map.eatGrass();
            this.map.reproduction();
            this.map.addNewGrass();

            Platform.runLater(()->{
                this.app.renderGrid();
                if(this.map.getDominantGenome()!=null){
                    this.app.renderStats();
                }

            });

             this.map.anotherDay();
            try{
                Thread.sleep(delay);
            }catch (InterruptedException err){
                System.out.println(err.getMessage());
            }
        }
//        Platform.runLater(()->{
//
//            this.app.renderStats();
//        });

        System.out.println("koniec symulacji");

        }
    }

