@echo off
del /q /f database-diagram.pdf
java -classpath ..\_schemacrawler\lib\schemacrawler-8.9.jar;..\_schemacrawler\lib\hsqldb-2.2.6.jar schemacrawler.Main -c hsqldb -infolevel=maximum -command=graph -outputformat=pdf -outputfile=database-diagram.pdf
echo Database diagram is in database-diagram.pdf