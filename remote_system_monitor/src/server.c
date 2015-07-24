#include <rpc/rpc.h>
#include <time.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <errno.h>
#include "nma.h"

#define BUFFER_MAX 4096


void run_program(char* resp, char* cmd)
{
  static FILE *stream;
  static int num_written;

  stream = popen(cmd, "r");
  if (stream)
  {
    num_written = fread(resp, 1, BUFFER_MAX-1, stream);
    resp[num_written-1] = '\0';
    pclose(stream);
  }
  else
  {
    strcpy(resp, "Error running program \0");
  }
}

char **get_sys_info_1(long *option)
{
  struct tm *timeptr;
  time_t clock;
  static char buffer[BUFFER_MAX];
  static char *ptr;

  ptr = buffer;

  clock = time(0);
  timeptr = localtime(&clock);

  switch(*option)
  {
    case 1:
      strftime(buffer,BUFFER_MAX,"%A, %B %d, %Y - %T",timeptr);
      break;

    case 2:
      run_program(buffer, "top -b -n 1");
      break;

    case 3:
      run_program(buffer, "netstat -s");
      break;

    default:
      strcpy(buffer, "Invalid Response \0");
      break;
  }

  return(&ptr);
}
