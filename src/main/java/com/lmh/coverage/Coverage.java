package com.lmh.coverage;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

public class Coverage {

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

    }


}
