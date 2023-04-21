package fr.uga.pddl4j.examples.asp;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SATVar{

    private static final AtomicInteger count = new AtomicInteger(0); 
    private int id;
    private List<Integer> precond; 
    private List<Integer> posEffects;  
    private List<Integer> negEffects;  

    public SATVar(){
        int id = count.getAndIncrement();
    }

    public void addPreCond(int id){
        precond.add(id);
    }

    public void addPosEffect(int id){
        posEffects.add(id);
    }

    public void addNegEffect(int id){
        negEffects.add(id);
    }
}
