package com.lmh.coverage;

import com.lmh.coverage.entity.Path;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.*;

import static com.lmh.coverage.utils.myBasicOperations.*;
import static com.lmh.coverage.utils.myBasicOperations.boardProcess;


public class primePathCoverage extends Coverage{

    public void generate(){
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
            LinkedList<Path> PPC = generatePath(this.getAttemptAutomaton());

            stringsList = generateStrings(PPC);

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
    private static void getSimplePath(State s, boolean[] visited, Path simple, LinkedList<Path> simplePaths) {
        for(Transition t:s.getTransitions()){
            int number = t.getDest().getNumber();
            if(!visited[number]){
                visited[number] = true;
                simple.push(t);
                if(number == s.getNumber()){
                    simplePaths.add(simple.deepClone());
                    simple.pop();
                    visited[number] = false;
                }else {
                    getSimplePath(t.getDest(),visited,simple,simplePaths);

                    simplePaths.add(simple.deepClone());
                    simple.pop();
                    visited[number] = false;
                }

            }

        }

    }
    private static void remove(LinkedList<Path> simplePaths){
        LinkedList<Path> removePaths = new LinkedList<>();
        LinkedList<Path> addPaths = new LinkedList<>();
        for(Path sp:simplePaths){
            LinkedList<Transition> trs = sp.getTransitions();

            for(Transition t:trs){
                int i = trs.indexOf(t);
                if(t.getMax() != t.getMin()){
                    char c = t.getMin();
                    while ((int)c <= (int)t.getMax()){
                        Path newPath = sp.deepClone();
                        Transition newTr = new Transition(c, t.getDest());
                        newPath.getTransitions().remove(i);
                        newPath.getTransitions().add(i,newTr);
                        addPaths.add(newPath);
                        c = (char)((int)c+1);
                    }
                    removePaths.add(sp);
                    break;
                }
            }
        }
        simplePaths.removeAll(removePaths);
        simplePaths.addAll(addPaths);
        boolean flag = removePaths.isEmpty() && addPaths.isEmpty();

        if(!flag)
            remove(simplePaths);

    }
    static LinkedList<Path> generatePath(Automaton automaton){
        Set<State> states = automaton.getStates();
        LinkedList<Path> simplePaths = new LinkedList<>();
        for(State s:states){
            boolean[] visited = new boolean[states.size()];
            Path simple = new Path(s.getNumber());
            getSimplePath(s,visited,simple,simplePaths);
        }


        remove(simplePaths);
        LinkedList<Path> primePaths = new LinkedList<>();

        for(int i = 0;i < simplePaths.size();i++){
            Path sub = simplePaths.get(i);
            boolean flag = true;
            for(int j = 0;j < simplePaths.size();j++){
                if(i == j)
                    continue;
                Path main = simplePaths.get(j);
                if(main.toString().contains(sub.toString())){
                    flag = false;
                    break;
                }

            }
            if(flag){
                primePaths.add(sub);
            }
        }
        boolean changed;
        do{
            changed = false;
            for (int i = 0; i < primePaths.size(); i++) {
                Path p1 = primePaths.get(i);
                for (int j = 0;j < primePaths.size(); j++) {
                    if(i == j)
                        continue;
                    Path p2 = primePaths.get(j);

                    Path p3 = merge(p1,p2);
                    if (!p3.equals(p1)) {
                        primePaths.remove(i);
                        changed = true;
                        break;
                    }
                }
                if (changed) break;
            }

        }while (changed);

        extendEPC(primePaths,automaton);

        return primePaths;
    }

    private static Path merge(Path p1, Path p2){
        Integer initial = p1.getInitial();
        LinkedList<Transition> trs1 = p1.getTransitions();
        LinkedList<Transition> trs2 = p2.getTransitions();

        int index = -1;
        for(Transition t:trs2){
            if(t.getDest().getNumber() == initial){
                index = trs2.indexOf(t);
                break;
            }
        }
        if(index == -1)
            return p1;
        else {
            boolean flag = true;
            for(int i = index + 1, j = 0;i < trs2.size() && j < trs1.size();i++,j++){
                if(!trs2.get(i).equals(trs1.get(j))){
                    flag = false;
                    break;
                }
            }
            if(flag){
                int size = trs2.size()  - 1 - index;
                for(int i = size ;i < trs1.size();i++){
                    p2.push(trs1.get(i));
                }
            }else {
                return p1;
            }
        }
        return p2;
    }

}