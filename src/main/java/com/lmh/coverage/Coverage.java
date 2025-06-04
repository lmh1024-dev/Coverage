package com.lmh.coverage;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;

import java.util.LinkedList;

import static com.lmh.coverage.utils.myBasicOperations.myComplement;

public class Coverage {
    private Automaton automaton;

    private Automaton attemptAutomaton;

    private String positiveStrings;

    private String negativeStrings;



    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

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


    public void generate(String regex,String attemptRegex){
        RegExp regexp = new RegExp(regex);
        Automaton automaton = regexp.toAutomaton();
        automaton.determinize();
        automaton.minimize();

        RegExp attemptRegexp = new RegExp(attemptRegex);
        Automaton attemptAutomaton = attemptRegexp.toAutomaton();
        attemptAutomaton.determinize();
        attemptAutomaton.minimize();

        this.setAutomaton(automaton);
        this.setAttemptAutomaton(attemptAutomaton);

    }


}
