rem generates JAXB java classes from xsd file
rem parameters: xsd-path, output-path
"%JAVA_HOME%\bin\xjc.exe" %1 -d %2
