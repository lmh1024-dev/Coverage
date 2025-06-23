
# SCREG: A regular expression example string generator applied in the teaching field based on the edge-pair coverage criterion of automata

This project is a Java based tool used to generate positive strings and negative strings. It help users verify the implementation quality of regular expressions.

We traverse the DFA and their complement DFA of regular expressions to ensure the generation of positive and negative strings. Strong edge-pair coverage criteria extending graph-based coverage metrics.

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
Positive Strings:[aa, bb, bba, , a, bbbb]
Negative Strings:[baba, baaa, babb, bab, b, aab, bbb, baa, ba, bbab]

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
