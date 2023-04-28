package planner;

import fr.uga.pddl4j.heuristics.state.FastForward;
import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.RequireKey;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ISolver;
import org.sat4j.minisat.SolverFactory;

/**
 * The class is an example. It shows how to create a simple A* search planner able to
 * solve an ADL problem by choosing the heuristic to used and its weight.
 *
 * @author D. Pellier
 * @version 4.0 - 30.11.2021
 */
@CommandLine.Command(name = "CSTM",
    version = "0.1",
    description = "Custo planner by louis gourinchas",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n")
public class CPlanner extends AbstractPlanner {

    /**
     * The weight of the heuristic.
     */
    private double heuristicWeight;

    /**
     * The name of the heuristic used by the planner.
     */
    private StateHeuristic.Name heuristic;

    /**
     * The class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CPlanner.class.getName());

    /**
     * Instantiates the planning problem from a parsed problem.
     *
     * @param problem the problem to instantiate.
     * @return the instantiated planning problem or null if the problem cannot be instantiated.
     */
    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        final Problem pb = new DefaultProblem(problem);
        pb.instantiate();
        return pb;
    }

    /**
     * Search a solution plan to a specified domain and problem using A*.
     *
     * @param problem the problem to solve.
     * @return the plan found or null if no plan was found.
     */
    @Override
    public Plan solve(final Problem problem) {

        //get the estimated number of steps.
        FastForward ff = new FastForward(problem);
        State initialState = new State(problem.getInitialState());
        int estimate = ff.estimate(initialState, problem.getGoal());

        //variable initialization
        List<Fluent> fluents = problem.getFluents();
        List<Action> actions = problem.getActions();
        int midsize = fluents.size() + actions.size();
        int size = (midsize * (estimate-1)) + fluents.size();

        //starting to buld the general sat var array
        //Note: every SATVar id should be equal to it's index in variables
        SATVar[] variables;
        variables = new SATVar[size];

        //iterating over every fluent and every action, for every step except the final one
        for(int step=0; step<estimate-1; step++){
            for(int i=0;i<fluents.size();i++){
                variables[step*midsize + i] = new SATVar();
                //fluents have no precond/effect to add
            }
            for(int i=0;i<actions.size();i++){
                //add the variable
                int id = step*midsize + fluents.size() + i;
                variables[id] = new SATVar();

                //add the precondition (our preconditions only even have positive fluents)
                String tempString = actions.get(i).getPrecondition().getPositiveFluents().toString();
                for(String fluentId : tempString.substring(1, tempString.length()-1).split(",")){
                    //an action at step i will require preconditions to be true at step i
                    variables[id].addPreCond(Integer.parseInt(fluentId.trim()) + (step*midsize));
                }

                //add the effect
                //an action at step i will have consequences at step i+1
                tempString = actions.get(i).getUnconditionalEffect().getNegativeFluents().toString();
                for(String fluentId : tempString.substring(1, tempString.length()-1).split(",")){
                    variables[id].addNegEffect( Integer.parseInt(fluentId.trim()) + ((step+1)*midsize) );
                }
                tempString = actions.get(i).getUnconditionalEffect().getPositiveFluents().toString();
                for(String fluentId : tempString.substring(1, tempString.length()-1).split(",")){
                    variables[id].addPosEffect( Integer.parseInt(fluentId.trim()) + ((step+1)*midsize) );
                }
            }
        }

        //add fluents for the final step
        for(int i=0;i<fluents.size();i++){
            variables[(estimate-1)*midsize + i] = new SATVar();
            //fluents have no precond/effect to add
        }

        //variables are done, we now need to build all the expressions
        AtomicInteger index = new AtomicInteger(0); 
        ArrayList<int[]> clauses = new ArrayList();
        String tempString;

        //INITIALSTATE clauses
        //expression is (positivefluents and (not fluent")) of initialstate, where fluent" is every fluent not in positivefluent
        //because we need (x and y), we will make two separate clauses containing respectively (x) and (y)
        //step 0 fluents are located at the very start of variables, so their id is the same as their fluentid.
        int[] fluentChecker = new int[fluents.size()];
        Arrays.fill(fluentChecker,0);

        tempString = problem.getInitialState().getPositiveFluents().toString();
        if(!tempString.contains("{}")) {
            for(String fluent : tempString.substring(1, tempString.length()-1).split(",")){
                int[] temp = {Integer.parseInt(fluent.trim())};
                fluentChecker[Integer.parseInt(fluent.trim())] = 1;
                clauses.add(index.getAndIncrement(), temp);
            }
        }

        //because of the nature of the clause, we simply kept track of which fluent were added in positivefluents and add (not) of the rest
        for(int i=0 ; i<fluents.size();i++){
            if(fluentChecker[i]==0){
                int[] temp = {-1*i};
                clauses.add(index.getAndIncrement(), temp);
            }
        }

        //GOAL clauses
        //expression is (positive fluent and not negativefluents)
        //fluents for goal are located at the very last step so fluentid + (estimate-1)*midsize
        tempString = problem.getGoal().getPositiveFluents().toString();
        if(!tempString.contains("{}")) {
            for(String fluent : tempString.substring(1, tempString.length()-1).split(",")){
                int[] temp = {Integer.parseInt(fluent.trim())};
                clauses.add(index.getAndIncrement(), temp);
            }
        }
        tempString = problem.getGoal().getNegativeFluents().toString();
        if(!tempString.contains("{}")) {
            for(String fluent : tempString.substring(1, tempString.length()-1).split(",")){
                int[] temp = {-1*Integer.parseInt(fluent.trim())};
                clauses.add(index.getAndIncrement(), temp);
            }
        }

        //ACTION clauses
        //expressions are of the form A -> B, which translates to -A v B
        //A is of format action
        //B is of format {all preconds} and {effect+} and {not effect-}
        //so in conclusion, every action creates one clause per member of B, of form (-A v xB)
        for(int step=0; step<estimate-1; step++){

            for(int i=0;i<actions.size();i++){
                //id of current action in varibles
                int id = step*midsize + fluents.size() + i;
                SATVar currVar = variables[id];
                List<Integer> templist;

                //interate over preconditions for current action
                templist = currVar.getPreCond();
                for(int j=0 ; j < templist.size() ; j++){
                    int[] temp = {-1*id, templist.get(j)}; //(-A v xB)
                    clauses.add(index.getAndIncrement(), temp);
                }
                //interate over positive effects for current action
                templist = currVar.getPosEffect();
                for(int j=0 ; j < templist.size() ; j++){
                    int[] temp = {-1*id, templist.get(j)}; //(-A v xB)
                    clauses.add(index.getAndIncrement(), temp);
                }
                //interate over negatie effects for current action
                templist = currVar.getNegEffect();
                for(int j=0 ; j < templist.size() ; j++){
                    int[] temp = {-1*id, -1*templist.get(j)}; //(-A v xB)
                    clauses.add(index.getAndIncrement(), temp);
                }
            }
        }

        //STATE TRANSITIONS clauses
        //expressions are of form:
        //(fi and -fi+1) -> (OR {actions at step i that could result in -fi+1})
        //(-fi and fi+1) -> (OR {actions at step i that could result in fi+1})
        //once again, clause of form (A -> B) becomes (-A v B)
        //also, clause of form -(x and y) becomes ((-x) or (-y))
        //in the end, our clauses are of form (-fi or fi+1 or {actions}), (fi or -fi+1 or {actions}) 

        //-1 steps because we cant have fi, fi+1 if i = max step.
        for(int step=0; step<estimate-1; step++){
            for(int i=0;i<fluents.size();i++){

                //id of curent fluent in variables
                int id = step*midsize + i;

                //fi v -fi+1
                int temp[] = {id, -1*(id+midsize)}; 
                //iterate over the actions of current step
                for(int j = step*midsize + fluents.size();j<(step+1)*midsize;j++){
                    //if the action results in -fi+1, we add it.
                    if(variables[j].getNegEffect().contains(id+midsize))
                        temp = Arrays.copyOf(temp, temp.length +1);
                        temp[temp.length-1] = j;
                }
                if(temp.length>2)
                    clauses.add(index.getAndIncrement(), temp);

                //-fi v fi+1
                int temp2[] = {-1*id, id+midsize}; 
                //iterate over the actions of current step
                for(int j = step*midsize + fluents.size();j<(step+1)*midsize;j++){
                    //if the action results in fi+1, we add it.
                    if(variables[j].getPosEffect().contains(id))
                        temp2 = Arrays.copyOf(temp2, temp2.length +1);
                        temp2[temp2.length-1] = j;
                }
                if(temp2.length>2)
                    clauses.add(index.getAndIncrement(), temp2);             
            }
        }

        //ACTION DISJONCTION clauses
        //expressions are of form (-a v -b), with a and b actions possible at step i
        //we create one clause for every pair of actions, per step.

        for(int step=0; step<estimate-1; step++){

            for(int i=0;i<actions.size();i++){

                int id1 = step*midsize + fluents.size() + i;
                for(int j=i+1;j<actions.size();j++){

                    int id2 = step*midsize + fluents.size() + j;
                    int[] temp = {-1*id1, -1*id2};
                    clauses.add(index.getAndIncrement(), temp);
                }
            }
        }

        //Because the solver does not want any clause with the value 0,
        //we add 1 to every clause number (this will have to be reversed when interpreting the plan)
        int[] currClause;
        for(int i=0;i<clauses.size();i++){
            currClause = clauses.get(i);
            for(int j=0;j<clauses.get(i).length;j++){
                currClause[j] = currClause[j]+1;
            }
        }

        //prepare the sat4j solver
        final int MAXVAR = variables.length;
        final int NBCLAUSES = clauses.size();

        ISolver solver = SolverFactory.newDefault();
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        //feed all clauses to the solver
        for (int i=0;i<NBCLAUSES;i++) {
            int [] clause = clauses.get(i);
            try {
                solver.addClause(new VecInt(clause)); 
            } catch (Exception e) {
                //e.printStackTrace();
            }
            
        }
        //Solver now has the clauses and can generate a model

        boolean possible;
        try {
            possible = solver.isSatisfiable();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (!possible) {
            //solver cannot find a solution.
            return null;
        } 


        //Solver can find a solution, get to work on converting that solution from a model to a plan
        //how? Iterate over every action, and look at whether or not it is positive in the model.
        Plan finalPlan = new SequentialPlan();

        //iterate over every step (except final one, no action to be performed here)
        for(int step=0; step<estimate-1; step++){

            //iterate over every action
            for(int i=0;i<actions.size();i++){

                //id is where the action would be in the variables array, 
                //we look at id+1 because of what we did earlier in order to feed the solver.
                int id = step*midsize + fluents.size() + i;

                //if it's in the model, add it to the plan
                //we can assume that only one action will be in the model per step becaue of the disjoinction clauses.
                if(solver.model(id+1)){
                    finalPlan.add(step, actions.get(i));
                }
            }
        }
        for(Action a : finalPlan.actions()){
            System.out.println(a.getName());
        }

        //Solution
        return finalPlan;
    }

    /**
     * The main method of the <code>ASP</code> planner.
     *
     * @param args the arguments of the command line.
     */
    public static void main(String[] args) {
        try {
            final CPlanner planner = new CPlanner();
            CommandLine cmd = new CommandLine(planner);
            cmd.execute(args);
        } catch (IllegalArgumentException e) {
            LOGGER.fatal(e.getMessage());
        }
    }	

    /**
     * Sets the weight of the heuristic.
     *
     * @param weight the weight of the heuristic. The weight must be greater than 0.
     * @throws IllegalArgumentException if the weight is strictly less than 0.
     */
    @CommandLine.Option(names = {"-w", "--weight"}, defaultValue = "1.0",
        paramLabel = "<weight>", description = "Set the weight of the heuristic (preset 1.0).")
    public void setHeuristicWeight(final double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight <= 0");
        }
        this.heuristicWeight = weight;
    }

    /**
     * Set the name of heuristic used by the planner to the solve a planning problem.
     *
     * @param heuristic the name of the heuristic.
     */
    @CommandLine.Option(names = {"-e", "--heuristic"}, defaultValue = "FAST_FORWARD",
        description = "Set the heuristic : AJUSTED_SUM, AJUSTED_SUM2, AJUSTED_SUM2M, COMBO, "
            + "MAX, FAST_FORWARD SET_LEVEL, SUM, SUM_MUTEX (preset: FAST_FORWARD)")
    public void setHeuristic(StateHeuristic.Name heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Returns the name of the heuristic used by the planner to solve a planning problem.
     *
     * @return the name of the heuristic used by the planner to solve a planning problem.
     */
    public final StateHeuristic.Name getHeuristic() {
        return this.heuristic;
    }

    /**
     * Returns the weight of the heuristic.
     *
     * @return the weight of the heuristic.
     */
    public final double getHeuristicWeight() {
        return this.heuristicWeight;
    }

    /**
     * Returns if a specified problem is supported by the planner. Just ADL problem can be solved by this planner.
     *
     * @param problem the problem to test.
     * @return <code>true</code> if the problem is supported <code>false</code> otherwise.
     */
    @Override
    public boolean isSupported(Problem problem) {
        return (problem.getRequirements().contains(RequireKey.ACTION_COSTS)
            || problem.getRequirements().contains(RequireKey.CONSTRAINTS)
            || problem.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
            || problem.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
            || problem.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
            || problem.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
            || problem.getRequirements().contains(RequireKey.FLUENTS)
            || problem.getRequirements().contains(RequireKey.GOAL_UTILITIES)
            || problem.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
            || problem.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
            || problem.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
            || problem.getRequirements().contains(RequireKey.PREFERENCES)
            || problem.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS)
            || problem.getRequirements().contains(RequireKey.HIERARCHY))
            ? false : true;
    }

}