# PATIA

This repository contains all of the works by Louis Gourinchas for PATIA class, year of 2022-2023.

## Custom SAT Planner

A custom SAT planner to solve pddl problems.
The planner is currently not working. It will only return one instruction as the plan, or none at all.

You can run the planner with the runningScript (written by [Mattvei](https://github.com/DrankRock)), usage:
```
./runningScript <make | run | makerun> [-d domain] [-p problem]
```

If you want to manually run the planner, run:
```
javac -d classes -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar src/planner/*.java

java -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar planner.CPlanner src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl
```
with the last two arguments being respectively the domain and the problem files.

## Sokoban
### Achieved features
- A pddl domain allowing us to solve sokoban problems.
- Java class allowing the automatic translation of a sokoban from a json file to a ppdl problem, as well as solving said problem.
- See [this readme](https://github.com/louisgourinchas/patia_rendus/tree/main/sokoban/src/main/java/sokoban) for detailed breakdown of classes' usage.

### How-to

After cloning the project, and from the top file level, simply run:
```
mvn package
```

and then 

```
java --add-opens java.base/java.lang=ALL-UNNAMED \
      -server -Xms2048m -Xmx2048m \
      -cp target/sokoban-1.0-SNAPSHOT-jar-with-dependencies.jar \
      sokoban.SokobanMain
```

If you want to run a different sokoban level, you need to manually change the file location in [this file](https://github.com/louisgourinchas/patia_rendus/blob/main/sokoban/src/main/java/sokoban/MyMain.java).

## Custom domains

A few custom domain that we were asked to create during the semester

- Hanoi
- Npuzzle
- Graph coloring
