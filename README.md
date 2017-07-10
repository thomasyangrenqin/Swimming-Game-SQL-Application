
## Swimming Game SQL Application
---
![Swim SQL](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/sample.png)

This repository contains all detailed components of the final project of the Course COMP533, Intro to DataBase System, at Rice.

 The course covered a range of DataBase related topics, including relational and other DataBase Systems, SQL Programming, DataBase Application Programming and DataBase Design.
 
### About The Project
---
This was a teamwork project, which consisted of plenty of DataBase schema designs, SQL programmings and DataBase application programmings. I teamed up with one of my classmates in this course, and we finished the whole project together.

Basically, our task was to design a DataBase System supporting the Swimming Game, whose ER design([Swim ER design](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/swim%20ERD.pdf)) was completed as last homework assignments, and relative DataBase Application.

We chose encapsulate all DataBase relating tasks in one .sql file. This includes code to initially create the schema’s tables, all the business logic, i.e., CRUD operations, data integrity, and the required queries that the application supports, will be packaged in functions in this file.

On the other hand, we used JFrame to implement a simple GUI which is able to support all the functions in the .sql file, but not able to modify any part of our Swim DataBase.

The detailed requirements and descriptions about this project, please refer to: [Project Descriptions](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/Assignment%206%20%281%29.pdf)


### Installation
---
We designed and implemented our project on PostgreSQL, therefore you need to have PostgreSQL to assess our Applications and DataBase System in the very first place. 

Then you can load our .sql file([533Project.sql](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/533project.sql)) from the command line: psql -f filename.sql, or from within psql: \i filename.sql. Then all you need to is start our application by the following:
To compile the project:
```
javac Menu.java
```
To execute the project:
```
java Menu
```

The main java class 'Menu'([Menu.java](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/Menu.java)) imported and used all other supporting java class, like 'DBConnect'([DBConnect.java](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/DBConnection.java)) connecting to PostgreSQL by JDBC library, 'CSVparser'([CSVparser.java](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/CSVparser.java)) parsing the original data source csv file, 'DBInsertion'([DBInsertion.java](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/DBInsertion.java)) inserting all data parsed by the parser into the DataBase, 'SQLExecutor'([SQLExecutor.java](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/SQLExecutor.java)) performing all needed SQL operations by calling functions in the .sql file.

I also included a simple data source file [533.csv](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/533.csv), which the 'CSVparser' will take as input.


### Test Cases and Other Issue Descriptions
 ---
 We created a few test cases and talked about some potential issues and its solutions. Please refer the following two files:
 * [readme_1](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/README.pdf)
 * [readme_2](https://github.com/thomasyangrenqin/Swimming-Game-SQL-Application/blob/master/README)
