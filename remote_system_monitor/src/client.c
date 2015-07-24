#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/types.h>
#include <rpc/rpc.h>
#include "nma.h"

long get_response(void);


long get_response()
{
  long choice;

  printf("Menu: \n");
  printf("1. Date and Time\n");
  printf("2. Top (Truncated to 4096 bytes)\n");
  printf("3. Netstat (Truncated to 4096 bytes)\n");
  printf("4. Exit\n");
  printf("Make a choice (1-4):");
  scanf("%ld", &choice);

  return(choice);
}


int main(int argc, char **argv)
{
  CLIENT *cl;
  char *server;
  char **sresult;
  //char date_str[DATE_MAX];
  long response;
  long *lresult;

  if(argc != 2)
  {
  fprintf(stderr, "usage:%s hostname\n", argv[0]);
  exit(1);
  }

  server = argv[1];
  lresult = (&response);

  if((cl=clnt_create(server,NMA_PROG,NMA_VERS,"udp")) == NULL)
  {
  clnt_pcreateerror(server);
  exit(2);
  }

  response = get_response();

  while(response!=4)
  {
  if((sresult = get_sys_info_1(lresult,cl)) == NULL)
  {
    clnt_perror(cl,server);
    exit(3);
  }

  printf("%s\n", *sresult);
  response = get_response();
  }

  clnt_destroy(cl);
  exit(0);

  return(0);

}
