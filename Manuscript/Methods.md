### Methods
This section presents the methods used to develop the software instantiations presented in the Results section. These include descriptions of software packages, and mathematical formalisms that describe each approach to our common problem.

#### BraGenBrain
The BraGenBrain approach utilizes a BV-genetic algorithm hybrid approach to produce adjacency matrices representing small connectomes. The use of operators such as crossover, mutation, and selection are used to introduce developmental plasticity, while the best performing developmental trajectories are discovered using natural selection. As the BV agents move around and interact in a sandbox simulation, agents develop both implicit (nervous system) as well as explicit (behavior) features. 

Environment and body. The BraGenBrain environment is a n-dimensional “box” of predefined size in pixels (which makes the suite screen size-independent) with so-called “world objects” across which the agents move. At the moment of writing, we have only conducted experiments in a two-dimensional space with one type of world objects defined as perfect circles of equal size. Yet extension of the program to additional dimensions is possible. An agent body incorporates many of the classic BV elements [5]. These include a body core, sensors that receive signals from world objects (the nature of these signals is defined later) as well as motors that move the whole body, that is body core, sensors and motors themselves based on the signal received by sensors (that is, sensors and motors are interconnected). Currently, we have conducted experiments with body of pre-defined “primitive Braitenberg” (rectangular) shape with sensors and motors circles small enough to mind them little in calculations, however the agent class (Vehicle) was supplied with a companion factory

__Table 1__. Constructing a vehicle in the BraGenBrain environment.

class Vehicle {                                                       
	//internal vehicle fields and methods
	...           
	companion object Factory {
		...                                                  
		fun simpleBVVehicle(...) : Vehicle {...}              
		//other vehicle-producing functions                   
		...                                                   
	}                                                             
}  
      
Creating a custom vehicle body is as easy as adding a new member function to the companion factory class. The world is filled with objects on pseudo-random position (Java Random generator) inside the box without taking into account closeness to already existing world objects.

Vehicles movement and attraction/repulsion. The simulation is being rendered in discrete “ticks” or “frames”. Movement of vehicles around the space is calculated each tick and is comprised of several steps. The first step in determining vehicle behavior is to approximate the effect of objects and the environment on the vehicle’s sensors. Any given set of objects has its own effect strength taken from the random distribution between two values, which can be configured in the BraGenBrain control panel. The force (F) generated by a vehicle can be calculated as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{1}$$

where d is the distance between the world object and sensor measured in pixels, e is the effect strength (represented in code as effectStrength), and g is a specific gravity constant (approximated as g = 10). 

The second step is to determine vehicle movement. All speed vectors that result for each motor separately by propagating sensors signals are being aggregated together (in our experiments just summed up) and result in rotating movement and sliding movement. Rotating movement calculates the angle between two adjacent vectors using following formula 

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{2}$$

where x and y are the corresponding axes coordinates of two speed vectors: the previous time point sampled (tick-1) and the current time point sampled (tick0). When the vehicle body is rotated around the body’s midline, such sliding movements are calculated by adding updated tick speed vector lengths along the axes to the vehicle position after the previous tick. If the resulting angle () is negative, 2is added to rectify the value.

Brain Network Formalism. The neuronal network of our cognitive agents are represented as a directed acyclic graph (DAG), which can be defined in the form G = (V, E). In this formulation, Vis a set of neurons and E set of connections between neurons. Note that ∀i,jV |E(i,j)| 1, i.e. there can not be more edges than one between a distinct pair of nodes (but could be none).  

For the sake of balancing the signal of each neuron, we normalized the weights so each neuron output synapses weights sum up to one. We do this with a weight function defined as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{3}$$

where a functionwwhich maps from the set of edges E to rational numbers between zero and one, so that the sum of weights of all edges from a particular network node is equal to 1. 

There is extreme overhead in storing each connection weight in a given network as a float (4 bytes). To overcome this, we compressed the representation to eight bits, so that there were 255 possible weights. This is defined by the following equation

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{4}$$

in which we multiply the w (rational weight) of a connection by the factor of 255 and take the next lower bound integer to this number (w<sub>concise</sub>). 

In this way, we preserve variety in the network while also reducing considerably the computational load. Related to this, we require a way to represent whole brain graphs in a compact manner. We chose matrix graph representation as a means to store connectivity information. This representation can be described mathematically as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{5}$$

where each i,jth entry of matrix M is the weight of edge between nodes i and j in our brain only if i < j, and zero otherwise (dictated by the fact that we do not want to count the connection between i and j and then j and i twice).

__Code.__ BraGenBrain is written in Kotlin using the JavaFX-based TornadoFX for the GUI, TornadoFX being a somewhat more language-affine binding for Kotlin) and is optimized enough to run agent populations up to 1000 agents smoothly on a standard personal computer. For more information, please visit the home repository: https://github.com/Orthogonal-Research-Lab/ GSoC-Braitenberg-Vehicles/tree/master/Stefan

Modeling Neural Plasticity using Multisensory Inputs
This instantiation is to create a robust and efficient simulation of Hebbian plasticity in learning and memory. The simulation utilized a Braitenberg Vehicle (BV) that possesses an olfactory system (smell), a gustatory system (taste), an associative memory, a motor unit, and a judgement unit. The BV is allowed to explore freely in an environment where sources of olfactory and gustatory stimuli are distributed, and to learn the association between taste and odor so as to approach the good sources and avoid the bad.

__Environment Setup.__ In this instantiation, our developmental BV attempts to acquire the association between odor and taste in a 2-dimensional space of sources that emit odor and taste. We tried to make the environment realistic that olfactory stimuli decay with distances exponentially from their sources, while gustatory stimuli are sensible only when the BV is within gustatory boundaries of those stimuli. These can be represented using an odor space and a taste space (Figure 5), respectively [44]. The space can be expressed mathematically as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{6}$$

where Ox,y,i is the ith olfactory feature sensible at position (x,y), and Io,i(k)is the ith feature of the odor omitted by stimulus source k; similarly, G and Ig,i (k)are for gustatory features. d d,y(k) is the Euclidean distance from (x,y) to source k, while dmax is the maximum distance in space, d′ is the gustatory sensible threshold, and c is an arbitrary scalar. Θ is the standard Heaviside function.

__Li-Hopfield Network.__ The olfactory system, is implemented as a type of Li-Hopfield network [45], which is used as a standard model of olfactory bulb function (Figure 6). Li-Hopfield networks model the dynamics of two important cells in olfactory bulb: mitral cells and granule cells. Mitral cells take in relayed sensory information from receptor cells and glomeruli as input, and produce appropriate outputs to other parts of the brain [46]. Meanwhile, granule cells serve as inhibitors of mitral cell activity [47]. In a biological context, the ratio of granule cells than mitral cells is high. In this model, however, there are equal numbers of each. The Li-Hopfield formalism can be described mathematically as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{7}$$

where x and y are the internal states of mitral cells and granule cells. M, G, and L are the weight matrices from granule to mitral, mitral to granule, and mitral to mitral, respectively, f are activation functions, Γ is a function setting the lower triangular entries to zeros. I is the input and Icis the constant ("center") input, and  is the time constant. The powers of mitral cells’ oscillation are collected to be the input to the BV’s associative memory.

__Gustatory System.__ In this model, the gustatory system is only a single layer of cells, for taste is simply an "impression" in this simulation. There is no noise involved in taste [48], or any other perturbation, so further processing of taste is redundant [49]. 

__Associative Memory.__ To model the associative memory between odors and tastes, we implement an associative memory using the generalized Hebbian algorithm (GHA) [50] with depression. When only odor patterns are present, the associative memory maps the odor to taste (recalling mode). When odor and taste patterns coincide, the associative memory changes its weights with the following dynamics (learning mode)

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{8}$$

where W is the association between I'o, the processed olfactory input, and Ig, penalized using the depression matrix D with a depression rate . Wijis set to 0 if the denominator of Dijis zero. 

The latter two expressions are requisite for GHA to be stable. However, in this learning process, unlike typical machine learning with neural networks where samples are learned one by one, occurs in a space where samples are mixed and the time required to learn from each sample is unknown. The presentation order also presents unpredictability, as each sample can be revisited, either immediately or after the vehicle visits a sequence of samples. It is hard to determine the initial learning rate and control the pace of convergence, in addition to avoiding the effect of initial conditions when samples are not introduced serially. The learning rate is thus empirically set to a small constant (O(10-3)) and decreased by 1% after the BV gets a set of non-zero gustatory features. Moreover, the depression matrix that naively imitates activity-dependent long-term depression attempts to cancel the effect of repeatedly learning from one stimulus source and noisy data. Its effect can be demonstrated through static testing where the BV does not move and stimuli are presented without priming.

__Motor Unit.__ The motor unit is radian-based. The BV moves along the heading direction whose value is in [-, ]. When the increase in preference passes a threshold, the BV moves forward with a little offset based on the increase; when the decrease in preference passes the threshold, the BV moves backward with a little offset based on the decrease. Otherwise, it moves towards a nearby source. The motor unit is implemented in Movement.RadMotor class. Because the learning rate of GHA has to decrease to ensure stability, the motor unit is equipped with memory to avoid repeated back-and-forth movement near the gustatory boundary of a “good” sample, which could easily lead to overfitting.

__Simple Judgement Unit.__ The judgement of a source is based on its taste. A judgement unit can be defined in the following form

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{9}$$

where p is the preference from the summation of different judgement J on recalled taste I'g. If there is no recalled taste and real taste exists, then I'g=Ig. The preference, the output of the judgement unit, is the sum of the output of each preference function applied to their corresponding gustatory attributes. The judgement unit is incorporated in Simulation class.

__Code.__ This project uses Cython and C. The most time consuming parts are either written in core.c or implemented by using OpenBLAS.. Static images such as those shown above are produced through Networkx and Matplotlib, while real-time animation is generated using PyQtGraph and PyQt5. For more information, please visit the home repository: https://github.com/Orthogonal-Research-Lab/GSoC-Braitenberg-Vehicles/tree/master/Ziyi 

__Methods for swarm intelligence using BVs__
The idea here is to assign a set of rules associated with environmental stimuli to each agent (vehicle) as described in the book "Vehicles: Experiments in Synthetic Psychology" [5]. Based on these simple rules when multiple agents (vehicles) are introduced in the environment, they behave like intelligent swarms. In the present version, there is no interaction between the agents, so their behavior is solely dependent upon the nature of the stimuli present in the environment.

__Wiring and Activation Rules.__ The wiring and activation rules for the swarm intelligence approach can be defined considering functions for inhibition-dependent action 

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{10}$$  

where A1 is excitatory activation, A2is inhibited activation, r is the distance between sensor and stimulus and k, k1, k2are calibrated constants.

__Components of Vehicle Kinematics.__ As the sensory activation reaches the wheel motor, it induces two different angular velocities in the two wheels which is mainly responsible for the resulting vehicular movement. Considering the angular velocities of the two wheels and the vehicular dimensions, we can define the components of two dimensional motion of the vehicle as

$${\Delta}{W_ij} = {\dfrac{\phi} {50 {W^2_ij}{I_j} + 1}} \tag{11}$$  

where Vx and Ay are the components of linear velocity along the axis of the vehicle and the radial acceleration perpendicular to the axis, respectively. 1, 2 are the angular velocities of left and right wheel respectively, while k is the calibrated constant depending upon vehicular dimensions. Based on an egocentric view of the environment, the vehicle can take either a left or right turn depending upon the difference in angular velocities. 1< 2results in a turn leftward, while 1> 2 results in a turn rightward 

Code. The code for this instantiation has been constructed in the Processing.py software environment (written in Python). For the elementary setup option, opt for python-mode. Alternatively, you can run this program in terminal as well as edit the code in your favorite text editor. For more information, please visit the home repository: [https://github.com/ankiitgupta7/ Simulations- of-Braitenberg-Vehicles](https://github.com/ankiitgupta7/ Simulations- of-Braitenberg-Vehicles)

