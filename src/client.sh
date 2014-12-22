javac inf/elte/parhalg/example/ExampleClient.java
if [ $? -ne 0 ]; then
	echo "Compile Failed, script stopped!"
else
	a=
	p=
	while [ $# -ge 1 ] ; do
		if [ $# -ge 2 ] && [ "$1" = '-a' ] ; then
			a="$2"
			shift 2
		elif [ $# -ge 2 ] && [ "$1" = '-p' ] ; then
			p="$2"
			shift 2
			if ! [ "$p" -ge 1 ] ; then
				echo 'invalid port'
				p=
			fi
		elif [ $# -ge 1 ] && [ "$1" = '--' ] ; then
			shift 1
			break
		else
			break
		fi
	done
	if [ -z "$p" ] ; then
		echo "option -p <port> is required"
		exit 1
	fi
	if [ -z "$a" ] ; then
		t="$(../bin/probe)" || { echo 'failed to probe' >&2 ; exit 1 ; }
	else
		t="$(../bin/probe "$a")" || { echo "failed to probe $a" >&2 ; exit 1 ; }
	fi
	if [ -z "$a" ] ; then
		a="$(printf '%s\n' "$t" | grep -Em1 '^(specific target:|signal at) ' | sed -r 's/^(specific target:|signal at) //')"
	fi
	printf '%s\n' "$t" | grep -Em1 '^identification is '
	echo "connecting to $a"
	java inf.elte.parhalg.example.ExampleClient "$@"
fi

#Examples:
#sh client.sh -p 1234 ../testfiles/ asd/1.jpg
#sh client.sh -a localhost -p 1234 ../testfiles/ asd/1.jpg
