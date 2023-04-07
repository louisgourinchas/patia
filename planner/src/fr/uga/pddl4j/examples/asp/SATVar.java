package fr.uga.pddl4j.examples.asp;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SATVar{

    private static final AtomicInteger count = new AtomicInteger(0); 
    private int id;
    private List<Integer> precond; 
    private List<Integer> effects;  

    public SATVar(){
        int id = count.getAndIncrement();
    }

    public void addPreCond(int id){
        precond.add(id);
    }

    public void addEffect(int id){
        effects.add(id);
    }
}
