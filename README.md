# CS 122B Project 1
**Features a Movie List page that contains movies sorted by rating. Able to direct to an individual movie or stars information.**
---
[*Demo Video*](https://www.youtube.com/watch?v=SlJ1oaUDXM4)

**Contributions**
---
Davy Nguyen: Single Movie page, Movie List page, demo

Pearl Nguyen: create_table.sql, Single Star page, Movie List page

Both members were in contact and transferring files to each other throughout the whole project

# CS 122B Project 2
**Updated the MovieList, Single-Star, and Single-Movie to include a search bar, checkout option, and "Add to Cart" button. Included a login page and a main page that allows the user to browse genres and letters for specific movies, or to search in the search bar for specific title, director, year, and star filter. Included filters for the Movie List page for limits to the movies per page and sorting the rating/titles, as well as being able to move to the next or previous page. Our website now has a shopping cart (and shopping cart page), payment page, and confirmation of payment page.**
---
[*Demo Video*](https://youtu.be/PjfuQXwfFW4)

**Contributions**
---
Davy Nguyen: Main page, Browsing and Searching features, Payment and Confirmation page, Website design

Pearl Nguyen: Movie List updates, Single Movie updates, Single Star updates, Shopping Cart features and page, Insert into Sales table

Both members were in contact and transferring files to each other throughout the whole project

**Substring Matching Design**
---
`
if (letter != null && !letter.isEmpty() && !letter.equalsIgnoreCase("null") && !letter.equals("*")) {
    query = query.concat(" AND updatedTable.title LIKE '" + letter + "%'");
} else if (letter.equals("*")) {
    query = query.concat(" AND updatedTable.title REGEXP '^[^0-9A-Za-z]'");
}
`
The substring matching for movie title, genre, movie director, movie year, and star names are the same as letter but replaced with the respective search.

# CS 122B Project 3
**Incorporate an employee dashboard and login that allows them to add stars, movies, and genrse. Incorporated recaptcha to our logins. Updated all statements to be PreparedStatements, and updated our url to include "https". Parsed xml files and added movies, stars, genres, stars in movies, and genres in movies to our existing tables.**
---
[*Demo Video*](https://youtu.be/fIVJtmGlpU0)

**Contributions**
---
Davy Nguyen: Recaptcha, HTTPS, employee dashboard, Stored procedure

Pearl Nguyen: Recaptcha, Updating PreparedStatements, XML Parsing

Both members were in contact and transferring files to each other throughout the whole project

**PreparedStatements**
--- 
Files: MovieServlet.java, LoginServlet.java, PaymentServlet.java, SingleMovieServlet.java, SingleStarServlet.java

**Parsing Time Optimization**
---
Two strategies-
1. Using batch queries instead of querying and inserting every line once I receive the information. This was slowing down our parsing because it was doing a database round trip at every call, so to optimize it we did a batch of queries so it does specific amounts of round trips for less and inserts batches.
2. Making a "local database". By calling the query once for collecting all data from stars, movies, genres, etc. and storing the desired information in Lists/ArrayLists that we can reference, we cut times down greatly because we were no longer calling to the database everytime we needed a single row of infomation. We could instead reference something on memory. 

Our naive approach took about 20 minutes and it consisted of calling multiple times to the database throughout the parsing repeatedly, and continuously connected to our database which kept slowing it down because it kept making connections. We were also inserting at every call rather than doing batches and prepared statements repeatedly rather than making one overall statement. We were able to cut down our times greatly by doing our two strategies above and doing small coding optimizations overall.

**Inconsistent Data Reports**
---
This is what we considered inconsistencies: Invalid tags (incorrectly formatted years and genres that aren't valid, see below for valid genres), movies with no genres, duplicate movies and stars, and stars or movies that don't exist.
Valid genres consisted of this table from the documentation: 

![image](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/4a8b0526-790a-454a-a2c6-24cf9c84492b)

Reports are in the text files below:

[DuplicateStars.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473333/DuplicateStars.txt)

[DuplicateStarsInMovies.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473334/DuplicateStarsInMovies.txt)

[InvalidTagsForMovies.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473335/InvalidTagsForMovies.txt)

[MovieDuplicates.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473336/MovieDuplicates.txt)

[NoGenre.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473337/NoGenre.txt)

[StarMovieDoesNotExist.txt](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/files/11473338/StarMovieDoesNotExist.txt)

# CS 122B Project 4
**Update the search bar for title to include Full-Text Search and Autocomplete suggestion. Create and implement an Android version.**
---
[*Demo Video*](https://youtu.be/KmS_ye-AHvI)

**Contributions**
---
Davy Nguyen: Android Version

Pearl Nguyen: Full-Text Search, Autocomplete search

Both members were in contact and transferring files to each other throughout the whole project

# CS 122B Project 5
- # General
    - #### Team#: s23-122b-lala_davy
    
    - #### Names: Davy Nguyen, Pearl Nguyen
    
    - #### Project 5 Video Demo Link: [*Demo Video*](https://youtu.be/XCyulDI7G0I)

    - #### Instruction of deployment:
    1. Setup 5 instances: 1 being on Google Cloud Platform, 4 being on aws. 1 should be a load balancer and 2 should be a master/slave pair. 1 being the original fablix instance.
    2. On the master instance:
						master-shell> sudo mysql -u root -p
						master-mysql> create user 'repl'@'%' identified WITH mysql_native_password by 'slave66Pass$word';
						master-mysql> grant replication slave on *.* to 'repl'@'%' ;
						master-mysql> show master status;
    3. On the slave instance: 
						Secondary-shell> sudo apt-get update
						Secondary-shell> sudo apt-get install mysql-server
						Secondary-shell> sudo service mysql restart
						Secondary-shell> sudo mysql -u root -p
						Secondary-mysql> CHANGE MASTER TO MASTER_HOST='172.2.2.2', MASTER_USER='repl', MASTER_PASSWORD='slave66Pass$word', MASTER_LOG_FILE='mysql-bin.000001',                                 MASTER_LOG_POS=337;
                        * MASTER_HOST becomes the private ip of the master instance, master_log_file and master_log_pos should match the ones given by show master stats; *
						Secondary-mysql> start slave;
						Secondary-mysql> show slave status;
    4. Setup load balancers using:
        - sudo vim /etc/apache2/sites-enabled/000-default.conf

        - Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED

        - <Proxy "balancer://Fablix_balancer">
            BalancerMember "http://172.2.2.2:8080/cs122b-project2/" route=1
            BalancerMember "http://172.3.3.3:8080/cs122b-project2/" route=2
          ProxySet stickysession=ROUTEID </Proxy>
        - ProxyPass /cs122b-project2 balancer://Fablix_balancer
        - ProxyPassReverse /cs122b-project2 balancer://Fablix_balancer
        * Ip addresses replaced by your private ip for both master and slave. *
    5. Install JMeter (https://jmeter.apache.org/download_jmeter.cgi) and carry out load tests.
    - #### Collaborations and Work Distribution:
Davy Nguyen: Connection Pooling, Master, Slave and Balancer Creation, JMeter Setup

Pearl Nguyen: Connection Pooling, Master, Slave and Balancer Creation, JMeter Setup

Both members were in contact and worked on project together. 

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    WebContent/META-INF/context.xml
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    With connection pooling, previous database requests are cached which would end up making Fablix faster. 
    - #### Explain how Connection Pooling works with two backend SQL.
    With two backend SQLs, when one backend is affected, the other is affected as well, so although they're seperate, they would share the data.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    WebContent/META-INF/context.xml
    WebContent/WEB-INF/web.xml
    - #### How read/write requests were routed to Master/Slave SQL?
    In both the GCP Load Balancer and AWS Load Balancer, both would make requests to either the master or slave sql depending on how busy either one is.
    The Load Balancers didnt have the fablix application themselves, but relied on routing to either one to make their requests.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
cd into the proper folder containing the log_processing.py. Once in the directory, put log_processing.py followed by any amount of file names.
Ex: log_processing.py aws_master.txt aws_slave.txt

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![Single-WithPool-1Thread](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/eb0de017-aeb9-404b-a677-5aacd18b4767)   | 1.9                        | 404.382183309236                    | 403.60324686586347        | The HTTP 1 thread had the fastest average query time and the fastest average search servlet time and average jdbc time.           |
| Case 2: HTTP/10 threads                        | ![Single-WithPool-10Threads](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/f90918ec-9838-4ad2-9996-9cef4b93e90c)   | 2.0                        | 662.4932179375951                   | 661.8827026529681         | The HTTP 10 thread was neither the fastest nor the slowest and didn't seem to have too big of a difference in comparison to HTTPS in terms of time.           |
| Case 3: HTTPS/10 threads                       | ![HTTPS-WithPool-10Threads](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/5e5e40d4-7630-4b6e-b3c7-274566ee1369)   | 2.0                        | 659.5611738405104                   | 658.630932784689          | The HTTPS 10 thread had the longest average search servlet time and average jdbc time, but had no other big difference in comparison to HTTP in terms of time.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![Single-NoPool-10Threads-Screenshot](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/4678f710-d565-4344-ae4a-94a9afd03e50)  | 2.1                        | 487.690966469187                    | 485.87223740896356        | The HTTP 10 threads no connection pooling had the slowest average query time.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![Scaled-WithPool-1Thread](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/c2ca2584-86e8-4dd0-a089-aa74a5a20c98)   | 1.8                        | 405.7524224432433                   | 404.59056211351356        | The HTTP 1 thread was faster with scaling than the HTTP 1 thread without scaling, and was the fastest average query time, average search servlet time, and average jdbc time between both scaled and not scaled.           |
| Case 2: HTTP/10 threads                        | ![Scaled-WithPool-10Threads](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/d96c2c57-1d33-4831-877d-95efc6737deb)  | 2.0                        | 516.7963612016806                   | 516.1472263627451         | The average search servlet time and average jdbc time was faster when scaled than in comparison to no scaling, but had the slowest average search servlet time and average jdbc time in comparison to the other scaled test plans.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![Scaled-NoPool-10Threads-Screenshot](https://github.com/UCI-Chenli-teaching/s23-122b-lala_davy/assets/57478320/bf719f28-935a-4d0f-8970-49597ffeabdb)   | 2.0                        |464.12290692233634                   | 462.88118450513476        | The average search servlet time and average jdbc time was faster when scaled than in comparison to no scaling.           |

