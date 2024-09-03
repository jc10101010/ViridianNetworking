find -name "*.java" > javafiles.txt
javac.exe @javafiles.txt
java.exe -cp "src" src/client/MPClient.java