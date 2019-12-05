# Ternary-Trie-Test-Cases
Personal Test cases for Purdue's Fall'19 CS251 (Data Structures) project 5, Ternary Tries.

Some projects for the course do not have public test cases that get released. This leaves many students struggling to understand
exactly where and why their code is failing. Since I tend to finish projects on the same day that they get released rather
than waiting until the end, I usually create and post public test cases on the course's online help board so that other students
don't have to encounter the same struggle that I did. These are one of such test cases.

11/25/2019 - Local test cases complete. Nothing special. Typical unit testing.

11/26/2019 - Started branching out the project. Creating client/server pair. At the end, the server should pass a solution
             instance to the client, so that students know exactly how an arbitrary solution trie should look. In particular,
             if they are failing a test case, it would allow them to analyze a specific instance of the solution trie Object by
             stepping through with a debugger.
             
End of 11/26/2019 - Finished branching out the project. Published the client on the online help board. Students will now have access to a solution trie via running testcasesAdvanced in conjunction with having testcasesClient.java.

11/27/2019 and beyond - Bugfixing, i.e. swapping from BufferedReader/Writer to ObjectInputStream/OutputStream due to BFR's issues accepting words with '\n' characters, adding comments, adding more words to the default test cases, and expanding functionality.
