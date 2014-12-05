#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#ifndef __USE_MISC // !
#define __USE_MISC // !
#endif // !
#include <net/if.h> // !
#include <net/if_arp.h> // !
#include <sys/ioctl.h> // !

#include "common.c"
#include "submain.c"
#include "main.c"
