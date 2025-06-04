package com.lmh.coverage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CoverageApplicationTests {

    @Test
    void edgePairCoverage() {
        String regex = "(a(a*|b+a)*)|(b(b*|a+b)*)";
        String attemptRegex = "a*a?|(a|b)bb*";
        edgePairCoverage edgePairCoverage = new edgePairCoverage();
        edgePairCoverage.generate(regex, attemptRegex);

        edgePairCoverage.generate();

        System.out.println(edgePairCoverage.getPositiveStrings());

        System.out.println(edgePairCoverage.getNegativeStrings());

    }

    @Test
    void primeCoverage(){
        String regex = "001|1010|110";
        String attemptRegex = "(001)(10101)(110)";
        primePathCoverage primePathCoverage = new primePathCoverage();
        primePathCoverage.generate(regex, attemptRegex);

        primePathCoverage.generate();

        System.out.println(primePathCoverage.getPositiveStrings());
        System.out.println(primePathCoverage.getNegativeStrings());
    }

    @Test
    void nodeCoverage(){
        String regex = "(a(a*|b+a)*)|(b(b*|a+b)*)";
        String attemptRegex = "(a((a|b)(a|b))*)|(b(a|b)((a|b)(a|b))*)";
        nodeCoverage nodeCoverage = new nodeCoverage();
        nodeCoverage.generate(regex, attemptRegex);

        nodeCoverage.generate();

        System.out.println(nodeCoverage.getPositiveStrings());
        System.out.println(nodeCoverage.getNegativeStrings());
    }

    @Test
    void edgeCoverage(){
        String regex = "(a(a*|b+a)*)|(b(b*|a+b)*)";
        String attemptRegex = "((a|b)*((aa)|(ba)|(bb)))|((a|b)?)";
        edgeCoverage edgeCoverage = new edgeCoverage();
        edgeCoverage.generate(regex, attemptRegex);

        edgeCoverage.generate();

        System.out.println(edgeCoverage.getPositiveStrings());
        System.out.println(edgeCoverage.getNegativeStrings());
    }


}
