javac inf/elte/parhalg/example/ExampleServer.java
if [ $? -ne 0 ]; then
	echo "Compile Failed, script stopped!"
else
	../bin/beacon &
	java inf.elte.parhalg.example.ExampleServer "$@"
fi

#Example:
#sh server.sh /media/2488-8586/backup 1234
