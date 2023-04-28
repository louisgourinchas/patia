The main code of the planner is available in the src/planner/CPlanner.java file.

---

Note: the planner is not working. Currently it will only return one instruction as the plan.

You can run the planner with the runningScript (written by Mattvei), usage:
./runningScript <make | run | makerun> [-d domain] [-p problem]

If you want to manually run the planner, run:

javac -d classes -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar src/planner/*.java

java -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar planner.CPlanner src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl

with the last two arguments being respectively the domain and the problem files.