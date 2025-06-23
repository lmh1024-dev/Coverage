package com.lmh.coverage;

import com.lmh.coverage.entity.Path;
import com.lmh.coverage.entity.edgePair;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.*;

import static com.lmh.coverage.utils.myBasicOperations.*;

public class edgePairCoverage{

    private Automaton attemptAutomaton;

    private String positiveStrings;

    private String negativeStrings;

    public Automaton getAttemptAutomaton() {
        return attemptAutomaton;
    }

    public void setAttemptAutomaton(Automaton attemptAutomaton) {
        this.attemptAutomaton = attemptAutomaton;
    }

    public String getPositiveStrings() {
        return positiveStrings;
    }

    public void setPositiveStrings(String positiveStrings) {
        this.positiveStrings = positiveStrings;
    }

    public String getNegativeStrings() {
        return negativeStrings;
    }

    public void setNegativeStrings(String negativeStrings) {
        this.negativeStrings = negativeStrings;
    }

    public void generate(String attemptRegex){
        RegExp attemptRegexp = new RegExp(attemptRegex);
        Automaton attemptAutomaton = attemptRegexp.toAutomaton();
        attemptAutomaton.determinize();
        attemptAutomaton.minimize();

        this.setAttemptAutomaton(attemptAutomaton);
        //run positive
        run(true);

        //run negative
        run(false);
    }
    private void run(boolean isPositive){
        LinkedList<String> stringsList = new LinkedList<>();

        if(!isPositive)
            this.setAttemptAutomaton(myComplement(this.getAttemptAutomaton()));

        if (!(this.getAttemptAutomaton().getStates().size() <= 1 && !this.getAttemptAutomaton().getInitialState().isAccept())) {
            Map<State, LinkedList<edgePair>> form = edgePairCoverage.generateEdgePairsMap(this.getAttemptAutomaton());


            LinkedList<Path> EPC = edgePairCoverage.generatePath(this.getAttemptAutomaton(), form);

            State initialState = this.getAttemptAutomaton().getInitialState();
            Set<Transition> transitions = initialState.getTransitions();
            for (Transition t:transitions){

                State dest = t.getDest();

                if(dest.isAccept()){
                    Path path = new Path(initialState.getNumber());
                    path.push(t);
                    EPC.add(path);
                }
            }

            stringsList = generateStrings(EPC);

            if(this.getAttemptAutomaton().run("")){
                stringsList.add("ε");
            }


        }
        HashSet<String> stringsSet = boardProcess(stringsList);

        if(isPositive)
            this.setPositiveStrings(stringsSet.toString());
        else
            this.setNegativeStrings(stringsSet.toString());

    }


    public static Map<State, LinkedList<edgePair>> generateEdgePairsMap(Automaton automaton){
        Map<State, LinkedList<edgePair>> form = new HashMap<>();
        Set<State> states = automaton.getStates();

        for (State firstState : states) {
            LinkedList<edgePair> edgePairs = new LinkedList<>();

            for (Transition transition1 : firstState.getTransitions()) {
                State middleState = transition1.getDest();
                if (!middleState.getTransitions().isEmpty()) {
                    for (Transition transition2 : middleState.getTransitions()) {
                        edgePair edgePair = new edgePair(firstState.getNumber(), transition1, transition2);
                        edgePairs.add(edgePair);
                    }
                }
            }

            form.put(firstState, edgePairs);
        }



        return form;
    }

    public static LinkedList<Path> generatePath(Automaton automaton,Map<State, LinkedList<edgePair>> map){
        LinkedList<edgePair> startPointLine = getEPPoint(automaton.getInitialState(),map);
        Path path = new Path();
        LinkedList<Path> EPC = new LinkedList<>();
        DFSearch(automaton,startPointLine,path,map,EPC);

        extendEPC(EPC,automaton);

        return EPC;
    }


    private static LinkedList<edgePair> getEPPoint(State state,Map<State, LinkedList<edgePair>> map){
        return map.get(state);
    }

    private static void DFSearch(Automaton automaton,LinkedList<edgePair> epp, Path path, Map<State, LinkedList<edgePair>> map, LinkedList<Path> EPC) {

        LinkedList<edgePair> ringRoads = findRingRoads(map);
        DFS(automaton,epp,null,path,map,EPC);
        path.clear();
        for (State state : map.keySet()) {
            LinkedList<edgePair> edgePairs = map.get(state);

            DFS(automaton,edgePairs,null,path,map,EPC);
        }

        clearRingRoad(EPC,ringRoads);
    }

    private static LinkedList<edgePair> findRingRoads(Map<State, LinkedList<edgePair>> map) {
        LinkedList<edgePair> ringRoads = new LinkedList<>();
        for (State state : map.keySet()) {
            LinkedList<edgePair> edgePairs = map.get(state);

            if(edgePairs == null)
                continue;
            for (edgePair edgePair : edgePairs) {
                if (edgePair.getNumber() == edgePair.getTransition2().getDest().getNumber()) {
                    ringRoads.add(edgePair);
                }
            }
        }
        return ringRoads;
    }

    private static void clearRingRoad(LinkedList<Path> EPC,LinkedList<edgePair> ringRoads) {
        boolean[] visited = new boolean[ringRoads.size()];

        for (Path path : EPC) {
            LinkedList<Transition> transitions = path.getTransitions();
            for (int i = 1; i < transitions.size(); i++) {
                edgePair pair;
                if (i == 1) {
                    pair = new edgePair(path.getInitial(), transitions.get(0), transitions.get(i));
                } else {
                    int initialNumber = transitions.get(i - 2).getDest().getNumber();
                    pair = new edgePair(initialNumber, transitions.get(i - 1), transitions.get(i));
                }
                for(edgePair r:ringRoads){
                    if(r.toString().equals(pair.toString())){
                        int index = ringRoads.indexOf(r);
                        if (visited[index]) {
                            transitions.remove(i);
                            transitions.remove(i - 1);
                        } else {
                            visited[index] = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void DFS(Automaton automaton,LinkedList<edgePair> epp,Transition tr2,Path path,Map<State, LinkedList<edgePair>> map,LinkedList<Path> EPC) {
        boolean flag = false;
        tagVisitPath(path,map);
        edgePair ep = detectRingEP(epp,tr2);
        if(ep != null){
            path = addPairToPath(ep,path);
            if(path.getLastState().isAccept()){
                Path newPath = path.deepClone();
                EPC.add(newPath);
            }
            flag = true;
            DFS(automaton,epp,ep.getTransition2(),path,map,EPC);
        }else {
            if(epp != null) {
                for (edgePair edgePair : epp) {
                    ep = edgePair;
                    if ((!ep.isVisited() && tr2 == null) || (!ep.isVisited() && ep.getTransition1().equals(tr2))) {
                        path = addPairToPath(ep, path);
                        if(path.getLastState().isAccept()){
                            Path newPath = path.deepClone();
                            EPC.add(newPath);
                        }
                        State dest = ep.getTransition1().getDest();
                        tr2 = ep.getTransition2();
                        LinkedList<edgePair> NP = getEPPoint(dest, map);
                        DFS(automaton,NP, tr2, path, map, EPC);
                        flag = true;
                        tr2 = path.getTransitions().getLast();
                        //break;
                    }
                }
            }

        }
        if(!path.isEmpty()){
            if (!flag){
                Path newPath = path.deepClone();
                EPC.add(newPath);
            }
            path.pop();
            if(path.getTransitions().isEmpty()){
                LinkedList<edgePair> NP = getEPPoint(automaton.getStateByNumber(path.getInitial()), map);
                path.clear();
                DFS(automaton,NP,null,path,map,EPC);
            }
        }
    }

    private static void tagVisitPath(Path path, Map<State, LinkedList<edgePair>> map) {
        String pathStr = path.toString();

        for (State state : map.keySet()) {
            LinkedList<edgePair> edgePairs = map.get(state);
            if(edgePairs == null)
                continue;
            for (edgePair edgePair : edgePairs) {
                if (edgePair.isVisited())
                    continue;
                if (pathStr.contains(edgePair.toString()))
                    edgePair.setVisited(true);
            }

        }
    }

    private static edgePair detectRingEP(LinkedList<edgePair> epp,Transition tr2) {
        edgePair result = null;
        if(epp == null) return null;

        if(tr2 == null){
            for (edgePair pair: epp) {

                if(pair.isVisited()) continue;
                if(pair.getNumber() == pair.getTransition2().getDest().getNumber()){
                    result = pair;
                    break;
                }
            }
        }else {
            for (edgePair pair: epp) {
                if(pair.isVisited()) continue;
                if(pair.getNumber() == pair.getTransition2().getDest().getNumber() && pair.getTransition1() == tr2){
                    result = pair;
                    break;
                }
            }
        }
        return result;
    }

    private static Path addPairToPath(edgePair pair, Path path) {
        if(path.isEmpty()){
            path.setInitial(pair.getNumber());
            path.getTransitions().add(pair.getTransition1());
            path.getTransitions().add(pair.getTransition2());
        }else {
            path.getTransitions().add(pair.getTransition2());
        }

        pair.setVisited(true);
        return path;
    }


}
