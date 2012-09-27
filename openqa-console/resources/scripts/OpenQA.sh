LD_LIBRARY_PATH=`pwd`/lib
export LD_LIBRARY_PATH
export CLASSPATH="$CLASSPATH:LD_LIBRARY_PATH:.:./lib:openqa-console.jar"
java -Xms512m -Xmx1024m org.openqa.console.OpenEphyra