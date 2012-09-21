mvn install:install-file -Dfile=bing-search-java-sdk.jar -DgroupId=com.google.search -DartifactId=bing -Dversion=2.0.176 -Dpackaging=jar

mvn install:install-file -Dfile=googleapi.jar -DgroupId=com.google.soap  -DartifactId=api -Dversion=beta -Dpackaging=jar

mvn install:install-file -Dfile=yahoosearch.jar -DgroupId=com.yahoo     -DartifactId=search -Dversion=beta -Dpackaging=jar

#mvn install:install-file -Dfile=minorthird_20080611.jar -DgroupId=edu.cmu    -DartifactId=minorthird -Dversion=20080611 -Dpackaging=jar
mvn install:install-file -Dfile=minorthird.jar -DgroupId=edu.cmu    -DartifactId=minorthird -Dversion=12.7.5.31 -Dpackaging=jar

mvn install:install-file -Dfile=jwnl.jar -DgroupId=net.didion     -DartifactId=jwnl -Dversion=1.4 -Dpackaging=jar

mvn install:install-file -Dfile=lingpipe.jar -DgroupId=com.aliasi     -DartifactId=lingpipe -Dversion=3.3.0 -Dpackaging=jar

mvn install:install-file -Dfile=snowball.jar -DgroupId=org.tartarus -DartifactId=snowball -Dversion=1 -Dpackaging=jar

mvn install:install-file -Dfile=stanford-ner.jar -DgroupId=edu.stanford.nlp     -DartifactId=stanford-ner -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=stanford-parser.jar -DgroupId=edu.stanford.nlp     -DartifactId=stanford-parser -Dversion=1.6 -Dpackaging=jar

mvn install:install-file -Dfile=stanford-postagger.jar -DgroupId=edu.stanford.nlp    -DartifactId=stanford-postagger -Dversion=2006-05-21 -Dpackaging=jar

mvn install:install-file -Dfile=javelin.jar -DgroupId=edu.cmu     -DartifactId=javelin -Dversion=2007-07-22 -Dpackaging=jar

mvn install:install-file -Dfile=maxent.jar -DgroupId=opennlp.maxent     -DartifactId=maxent -Dversion=2.4.0 -Dpackaging=jar

mvn install:install-file -Dfile=opennlp-tools.jar -DgroupId=opennlp.tools     -DartifactId=opennlp-tools -Dversion=1.3.0 -Dpackaging=jar

mvn install:install-file -Dfile=trove.jar -DgroupId=gnu.trove     -DartifactId=trove -Dversion=1.0.2 -Dpackaging=jar

mvn install:install-file -Dfile=indri.jar -DgroupId=lemurproject     -DartifactId=indri -Dversion=2.6 -Dpackaging=jar

mvn install:install-file -Dfile=plingstemmer.jar -DgroupId=javatools.plingstemmer     -DartifactId=plingstemmer -Dversion=2007-w24-3 -Dpackaging=jar

