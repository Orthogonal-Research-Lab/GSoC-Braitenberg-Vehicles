## Braitenberg Vehicles as Developmental Neurosimulation
Bradly Alicea<sup>1,2</sup>, Stefan Dvoretskii<sup>3</sup>, Ziyi Gong<sup>4</sup>, Ankit Gupta<sup>5</sup>, and Jesse Parent<sup>6</sup>

<sup>1</sup> Orthogonal Research and Education Laboratory, <sup>2</sup> OpenWorm Foundation, <sup>3</sup> Technische Universität München, <sup>4</sup> University of Pittsburgh, <sup>5</sup> IIT Kharagpur, <sup>6</sup> SUNY Albany.

### Abstract

### Introduction
How do we understand the emergence of a connected nervous system, particularly in terms of how it leads to neural function and behavior? One way is to infer the co-occurrence of neural cell differentiation in a model organism [1,2]. This requires a small connectome in which cell differentiation can be tracked. Even for organisms such as the nematode Caenorhabditis elegans [3], direct experimentation is difficult. An embodied in silico system with a generalized nervous system would provide a means to both modify the developmental process and directly observe all possible developmental outcomes. Utilizing an abstraction to study hard-to-observe questions is in fact consistent with how theoretical modeling and simulations have been used throughout the history of neuroscience [4]. We propose that Braitenberg Vehicles (BV) [5] can be used as a means to construct such simulations. Originally a thought experiment first proposed by Valentino Braitenberg, BVs are embodied model of a simple nervous system. The basic architecture not only allows us to embody a connectome, but also to model its behavioral outputs. It is of note that Braitenberg’s aim was to imagine how simple behaviors can emerge from hard-wired nervous systems and phenotypes. Our approach differs in that we allow nervous systems to develop using a variety of techniques. We will introduce a general computational model, followed by specific instantiations involving different aspects of cognition. 

#### Motivation
This work is motivated by a desire to understand neurodevelopment balanced with a need to establish a model system that allows us to simulate processes such as learning, plasticity, and the regulation of behavior. Of particular interest is a model which allows us to model global structures such as the connectome [6], which forces us to understand a network of neural cells that provide us with complex behavioral outputs. Much as with biological model organisms, their digital counterparts must allow for these processes to be experimentally tractable. The BV is a good model in this regard, since it allows for a realistic amount of complexity but also provides a means to reverse engineer this complexity. 

#### Neurodevelopment
The study of neural development has a long history [7]. The phenomenon of neural development proceeds from a simple group of cells to a complex network of multiple functions.   In vertebrates, the spinal cord and brain arise from the neural tube [8]. This drive towards complexity also results to complex small connectomes with specialized function such as those found in Drosophila mushroom bodies [9]. BV models of development allow us to implement this drive towards complexity in a digital environment where the components of the emerging nervous system can be specified and measured. 

#### Suitability of BVs for Modeling Development
There are three benefits in choosing the Braitenberg vehicles paradigm to model development: a simplified structural-functional relationship, the ability to simulate an embodied nervous system, and the flexibility of modeling a heterogeneous population of agents. These benefits are summarized as follows

__Simplistic structure-function relationship.__ BVs provide a simplistic model for understanding the interplay between the complexity of brains and their relationship to the physical world. 

__Embodied neural system.__ BVs provide us with both a simple mapping between sensors and effectors. In addition, BVs allow us to model nervous system connectivity as a consequence of their behavior in the world. 

Since the mapping between nervous system elements and behavioral outputs is so explicit, we can introduce a heterogeneous population of agents into the same environment to study and exploit collective behaviors.

#### Neurodevelopment and Brain Networks
Even in the case of an in silico model, it is often difficult to approximate the complexity of a connectome. Attempts to grow a connectome in silico using the connection rules of an adult mouse brain [10] demonstrate the difficulty of simulating a network at large scales. While scale is a major factor in this complexity, the nature of developmental (as opposed to adult) rules are often unknown. 

In _C. elegans_, the hierarchical nature of its connectome reveals a number of higher-order organization principles such as rich-club connectivity [11] and the hourglass effect [12].

We have also attempted to bridge the gap between strong biological fidelity and models of mixed cognitive and biological fidelity [13]. Such “mixed” models correspond to the deep learning/swarm instantiation presented in a later section. The other two software instantiations, in addition to the general computational developmental neuroscience model, exhibit strong biological fidelity. As such they rely on bottom-up organizational principles such as a plasticity of connections and the emergence of simple behaviors. On the other hand, mixed biological-cognitive models retain a pattern of connectivity throughout their life-history trajectory (and thus a non-plastic behavioral repertoire). Yet while each agent is used to represent singular behaviors, putting them in an environment with other agents representing the same or a multitude of behaviors can result in the observation of emergent phenomena [14].

#### Models of Regulation
Our approach relies upon several models of regulation that may also play a role in emergent nervous systems that interact with their environments. Our software instantiation present at least two: behavioral reinforcement and Hebbian learning. Behavioral reinforcement is most famously characterized through reinforcement learning techniques [15], but the core mechanism itself can be implemented using a host of other techniques [16]. Hebbian learning is the dictum that “neurons that fire together, wire together” [17]. While there are parallels between these processes, 

#### Emergence (specific to model organisms)
	We also explore emergent phenomena at two scales of biological organization: the biological connectome within an agent and collective behavior among a population of agents. While they result in different types of behavioral outcomes, in some ways the emergence of behaviors involves multiple sets of mechanisms that tend to be scale-invariant [18]. 

### Methods

#### Modeling Neural Plasticity using Multisensory Inputs

Learning functions for Hebbian Algorithm. The learning rate should be properly set and converge to zero for accuracy, but in this simulation it is a small constant, because this learning process, unlike typical machine learning with neural networks where samples are learned one by one, occurs in a space where samples are mixed and the time required to learn from each sample is unknown (so are the orders of learning samples). In addition, each sample can be revisited, either immediately or after the Braitenberg vehicle visits some other samples. It is hard to determine the initial learning rate and control the converging pace. It is also hard to avoid the effect of initial conditions if the convergence is introduced.

Moreover, a depression function

$$\DeltaW_ij = \phi/50 W^2_ij I_j + 1$$

that naively imitates activity-dependent long-term depression is used to cancel the effect of repeatedly learning from one stimulus source and noisy data. Its effect was demonstrated through static testing where the BV does not move and stimuli are presented without priming, yet not demonstrated in the actual simulation. The associative memory is implemented as a bidirectional associative memory model in Layers.BAM class.

__Code.__ This project uses Cython and C. The most time consuming parts are either written in core.c or implemented by using OpenBLAS.. Static images such as those shown above are produced through Networkx and Matplotlib, while real-time animation is generated using PyQtGraph and PyQt5.


### Results

#### Computational model of Developmental Neuroscience
One goal of this project is to create a generalized computational model of neurodevelopment. This will allow us to investigate a large number of potential research questions. In general, we have found that there are three ways to approach an approximation of development and plasticity. Two of these approaches are a forward mapping, and the third is an inverse mapping. The first is to use a correlation (or covariance) matrix approach, where all neural units in the nervous system are compared with every other neural unit. This results in pairwise comparisons that can lead to connectome network maps [19]. The second approach is to add nodes and arcs sequentially to a simple set of I/O connections. In this case, we get a more explicit network topology, and can observe phenomena such as preferential attachment. A third approach is to prune connections from a fully-formed network engaged in hard-wired behaviors. Using this approach, one can come to understand exactly which connections and neurons are essential for the execution of a behavior.

#### Software Instantiations.
Currently, there are three software instantiations of the software: BraGenBrain, modeling neural plasticity using multisensory inputs, and BVs as Deep Learning. We will now discuss their details and limitations.  

__BraGenBrain: a genetic algorithmic approach.__ 

__Modeling Neural Plasticity using Multisensory Inputs.__ This instantiation is to create a robust and efficient simulation of Hebbian plasticity in learning and memory. The simulation utilized a Braitenberg Vehicle (BV) that possesses an olfactory system (smell), a gustatory system (taste), an associative memory, a motor unit, and a judgement unit. The BV is allowed to explore freely in an environment where sources of olfactory and gustatory stimuli are distributed.

During its exploration, the BV associates taste with smell when both taste and smell information are available. When there is no taste, it recalls the taste based on its associative memory and the smell received. Tastes are both sensed and recalled, which can produce preference that affects the BV's movement. When the BV becomes more and more mature via association, it can exhibit avoidance and preference behaviors, in a manner similar to small animals. An example of the simulation is shown in Figure 1.




__Figure 1.__ An example of real-time animation. Experiments are conducted using an iPython Jupyter Notebook.

_Environment._ The environment is realistic that olfactory stimuli decay with distances exponentially from their sources, while gustatory stimuli are sensible only when the BV is within gustatory boundaries of those stimuli. The mapping between olfactory attributes and gustatory attributes should be defined before initializing Space.Space class and Simulation class. These outputs can be represented using an odor space and a taste space, respectively (Figure 2).



__Figure 2.__ Odor space [20] of one olfactory attribute (left) and taste space of one gustatory attribute (right).

_Olfactory System._ The olfactory system, is implemented as a type of Li-Hopfield network [21], which is used as a standard model of olfactory bulb function (Figure 3). Li-Hopfield networks model the dynamics of two important cells in olfactory bulb: mitral cells and granule cells. Mitral cells take in relayed sensory information from receptor cells and glomeruli as input, and produce appropriate outputs to other parts of the brain [22]. Meanwhile, granule cells serve as inhibitors of mitral cell activity [23]. In a biological context, the ratio of granule cells than mitral cells is high. In this model, however, there are equal numbers of each.



__Figure 3.__ Li-Hopfield-inspired model of the olfactory bulb. The grey dots are the mitral cells and the black are the granule cells. Red means excitation and blue means inhibition.

The Li-Hopfield network has been characterized as a group of coupled nonlinear oscillators [24]. In short, it is able to alter its oscillatory frequencies based on changes in olfactory attributes, so it is important to "filter" the noise and identify which stimulus source the BV is approaching. The signal powers of the output are then calculated, instead of modeling a complex afferent nerve in real nervous system. The olfactory system is implemented in Layers.LiHopfield class.

_Gustatory System._ In this model, the gustatory system is only a single layer of cells, for taste is simply an "impression" in this simulation. There is no noise involved in taste [25], or any other perturbation, so further processing of taste is redundant [26]. The gustatory system is implemented in Layers.Single class.

_Associative Memory._ The associative memory, implemented as a bidirectional associative memory (BAM), is how Hebbian learning is represented in this model (Figure 4). Rather than Hebbian rule that BAM often utilized, a Generalized Hebbian algorithm (GHA) is used, for it is demonstrably stable. The learning rate converges to zero with a constant rate to ensure the stability of GHA.



__Figure 4.__ An example of a bidirectional associative memory (BAM) network. Nodes on the left are input cells, and the nodes on the right are output cells.

_Motor Unit._ The motor unit is radian-based. The BV moves along the heading direction whose value is in [-, ]. When the increase in preference passes a threshold, the BV moves forward with a little offset based on the increase; when the decrease in preference passes the threshold, the BV moves backward with a little offset based on the decrease. Otherwise, it moves towards a nearby source. The motor unit is implemented in Movement.RadMotor class. Because the learning rate of GHA has to decrease to ensure stability, the motor unit is equipped with memory to avoid repeated back-and-forth movement near the gustatory boundary of a “good” sample, which could easily lead to overfitting.

_Judgement Unit._ An array of preference function should be defined before initializing Simulation class. The preference, the output of the judgement unit, is the sum of the output of each preference functions applied to their corresponding gustatory attributes. The judgement unit is incorporated in Simulation class.


__BVs as a Deep Learning/Swarm model.__

### Discussion and Future Plans

Modeling Neural Plasticity using Multisensory Inputs. Future Plan. Put the trained BV in a new, testing environment, like conducting tests in animal models. Implement the progress saving functionality.



#### Use cases


Possible Implementation for modeling neural plasticity using multisensory inputs include more complex senses or more than two senses. More than 1 BV and BVs interactions. Another possible solution: apply genetic algorithm or other kinds to optimize the network structure.


### References
[1] Kaiser, M. (2017). Mechanisms of Connectome Development. _Trends in Cognitive Sciences_, 21(9), P703-P717. doi:10.1016/j.tics.2017.05.010.

[2]  Alicea, B. (2017). The Emergent Connectome in Caenorhabditis elegans Embryogenesis. _BioSystems_, 173, 247-255. 

[3] Larson, S.D., Gleeson, P., & Brown, A.E.X. (2018). Connectome to behaviour: modelling Caenorhabditis elegans at cellular resolution. _Philosophical Transactions of the Royal Society of London B_, 373(1758), 20170366. doi:10.1098/rstb.2017.0366.

[4] Fan, X. & Markram, H. (2019). A Brief History of Simulation Neuroscience. _Frontiers in Neuroinformatics_, doi:10.3389/fninf.2019.00032.

[5] Braitenberg, V. (1984). Vehicles: experiments in synthetic Psychology. MIT Press, Cambridge, MA.

[6] Seung, S. (2009). Connectome: How the Brain's Wiring Makes Us Who We Are. Houghton-Mifflin, Boston.

[7] Stiles, J. & Jernigan, T.L. (2010). The Basics of Brain Development. _Neuropsychological Review_, 20(4), 327–348. doi:10.1007/s11065-010-9148-4.

[8] Smith, J.L. & Schoenwolf, G.C. (1997). Neurulation: coming to closure. _Trends in Neurosciences_, 20(11), P510-P517. doi:10.1016/S0166-2236(97)01121-1.

[9] Eichler, K., Li, F., Litwin-Kumar, A., Park, Y., Andrade, I., Schneider-Mizell, C.M., Saumweber, T., Huser, A., Eschbach, C., Gerber, B., Fetter, R.D., Truman, J.W., Priebe, C.E., Abbott, L.F., Thum, A.S., Zlatic, M., & Cardona, A. (2017). The complete connectome of a learning and memory centre in an insect brain. _Nature_, 548(7666), 175-182. doi:10.1038/nature23455

[10] Craddock, R.C., Tungaraza, R.L., & Milham, M.P. (2015). Connectomics and new approaches for analyzing human brain functional connectivity. _Gigascience_, 4, 13.

[11] Towlson, E.K., Vertes, P.E., Ahnertm, S.E., Schafer, W.R., & Bullmore, E.T. (2013). The rich club of the C. elegans neuronal connectome. _Journal of Neuroscience_, 33(15), 6380-6387. doi:10.1523/JNEUROSCI.3784-12.2013.

[12] Sabrin, K.M & Dovrolis, C. (2017). The hourglass effect in hierarchical dependency networks. _Network Science_, 5(4), 490-528.

[13] Kriegeskorte, N. & Douglas, P.K. (2018). Cognitive computational neuroscience. _Nature Neuroscience_, 21, 1148–1160.

[14] Couzin, I. & Krause, J. (2003). Self-Organization and Collective Behavior in Vertebrates. _Advances in the Study of Behavior_, 32, 1-75 doi:10.1016/S0065-3454(03)01001-5.

[15] Neftci, E.O. & Averbeck, B.B. (2019). Reinforcement learning in artificial and biological systems. _Nature Machine Intelligence_, 1, 133–143. 

[16] Drugan, M.M. (2019). Reinforcement learning versus evolutionary computation: A survey on hybrid algorithms. _Swarm and Evolutionary Computation_, 44, 228-246.

[17] Munakata, Y. & Pfaffly, J. (2004). Hebbian learning and development. _Developmental Science_, 7(2), 141–148.

[18] Khaluf, Y., Ferrante, E., Simoens, P., & Huepe, C. (2017). Scale invariance in natural and artificial collective systems: a review. _Journal of the Royal Society Interface_, 14, 20170662. doi:10.1098/rsif.2017.0662.

[19] Henriksen, S., Pang, R., & Wronkiewicz, M. (2016). A simple generative model of the mouse mesoscale connectome. _eLife_, 5, e12366. doi:10.7554/eLife.12366.

[20] Soh, Z., Nishikawa, S., Kurita, Y., Takiguchi, N., & Tsuji, T. (2016). A Mathematical Model of the Olfactory Bulb for the Selective Adaptation Mechanism in the Rodent Olfactory System. _PLoS One_, 11(12), e0165230. doi: 10.1371/journal.

[21] Li, Z. & Hopfield, J.J. (1989). Modeling the Olfactory Bulb and its Neural Oscillatory Processings. _Biological Cybernetics_, 61, 379-392.

[22] Nagayama, S., Enerva, A., Fletcher, M. L., Masurkar, A.V., Igarashi, K.M., Mori, K., & Chen, W.R. (2010). Differential axonal projection of mitral and tufted cells in the mouse main olfactory system. _Frontiers in Neural Circuits_, 4(120). doi: 10.3389/fncir.2010.00120.

[23] Shepherd, G.M., Chen, Willhite, W.R. D., Migliore, M., & Greer C.A. (2007). The olfactory granule cell: From classical enigma to central role in olfactory processing. _Brain Research Reviews_, 55, 373-382.

[24] Sanger, T. D. (1989). Optimal Unsupervised Learning in a Single-Layer Linear Feedforward Neural Network. _Neural Networks_, 2, 459-473.

[25] Smith, D.V. & St John, S.J. (1999). Neural coding of gustatory information. _Current Opinion in Neurobiology_, 9, 427-435.

[26] Wu, A., Dvoryanchikov, G., Pereira, E., Chaudhari, N., & Roper, S. D. (2015). Breadth of tuning in taste afferent neurons varies with stimulus strength. _Nature Communications_, 6, 8171. doi: 10.1038/ncomms9171.


