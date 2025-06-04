package com.lmh.coverage;

import com.lmh.coverage.entity.Edge;
import com.lmh.coverage.entity.Path;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.*;

import static com.lmh.coverage.utils.myBasicOperations.*;

public class edgeCoverage extends Coverage{
    public void generate(){
        //run positive
        run(true);

        //run negative
        run(false);
    }
    private void run(boolean isPositive){
        LinkedList<String> stringsList;

        if(!isPositive)
            this.setAttemptAutomaton(myComplement(this.getAutomaton(),this.getAttemptAutomaton()));


        Map<State, LinkedList<Edge>> form = generateEdgeMap(this.getAttemptAutomaton());

        LinkedList<Path> EC = edgeCoverage.generatePath(this.getAttemptAutomaton(), form);

        stringsList = generateStrings(EC);

        if(this.getAttemptAutomaton().run("")){
            stringsList.add("ε");
        }

        HashSet<String> stringsSet = boardProcess(stringsList);
        if(isPositive)
            this.setPositiveStrings(stringsSet.toString());
        else
            this.setNegativeStrings(stringsSet.toString());
    }

    static LinkedList<Path> generatePath(Automaton automaton, Map<State, LinkedList<Edge>> map){
        LinkedList<Edge> startPointLine = getEPPoint(automaton.getInitialState(),map);
        Path path = new Path();
        LinkedList<Path> EC = new LinkedList<>();

        int initial = automaton.getInitialState().getNumber();

        path.setInitial(initial);

        DFSearch(startPointLine,path,map,EC);

        for (State state : map.keySet()) {
            LinkedList<Edge> edges = map.get(state);
            for(Edge e:edges){
                if(!e.getVisited()){
                    Path newPath = new Path();
                    newPath.setInitial(state.getNumber());
                    newPath.push(e.getTransition());
                    DFSearch(edges,newPath,map,EC);
                }
            }

        }
        extendEPC(EC,automaton);

        return EC;
    }


    private static LinkedList<Edge> getEPPoint(State state,Map<State, LinkedList<Edge>> map){
        return map.get(state);
    }

    private static void DFSearch(LinkedList<Edge> epp, Path path, Map<State, LinkedList<Edge>> map, LinkedList<Path> EC) {
        LinkedList<Transition> transitions = path.getTransitions();
        boolean flag = false;

        if(!transitions.isEmpty() && transitions.getLast().getDest().isAccept()){
            Path newPath = path.deepClone();
            EC.add(newPath);
        }

        for(Edge e:epp){
            State dest = e.getTransition().getDest();
            if(!e.getVisited()) {
                path.push(e.getTransition());
                e.setVisited(true);
                flag = true;
                LinkedList<Edge> epPoint = getEPPoint(dest, map);
                DFSearch(epPoint,path,map,EC);
            }

        }
        if(!flag){
            path.pop();
            State dest = path.getLastState();
            if(dest != null){
                LinkedList<Edge> epPoint = getEPPoint(dest, map);
                DFSearch(epPoint,path,map,EC);
            }
        }

    }
}
