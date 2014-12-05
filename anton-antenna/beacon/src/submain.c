static void fail(const char *information)
{
	perror(information);
	exit(1);
}

static inline int extend_antenna(void)
{
	int antenna = socket(PF_INET, SOCK_DGRAM, 0);
	if(antenna < 0) fail("socket()");
	return antenna;
}

static inline void activate_antenna(int antenna, ui port)
{
	struct sockaddr_in addr = {
		.sin_family = AF_INET,
		.sin_port = htons(port),
		.sin_addr = {.s_addr = INADDR_ANY}
	};
	
	if(bind(antenna, (struct sockaddr *)&addr, sizeof(addr)) != 0) fail("bind()");
}

static char identification[18]; // !

static inline void identify_beacon(int antenna) // !
{
	char buf[1024];
	struct ifconf ifc;
	
	ifc.ifc_buf = buf;
	ifc.ifc_len = sizeof(buf);
	if(ioctl(antenna, SIOCGIFCONF, &ifc) == 0)
	{
		const struct ifreq *re = ifc.ifc_req + ((size_t)ifc.ifc_len / sizeof(struct ifreq));
		for(const struct ifreq *r = ifc.ifc_req; r != re; ++r)
		{
			struct ifreq ifr;
			strcpy(ifr.ifr_name, r->ifr_name);
			if(ioctl(antenna, SIOCGIFFLAGS, &ifr) == 0)
			{
				if(!(ifr.ifr_flags & IFF_LOOPBACK))
				{
					if(ioctl(antenna, SIOCGIFHWADDR, &ifr) == 0)
					{
#define A(i) ((ui)(unsigned char)(ifr.ifr_hwaddr.sa_data[(i)]))
						snprintf(identification, sizeof(identification), "%02x:%02x:%02x:%02x:%02x:%02x", A(0), A(1), A(2), A(3), A(4), A(5));
						if(strlen(identification) != 17)
							abort();
						return;
					}
					else
						perror("ioctl(SIOCGIFHWADDR)");
				}
			}
			else
				perror("ioctl(SIOCGIFFLAGS)");
		}
	}
	else
		perror("ioctl(SIOCGIFCONF)");
	
	strcpy(identification, "??:??:??:??:??:??");
}

static inline ssize_t spot_transmission(int antenna, char *buffer, size_t size, struct sockaddr_in *from)
{
	socklen_t flen = sizeof(from);
	
	ssize_t sz = recvfrom(antenna, buffer, size, 0, (struct sockaddr *)from, &flen);
	
	if(sz < 0 || (size_t)sz > size)
	{
		perror("recvfrom()");
		sz = -1;
	}
	
	return sz;
}

static inline void handle_transmission(int antenna, const char *message, size_t size, const struct sockaddr_in *from)
{
	if(size == sizeof(PROMPT_PHRASE) - 1 && !memcmp(message, PROMPT_PHRASE, sizeof(PROMPT_PHRASE) - 1))
		// ~ sendto(antenna, SIGNAL_PHRASE, sizeof(SIGNAL_PHRASE) - 1, 0, (struct sockaddr *)from, sizeof(*from));
	{ // !
		char msg[sizeof(SIGNAL_PHRASE) - 1 + 1 + 17];
		memcpy(msg, SIGNAL_PHRASE, sizeof(SIGNAL_PHRASE) - 1);
		msg[sizeof(SIGNAL_PHRASE) - 1] = ' ';
		memcpy(msg + sizeof(SIGNAL_PHRASE), identification, 17);
		sendto(antenna, msg, sizeof(msg), 0, (struct sockaddr *)from, sizeof(*from));
	}
	else
	{
		fputs("unrecognized message [", stdout);
		fwrite(message, 1, size, stdout);
		puts("]");
	}
}

static inline void analyze_channel(int antenna)
{
	puts("analyzing channel");
	
	for(;;)
	{
		char buf[16384];
		struct sockaddr_in from;
		
		ssize_t sz = spot_transmission(antenna, buf, sizeof(buf), &from);
		if(sz >= 0)
			handle_transmission(antenna, buf, (size_t)sz, &from);
	}
}

static inline void retract_antenna(int antenna)
{
	if(close(antenna) != 0) fail("close()");
}

static void submain(void)
{
	puts("deploying beacon");
	
	int antenna = extend_antenna();
	activate_antenna(antenna, PORT);
	identify_beacon(antenna); // !
	printf("identification is %s\n", identification); // !
	analyze_channel(antenna);
	retract_antenna(antenna);
}
