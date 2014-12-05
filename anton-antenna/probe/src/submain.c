static void fail(const char *information)
{
	perror(information);
	exit(1);
}

static inline int equip_radio(void)
{
	int radio = socket(PF_INET, SOCK_DGRAM, 0);
	if(radio < 0) fail("socket()");
	
	int bc = 1;
	if(setsockopt(radio, SOL_SOCKET, SO_BROADCAST, &bc, sizeof(bc)) != 0) fail("setsockopt()");
	
	struct timeval tv = {
		.tv_sec = 2,
		.tv_usec = 0
	};
	if(setsockopt(radio, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) != 0) fail("setsockopt()");
	
	return radio;
}

static inline void transmit_message(int radio, const char *specific, ui port, const char *message, size_t size)
{
	struct addrinfo *ai0 = NULL;
	struct sockaddr_in *t = NULL;
	
	if(specific != NULL)
	{
		if(getaddrinfo(specific, NULL, NULL, &ai0) != 0) fail("getaddrinfo()");
		for(struct addrinfo *ai = ai0; ai != NULL; ai = ai->ai_next)
		{
			if(ai->ai_family != PF_INET)
				continue;
			t = (struct sockaddr_in *)(void *)ai->ai_addr;
			break;
		}
	}
	
	struct sockaddr_in to = {
		.sin_family = AF_INET,
		.sin_port = htons(port),
		.sin_addr = {.s_addr = t ? t->sin_addr.s_addr : INADDR_BROADCAST}
	};
	
#define B(k) ((ui)(((bt *)(&to.sin_addr))[k]))
	if(specific != NULL)
		printf("specific target: %u.%u.%u.%u\n", B(0), B(1), B(2), B(3));
	ssize_t sz = sendto(radio, message, size, 0, (struct sockaddr *)&to, sizeof(to));
	
	if(ai0)
		freeaddrinfo(ai0);
	
	if(sz < 0 || (size_t)sz != size)
		perror("sendto()");
}

static inline void transmit_string(int radio, const char *specific, ui port, const char *string)
{
	transmit_message(radio, specific, port, string, strlen(string));
}

static inline ssize_t pick_up_signal(int radio, char *buffer, size_t size, struct sockaddr_in *from)
{
	socklen_t flen = sizeof(from);
	
	ssize_t sz = recvfrom(radio, buffer, size, 0, (struct sockaddr *)from, &flen);
	
	if(sz < 0 && errno == EAGAIN)
	{
		puts("signal dissipated");
		sz = -1;
	}
	else if(sz < 0 || (size_t)sz > size)
	{
		perror("recvfrom()");
		sz = -1;
	}
	
	return sz;
}

static inline void handle_signal(const char *message, size_t size, const struct sockaddr_in *from)
{
	// ~ if(size == sizeof(SIGNAL_PHRASE) - 1 && !memcmp(message, SIGNAL_PHRASE, sizeof(SIGNAL_PHRASE) - 1))
	// ~ {
		// ~ char ident[INET_ADDRSTRLEN];
		// ~ inet_ntop(AF_INET, &from->sin_addr, ident, sizeof(ident));
		// ~ printf("signal at %s\n", ident);
	// ~ }
	if(size >= sizeof(SIGNAL_PHRASE) - 1 && !memcmp(message, SIGNAL_PHRASE, sizeof(SIGNAL_PHRASE) - 1) && message[sizeof(SIGNAL_PHRASE) - 1] == ' ')
	{
		if(from)
		{
			char ident[INET_ADDRSTRLEN];
			inet_ntop(AF_INET, &from->sin_addr, ident, sizeof(ident));
			printf("signal at %s\n", ident);
		}
		fputs("identification is", stdout);
		fwrite(message + sizeof(SIGNAL_PHRASE) - 1, 1, size - (sizeof(SIGNAL_PHRASE) - 1), stdout);
		puts("");
	}
	else
	{
		fputs("unrecognized message [", stdout);
		fwrite(message, 1, size, stdout);
		puts("]");
		exit(1);
	}
}

static inline void probe_field(int radio, const char *specific)
{
	transmit_message(radio, specific, PORT, PROMPT_PHRASE, sizeof(PROMPT_PHRASE) - 1);
	
	char buf[16384];
	struct sockaddr_in from;
	
	ssize_t sz = pick_up_signal(radio, buf, sizeof(buf), &from);
	if(sz >= 0)
		handle_signal(buf, (size_t)sz, specific != NULL ? NULL : &from);
}

static inline void conceal_radio(int radio)
{
	if(close(radio) != 0) fail("close()");
}

static void submain(const char *specific)
{
	puts("running probe");
	
	int radio = equip_radio();
	probe_field(radio, specific);
	conceal_radio(radio);
}
