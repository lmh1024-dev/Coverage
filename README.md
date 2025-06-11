
# Coverage:a regular expression example string generator applied in the teaching field based on the coverage criterion of automata

This project is a Java based testing tool used to generate test strings that meet different regular expression coverage criteria. It provides four coverage testing methods to help developers and testers verify the implementation quality of regular expressions.


## Core functions
The usage methods of four coverage criteria have been pre written in the file CoverageApplicabilityTests.java


1. Edge Pair Coverage (EPC)
Example Regex: "a*|(a|bb)*"

Covers consecutive edge pairs

Creates test cases that traverse every edge pairs

2. Prime Path Coverage (PPC)
Example Regex: "(a((a|b)(a|b))*)|(b(a|b)((a|b)(a|b))*)"

Tests all prime paths

Provides the most comprehensive path coverage

3. Node Coverage (NC)
Example Regex: "a+b*"

Ensures all nodes in the regex finite automaton are visited

Generates test strings that cover every state

4. Edge Coverage (EC)
Example Regex: "(a(a*|b+a)*)|(b(b*|a+b)*)"

Validates all transitions between states

Creates test cases that traverse every edge

## Requirements
Java 17+

Maven 3.8+

Spring Boot 2.7+

Lombok

## Installation & Execution
1. Clone the repository:
```bash
  git clone https://github.com/lmh1024-dev/Coverage
```

2. Build the project:
```bash
  mvn clean package
```

3. Run all tests:
```bash
  mvn test
```

## Sample Output
```text
Regex:a*|(a|bb)*
EPC:
Positive Strings:[aa, bb, bba, , a, bbbb]
Negative Strings:[baba, baaa, babb, bab, b, aab, bbb, baa, ba, bbab]

Regex:(a((a|b)(a|b))*)|(b(a|b)((a|b)(a|b))*)
PPC:
Positive Strings:[baaa, abbba, ababb, aaaaa, aaaab, ababa, bbbbab, bbbbaa, bbab]
Negative Strings:[baaaa, , baaab, bbbba, bbabb, bbaba, abbbaa, abbbab, aaaa, abab]
...
```

## Customization

1. Open CoverageApplicationTests.java

2. Modify the regex variable in any test method

3. Save and rerun tests:
```bash
  mvn test
```
## License
Coverage is released under the under the terms of the BSD license. Copyright (C) Lixiao Zheng, Minghuang Lin
