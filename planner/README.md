javac -d classes -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar src/fr/uga/pddl4j/examples/asp/*.java

java -cp classes:lib/pddl4j-4.0.0.jar:lib/sat4j-sat.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl -e FAST_FORWARD -w 1.2 -t 1000