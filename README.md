# Java-CPU-Emulator
This is a basic CPU emulator software. It loads program code from text file.

### Run
./java source input.txt

### Example
Following example code is an app that can compute the sum of the numbers between 0 and 20.

0. START
1. LOAD 20
2. STORE 200
3. LOAD 0
4. STORE 201
5. STORE 202
6. CMPM 200
7. CJMP 15
8. LOADM 202
9. ADDM 201
10. STORE 202
11. LOADM 201
12. ADD 1
13. STORE 201
14. JMP 6
15. LOADM 202
16. DISP
17. HALT
