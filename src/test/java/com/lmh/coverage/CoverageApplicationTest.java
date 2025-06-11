package com.lmh.coverage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CoverageApplicationTest {

    @Test
    void edgePairCoverage() {
        String regex = "a*|(a|bb)*";
        edgePairCoverage edgePairCoverage = new edgePairCoverage();
        edgePairCoverage.generate(regex);

        edgePairCoverage.generate();

        System.out.println("Regex:" + regex +"\nEPC:");
        System.out.println("Positive Strings:" + edgePairCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + edgePairCoverage.getNegativeStrings());

    }

    @Test
    void primeCoverage(){
        String regex = "(a((a|b)(a|b))*)|(b(a|b)((a|b)(a|b))*)";
        primePathCoverage primePathCoverage = new primePathCoverage();
        primePathCoverage.generate(regex);

        primePathCoverage.generate();

        System.out.println("Regex:" + regex +"\nPPC:");
        System.out.println("Positive Strings:" + primePathCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + primePathCoverage.getNegativeStrings());
    }

    @Test
    void nodeCoverage(){
        String regex = "a+b*";
        nodeCoverage nodeCoverage = new nodeCoverage();
        nodeCoverage.generate(regex);

        nodeCoverage.generate();

        System.out.println("Regex:" + regex +"\nNC:");
        System.out.println("Positive Strings:" + nodeCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + nodeCoverage.getNegativeStrings());
    }

    @Test
    void edgeCoverage(){
        String regex = "(a(a*|b+a)*)|(b(b*|a+b)*)";
        edgeCoverage edgeCoverage = new edgeCoverage();
        edgeCoverage.generate(regex);

        edgeCoverage.generate();

        System.out.println("Regex:" + regex +"\nEC:");
        System.out.println("Positive Strings:" + edgeCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + edgeCoverage.getNegativeStrings());
    }


}
