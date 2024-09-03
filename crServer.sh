find -name  "*.class" -exec rm {} +
find -name "*.java" > javafiles.txt
javac.exe @javafiles.txt
java.exe -cp "src" src/server/MPServer.java