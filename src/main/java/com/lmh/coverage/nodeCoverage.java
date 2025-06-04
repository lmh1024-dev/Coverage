package com.lmh.coverage;

import com.lmh.coverage.entity.Edge;
import com.lmh.coverage.entity.Path;
import com.lmh.coverage.entity.edgePair;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.*;

import static com.lmh.coverage.utils.myBasicOperations.*;
import static com.lmh.coverage.utils.myBasicOperations.dijkstraToAccept;
import static dk.brics.automaton.Transition.appendCharString;

public class nodeCoverage extends Coverage{

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
        LinkedList<Path> NC = nodeCoverage.generatePath(this.getAttemptAutomaton(), form);

        stringsList = generateStrings(NC);

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
        LinkedList<Path> NC = new LinkedList<>();

        Set<Integer> visited = new HashSet<>();
        int initial = automaton.getInitialState().getNumber();
        visited.add(initial);
        path.setInitial(initial);

        DFSearch(visited,startPointLine,path,map,NC);

        return NC;
    }


    private static LinkedList<Edge> getEPPoint(State state,Map<State, LinkedList<Edge>> map){
        return map.get(state);
    }

    private static void DFSearch(Set<Integer> visited,LinkedList<Edge> epp, Path path, Map<State, LinkedList<Edge>> map, LinkedList<Path> NC) {
        LinkedList<Transition> transitions = path.getTransitions();
        boolean flag = false;

        if(!transitions.isEmpty() && transitions.getLast().getDest().isAccept()){
            Path newPath = path.deepClone();
            NC.add(newPath);
        }
        for(Edge e:epp){
            State dest = e.getTransition().getDest();
            int number = dest.getNumber();
            if(!e.getVisited() && !visited.contains(number)) {
                path.push(e.getTransition());
                e.setVisited(true);
                visited.add(dest.getNumber());
                flag = true;
                LinkedList<Edge> epPoint = getEPPoint(dest, map);
                DFSearch(visited,epPoint,path,map,NC);
            }

        }

        if(!flag){
            path.pop();
            State dest = path.getLastState();
            if(dest != null){
                LinkedList<Edge> epPoint = getEPPoint(dest, map);
                DFSearch(visited,epPoint,path,map,NC);
            }
        }

    }

}
