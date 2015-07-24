/*
 * nma.x
 */

/*
 * get_sys_info_1() accepts a long and returns a string
 */

program NMA_PROG
{
  version NMA_VERS
  {
	string get_sys_info(long) = 1; /*procedure number = 1 */

  } = 1;	/* version number = 1 */
} = 1; /* program number */
