package fr.uga.pddl4j.examples.asp;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SATVar{

    private static final AtomicInteger count = new AtomicInteger(0); 
    private int id;
    private List<Integer> precond = new ArrayList(); 
    private List<Integer> posEffects = new ArrayList();  
    private List<Integer> negEffects = new ArrayList();  

    public SATVar(){
        this.id = count.getAndIncrement();
    }

    public void addPreCond(int id){
        precond.add(id);
    }

    public List<Integer> getPreCond(){
        return precond;
    }

    public void addPosEffect(int id){
        posEffects.add(id);
    }

    public List<Integer> getPosEffect(){
        return posEffects;
    }

    public void addNegEffect(int id){
        negEffects.add(id);
    }

    public List<Integer> getNegEffect(){
        return negEffects;
    }

    public int getId(){
        return this.id;
    }
}
