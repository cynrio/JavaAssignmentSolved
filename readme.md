# Instructions about codebase

Package `com.maybank` contains the Java files as per the given questions list. Implementation described bellow. 

## Question 1: `ArrArgs` 
Code is refactored and optimized.

## Question 2: `PermisTest` should be run using terminal:
### Attempt 1:
command: `java -Djava.security.manager PermisTest`

output

``` WARNING: A command line option has enabled the Security Manager
WARNING: The Security Manager is deprecated and will be removed in a future release
Exception in thread "main" java.security.AccessControlException: access denied ("java.util.PropertyPermission" "user.home" "read")
at java.base/java.security.AccessControlContext.checkPermission(AccessControlContext.java:485)
at java.base/java.security.AccessController.checkPermission(AccessController.java:1068)
at java.base/java.lang.SecurityManager.checkPermission(SecurityManager.java:416)
at java.base/java.lang.SecurityManager.checkPropertyAccess(SecurityManager.java:1160)
at java.base/java.lang.System.getProperty(System.java:929)
at PermisTest.main(PermisTest.java:8)
```

### Attempt 2:

First, create policy file `permisTest.policy` to grant permissions. Then run the following command:

`java -Djava.security.manager -Djava.security.policy=permisTest.policy PermisTest`

output: 

```
WARNING: A command line option has enabled the Security Manager
WARNING: The Security Manager is deprecated and will be removed in a future release
/Users/faisal
Error java.io.FileNotFoundException: input.txt (No such file or directory)
```

### Attempt 3 (created input.txt file in home dir):

`java -Djava.security.manager -Djava.security.policy=permisTest.policy PermisTest`

```
WARNING: A command line option has enabled the Security Manager
WARNING: The Security Manager is deprecated and will be removed in a future release
/Users/faisal
The two nos are : 5, 10
The total sum value is 15
```

It created `output.txt` file in home directory with value: `15`

## Question 3: `OutOfMemoryError`

Error reproduced and solved with GC and without GC approaches.

## Question 4: Implemented `RestaurantMenu` under package `resturantMenu`

Implemented as per the requirements.

## Question 5: spring boot app `hello` is provided separately. 

You can run that solution independently. Database `maybank-db` should be created beforehand.

## Question 6: Implemented `CustomerAddressApp` under package `customerApp`

This app is following MVC structure and provides the required features. 
`scripts.sql` is provided for creating database and tables required for this app. 
If you want to use your own credentials, you can update `DBConnection.java` file.

