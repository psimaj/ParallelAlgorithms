# ParallelAlgorithms
A threading project written for Java class.

The project is currently divided into two parts: 
- implementation and comparision of a multithreaded scan algorithm
- simulation based around the idea of random walks on a graph
 
Please note that the program is not directly launchable.
In order to see it in action, one needs to pull the project and run the tests themself via JUnit4.

The scan algorithm is implemented in three ways:
- the classic sequential approach
- a 'step-back' parallel algorithm ran sequentially
- a 'step-back' parallel algorithm ran using multithreading
  
The algorithm are compared on 4 tests of different input sized and operators.

The random walkers simulation is a scenario where initially a certain amount of walkers are placed onto different vertices of a simple, connected graph.  
The walkers then move around the graph randomly, to each neighbour vertex with equal probability.  
If a walker were to move to a vertex that is already taken by an another walker, the entering walker kills the residing walker.  
The simulation continues untill only 1 walker is alive.  
The point of the simulation is researching how long it takes until the process completes.  
Note that it is added that each walker goes to sleep for a brief moment after moving, in order to prevent situations where the first walker basically kills everyone without giving them a chance.  
