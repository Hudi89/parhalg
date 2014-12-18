javac inf/elte/parhalg/example/ExampleClient.java
if [ $? -ne 0 ]; then
	echo "Compile Failed, script stopped!"
else
	java inf.elte.parhalg.example.ExampleClient $@
fi

#Example:
#sh client.sh localhost 1234 ../testfiles/ asd/1.jpg
