javac inf/elte/parhalg/example/ExampleServer.java
if [ $? -ne 0 ]; then
	echo "Compile Failed, script stopped!"
else
	java inf.elte.parhalg.example.ExampleServer /media/2488-8586/backup 2345
fi