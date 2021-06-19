<!--
Copyright (C) 2021 Rafael Ribeiro, Xavier Pisco, Diogo Rodrigues, João António Sousa
Distributed under the terms of the GNU General Public License, version 3
-->

# JMM (Java-- compiler)

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![build](https://github.com/dmfrodrigues/feup-comp-proj/actions/workflows/build.yml/badge.svg)](https://github.com/dmfrodrigues/feup-comp-proj/actions/workflows/build.yml)
[![test](https://github.com/dmfrodrigues/feup-comp-proj/actions/workflows/test.yml/badge.svg)](https://github.com/dmfrodrigues/feup-comp-proj/actions/workflows/test.yml)

- **Project name:** JMM, a Java-- mini-compiler
- **Short description:** A compiler for the Java-- language
- **Environment:** Gradle, JVM
- **Tools:** Java, Jasmin
- **Institution:** [FEUP](https://sigarra.up.pt/feup/en/web_page.Inicial)
- **Course:** [COMP](https://sigarra.up.pt/feup/en/UCURR_GERAL.FICHA_UC_VIEW?pv_ocorrencia_id=459486) (Compilers)
- **Project grade:** 19.57/20.00
- **Group members:**
    - [Rafael Soares Ribeiro](https://github.com/up201806330) (<up201806330@edu.fe.up.pt>)
    - [Xavier Ruivo Pisco](https://github.com/Xavier-Pisco) (<up201806134@edu.fe.up.pt>)
    - [Diogo Miguel Ferreira Rodrigues](https://github.com/dmfrodrigues) (<dmfrodrigues2000@gmail.com>)
    - [João António Cardoso Vieira e Basto de Sousa](https://github.com/JoaoASousa) (<up201806613@edu.fe.up.pt>)
    
### Contributions

| Name | Number | Self Assessment | Contribution |
| ---- | ------ | --------------- | ------------ |
| Diogo Miguel Ferreira Rodrigues | 201806429 | 20 | 25% |
| João António Cardoso Vieira e Basto de Sousa | 201806613 | 20 | 25% |
| Rafael Soares Ribeiro | 201806630 | 20 | 25% |
| Xavier Ruivo Pisco | 201806134 | 20 | 25% |

### Summary
A *Java--* mini-compiler that handles generation of *.class* files compatible with *Java*.
Parses text files using *javacc* and the *Jmm grammar*, handles semantic and syntactic errors and performs some optimizations at the machine code level

### Dealing with syntatic errors

When the compiler encounters a syntatic error, at the level of the while expressions. That is, if an error is found,
a report is generated, indicating the line where it occurred, and the parser proceeds.
This can be seen in the `CompleteWhileTest.jmm` file

### Semantic analysis

All the semantic rules implemented follow below:

#### Symbol Table
- global: inclui info de imports e a classe declarada :white_check_mark:
- classe-specific: inclui info de extends, fields e methods :white_check_mark:
- method-specific: inclui info dos arguments e local variables :white_check_mark:
- retorno do SemanticsReport
    - permite consulta da tabela por parte da análise semantica :white_check_mark:
    - permite impressão para fins de debug :white_check_mark:
- small bonus: permitir method overload (i.e. métodos com mesmo nome mas assinatura de parâmetros diferente) :white_check_mark:

#### Expression Analysis

##### Type Verification

**Type Verification**
- verificar se operações são efetuadas com o mesmo tipo (e.g. int + boolean tem de dar erro) :white_check_mark:
- não é possível utilizar arrays diretamente para operações aritmeticas (e.g. array1 + array2) :white_check_mark:
- verificar se um array access é de facto feito sobre um array (e.g. 1[10] não é permitido) :white_check_mark:
- verificar se o indice do array access é um inteiro (e.g. a[true] não é permitido) :white_check_mark:
- verificar se valor do assignee é igual ao do assigned (a_int = b_boolean não é permitido!) :white_check_mark:
- verificar se operação booleana (&&, < ou !) é efetuada só com booleanos :white_check_mark:
- verificar se conditional expressions (if e while) resulta num booleano :white_check_mark:

##### Method Verification

**Method Verification**
- verificar se o "target" do método existe, e se este contém o método (e.g. a.foo, ver se 'a' existe e se tem um método 'foo') :white_check_mark:
    - caso seja do tipo da classe declarada (e.g. a usar o this), se não existir declaração na própria classe: se não tiver extends retorna erro, se tiver extends assumir que é da classe super :white_check_mark:
    - caso o método não seja da classe declarada, isto é uma classe importada, assumir como existente e assumir tipos esperados. (e.g. a = Foo.b(), se a é um inteiro, e Foo é uma classe importada, assumir que o método b é estático (pois estamos a aceder a uma método diretamente da classe), que não tem argumentos e que  type is undefined :white_check_mark: 
- Cannot access property of primitive type :white_check_mark:

##### Extras

- Variable already defined in the scope
- Static function declaration and verification in invocation
- Function overloading and verification
- Return type mismatch
- Variables must be declared before used
- Variables must be initialized before used
- Array declare size must be int
- this cannot be used in a static context
- since all fields are non static, cannot access fields in static functions
- Variable type is undefined
- Cannot access property of primitive type

#### Jasmin Generation
- estrutura básica de classe (incluindo construtor <init>)
- estrutura básica de fields
- estrutura básica de métodos (podem desconsiderar os limites neste checkpoint: limit_stack 99, limit_locals 99)
- assignments
- operações aritméticas (com prioridade de operações correta)
    - neste checkpoint não é necessário a seleção das operações mais eficientes mas isto será considerado no CP3 e versão final
- invocação de métodos

#### CODE GENERATION:
*Jasmin* is used to generate JVM Bytecodes from the OLLIR (generated previously from the AST).
In this phase, some optimzations were implemented (some executed after the AST generation and some after the OLLIR generation), as follows:

**Optimizations**
- Better while structure (too many jumps)
- If and while dead code removal :white_check_mark:
- iinc optimization (e.g. x = x + 2) :white_check_mark:
- Temporary variables are being stored needlessly
- Constructor optimization (use dup when it is being assigned e.g. Test a = new Test(); )
- If optimizations (no &&, só faz ifeq se não souber que o operador é true (inicialmente, verificar só se é um literal 'true' :white_check_mark: )
- No constant propagation, remover dead assignments :white_check_mark:
- Constant folding on all expressions (is deleting dead assignments as well) :white_check_mark:
- division and multiplication by powers of 2 replaced with shifts :white_check_mark:
- i < 0 => usar 'load i => if_lt' em vez de 'load i=>iconst_0=>if_icmpeq' :white_check_mark:

#### Task distribution

##### Checkpoint 1

1. Develop a parser for Java-- using JavaCC and taking as starting point the Java-- grammar furnished (note that the original grammar may originate conflicts when implemented with parsers of LL(1)type and in that case you need to modify the grammar in order to solve those conflicts); **Rafael e Xavier**
2. Include error treatment and recovery mechanisms for while conditions; **Diogo**
3. Proceed with the specification of the file jjt to generate, using JJTree, a new version of the parser including in this case the generation of the syntax tree (the generated tree should be an AST), annotating the nodes and leafs of the tree with the information (including tokens) necessary to perform the subsequent compiler steps; **João e Xavier**

##### Checkpoint 2

4. Implement the interfaces that will allow the generation of the JSON files representing the source code and the necessary symbol tables; **João**
5. Implement the Semantic Analysis and generate the LLIR code, OLLIR, from the AST; **Diogo, João, Rafael e Xavier**
6. Generate from the OLLIR the JVM code accepted by jasmin corresponding to the invocation of functions in Java--; **Diogo, João, Rafael e Xavier**
7. Generate from the OLLIR JVM code accepted by jasmin for arithmetic expressions; **Diogo, João, Rafael e Xavier**

##### Checkpoint 3

8. Generate from the OLLIR JVM code accepted by jasmin for conditional instructions (if and if-else); **João**
9. Generate from the OLLIR JVM code accepted by jasmin for loops; **João**
10. Generate from the OLLIR JVM code accepted by jasmin to deal with arrays. **Diogo**
11. Complete the compiler and test it using a set of Java-- classes; **Rafael e Xavier**

##### Final delivery

12. Proceed with the optimizations related to the code generation, related to the register allocation (“-r” option) and the optimizations related to the “-o” option **Diogo, Rafael e Xavier**

#### Pros

Does thorough analysis on every stage and takes advantage of clever tricks to save instructions (e.g. `boolean x = 10 < 11;` is done with by checking the 2's complement sign bit of the difference of the two values, so there's no need for a jump)

#### Cons

We couldn't do as many optimizations in the code generation as we wanted.