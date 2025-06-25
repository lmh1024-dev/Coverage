
# SCREG: A tool for generating positive and negative example strings form regular experssion

This is a tool that generates a set of representative positive (matching) and negative (nonmatching) example strings from a given RE. The set of strings satisfies strong edge-pair coverage for the RE's DFA and its complement. This tool can be used to assisst CS students in Learning REs.


## Requirements
Java 17+

Maven 3.8+

Spring Boot 2.7+

Lombok

## Installation & Execution
1. Clone the repository:
```bash
  git clone https://github.com/lmh1024-dev/SCREG
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
Coverage is released under the terms of the BSD license. 
