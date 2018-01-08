#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h> /*pid_t */
#include <sys/wait.h> /*wait */

#define M 2
#define N 3
#define O 2

int main()
{
	//example matrices
	int matrix1[M][N] = { {1,2,3}, {4,5,6} }; /*M rows x N columns*/
	int matrix2[N][O] = { {1,2}, {3,4}, {5,6} }; /* N x O */
	int Product[M][O]; /* M x O */

	//declaration and creation of pipes
	pid_t pid[M * O];
	int childarray[M*O];
	int i, j, k;
	int row=0;
	int col=0;
	int childnumber =0;
	int childsolution;
	int fd[M*O][2];
	for(i=0; i<M*O; i++)
	{
	  pipe(fd[i]); 
	}
	for(i=0; i<M; i++)//initialize product matrix
	  for(j=0; j<O; j++)
	    Product[i][j]=0;
	for(i = 0; i < M * O; ++i)//children making loop
	{
	  if(pid > 0)
	  {
	    childarray[i]=(pid[i]=fork());
	    wait(NULL);
	  }
	  if(pid[i] == 0)
	  {
	    childsolution=0;
	    for(j=0; j<N; j++)
	    {
	      childsolution+=matrix1[row][j]*matrix2[j][col];
	    }
	    close(fd[childnumber][0]);
	    write(fd[childnumber][1], &childsolution, sizeof(int));
	    exit(0);
	  }
	  if(pid>0)
	  {
	    childnumber++;
	    if(col<(M-1))
	      col++;
	    else{col=0; row++;}
	  }
	}
	if(pid>0)//parent only loop
	 {
	   for(i=0; i<M*O; i++)
	   {
	     waitpid(childarray[i],0,0);
	   }
	   j=0;
	   k=0;
	   for(i=0; i<M*O; i++) //save solution
	   {
	     close(fd[i][1]);
	     read(fd[i][0], &childsolution, sizeof(int));
	     Product[j][k]=childsolution;
	     if(k<O-1)
	       k++;
	     else
	     {
	       k=0;
	       j++;
	     }
	     
	   }
	   printf("The solution is:\n");
	   for(j=0; j<M; j++) //print solution
	   {
	     printf("\n");
	     for(k=0; k<O; k++)
	       printf("     %d", Product[j][k]);
	   }
	   printf("\n");
	  }
	return 0;
	
}
