
# A regular expression example string generator applied in the teaching field based on the coverage criterion of automata

This project is a Java based tool used to generate positive strings and negative strings that meet different regular expression coverage criteria. It provides four coverage methods to help users verify the implementation quality of regular expressions.

We traverse the DFA and their complement DFA of regular expressions to ensure the generation of positive and negative strings. Strong coverage criteria extending graph-based coverage metrics, including
node, edge, edge-pair and prime-path coverage, to enforce structural completeness and final-state validation in finite automata.


## Data
Experimental data and results are published in the file 'Data'.


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
Coverage is released under the terms of the BSD license. Copyright (C) Lixiao Zheng, Minghuang Lin
