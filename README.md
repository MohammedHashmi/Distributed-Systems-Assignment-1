Mohammed Adnan Hashmi 100753115 Distributed Assignment 1

The application idea behind the assignment was to create a Jeopardy-style game where a local server would be created to host an array of questions ranging from three different categories that the players on the client side can answer. Any players can enter the client, which is recorded on the server side. This application/game aims to answer the most questions correctly and have a higher score than the other players connected.

To run this file you will need two java files saved onto your desktop:

QuizServer QuizClient Once you have saved the two files, open up a cmd window and locate the two files onto your cmd line. You will first have to compile the two files using:

javac QuizClient.java QuizServer.java

once you have compiled the two files, use java QuizServer to run the server first and then java QuizClient to run the client right after. Once you have ran the server, you will be able to run any amount of java clients to connect to the server.
