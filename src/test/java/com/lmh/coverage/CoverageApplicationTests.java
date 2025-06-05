package com.lmh.coverage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CoverageApplicationTests {

    @Test
    void edgePairCoverage() {
        String regex = "a*|(a|bb)*";
        edgePairCoverage edgePairCoverage = new edgePairCoverage();
        edgePairCoverage.generate(regex);

        edgePairCoverage.generate();

        System.out.println("Regex:" + regex +"EPC:");
        System.out.println("Positive Strings:" + edgePairCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + edgePairCoverage.getNegativeStrings());

    }

    @Test
    void primeCoverage(){
        String regex = "(001)(10101)(110)";
        primePathCoverage primePathCoverage = new primePathCoverage();
        primePathCoverage.generate(regex);

        primePathCoverage.generate();

        System.out.println("Regex:" + regex +"PPC:");
        System.out.println("Positive Strings:" + primePathCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + primePathCoverage.getNegativeStrings());
    }

    @Test
    void nodeCoverage(){
        String regex = "(a((a|b)(a|b))*)|(b(a|b)((a|b)(a|b))*)";
        nodeCoverage nodeCoverage = new nodeCoverage();
        nodeCoverage.generate(regex);

        nodeCoverage.generate();

        System.out.println("Regex:" + regex +"NC:");
        System.out.println("Positive Strings:" + nodeCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + nodeCoverage.getNegativeStrings());
    }

    @Test
    void edgeCoverage(){
        String regex = "(a(a*|b+a)*)|(b(b*|a+b)*)";
        edgeCoverage edgeCoverage = new edgeCoverage();
        edgeCoverage.generate(regex);

        edgeCoverage.generate();

        System.out.println("Regex:" + regex +"EC:");
        System.out.println("Positive Strings:" + edgeCoverage.getPositiveStrings());
        System.out.println("Negative Strings:" + edgeCoverage.getNegativeStrings());
    }


}
