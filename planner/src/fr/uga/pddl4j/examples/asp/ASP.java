package fr.uga.pddl4j.examples.asp;

import fr.uga.pddl4j.heuristics.state.FastForward;
import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.RequireKey;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.SearchStrategy;
import fr.uga.pddl4j.planners.statespace.search.StateSpaceSearch;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.util.BitVector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.List;
/**
 * The class is an example. It shows how to create a simple A* search planner able to
 * solve an ADL problem by choosing the heuristic to used and its weight.
 *
 * @author D. Pellier
 * @version 4.0 - 30.11.2021
 */
@CommandLine.Command(name = "ASP",
    version = "ASP 1.0",
    description = "Solves a specified planning problem using A* search strategy.",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n")
public class ASP extends AbstractPlanner {

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
    private static final Logger LOGGER = LogManager.getLogger(ASP.class.getName());

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
        //i am making soup
        System.out.println("\n\nThis is where i make my soup.\n\n");

        /* temporary notes on what ive learned
         * predicates: the actions that can be taken (as in, archetypes of actions)
         * actions: the actual actions that can be taken (like move x from a to b) with preconditions and effects (conditional or not)
         * preconditions: set of fluents that must be true for an action to be possible
         * effects: set of fluents set to true by the action + sets of fluents set to false by the action.
         * 
         * I am to construct a list of clauses based on everything from initial state to goal
        */

        //get the estimated number of steps.
        FastForward ff = new FastForward(problem);
        State initialState = new State(problem.getInitialState());
        int estimate = ff.estimate(initialState, problem.getGoal());
        System.out.println("estimated number of steps from initial state: " + estimate);

        List<Fluent> fluents = problem.getFluents();
        List<Action> actions = problem.getActions();
        int midsize = fluents.size() + actions.size();
        int size = (midsize) * estimate;

        // for(Action a : actions){
        //     System.out.println(a.getName());
        //     System.out.println(a.getPrecondition().toString());
        //     System.out.println(a.getUnconditionalEffect().getPositiveFluents());
        // }

        //starting to buld the general sat var array
        //Note: every SATVar id should be equal to it's index in variables
        SATVar[] variables;
        variables = new SATVar[size];

        //iterating over every fluent and every action, for every step
        for(int step=0; step<estimate; step++){
            for(int i=0;i<fluents.size();i++){
                variables[step*midsize + i] = new SATVar();
                //fluents have no precond/effect to add
            }
            for(int i=0;i<actions.size();i++){
                //add the variable
                int id = step*midsize + fluents.size() + i;
                variables[id] = new SATVar();

                //add the precondition (our preconditions only even have positive fluents)
                String s = actions.get(i).getPrecondition().getPositiveFluents().toString();
                for(String fluentId : s.substring(1, s.length()-1).split(" ")){
                    //an action at step i will require preconditions to be true at step i
                    variables[id].addPreCond(Integer.parseInt(fluentId + (step*midsize)));
                }

                //add the effect
                s = actions.get(i).getUnconditionalEffect().getNegativeFluents().toString();
                for(String fluentId : s.substring(1, s.length()-1).split(" ")){
                    //an action at step i will have consequences at step i+1
                    //TODO make negative
                    variables[id].addEffect( Integer.parseInt(fluentId) + ((step+1)*midsize) );
                }
                s = actions.get(i).getUnconditionalEffect().getPositiveFluents().toString();
                for(String fluentId : s.substring(1, s.length()-1).split(" ")){
                    //an action at step i will have consequences at step i+1
                    variables[id].addEffect( Integer.parseInt(fluentId) + ((step+1)*midsize) );
                }
            }
        }


        // Creates the A* search strategy
        StateSpaceSearch search = StateSpaceSearch.getInstance(SearchStrategy.Name.ASTAR,
            this.getHeuristic(), this.getHeuristicWeight(), this.getTimeout());
        LOGGER.info("* Starting A* search \n");
        // Search a solution
        Plan plan = search.searchPlan(problem);
        // If a plan is found update the statistics of the planner and log search information
        if (plan != null) {
            LOGGER.info("* A* search succeeded\n");
            this.getStatistics().setTimeToSearch(search.getSearchingTime());
            this.getStatistics().setMemoryUsedToSearch(search.getMemoryUsed());
        } else {
            LOGGER.info("* A* search failed\n");
        }
        // Return the plan found or null if the search fails.
        return plan;
    }

    /**
     * The main method of the <code>ASP</code> planner.
     *
     * @param args the arguments of the command line.
     */
    public static void main(String[] args) {
        try {
            final ASP planner = new ASP();
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