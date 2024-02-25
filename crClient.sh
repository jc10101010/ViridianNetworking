find -name "*.java" > javafiles.txt
javac @javafiles.txt
java -cp "src" src/client/MPClient.java