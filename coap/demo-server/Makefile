all:build
build:
	javac -classpath lib/californium-core-1.0.4.jar src/org/californium/osfy/demo/MyCoAPServer.java 
run:build
	java -classpath lib/californium-core-1.0.4.jar:lib/element-connector-1.0.4.jar:src/ org.californium.osfy.demo.MyCoAPServer 
clean:
	rm -rf src/org/californium/osfy/demo/*.class
