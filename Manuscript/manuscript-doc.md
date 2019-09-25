## Braitenberg Vehicles as Developmental Neurosimulation
Bradly Alicea, Stefan Dvoretskii, Ziyi Gong, Ankit Gupta, and Jesse Parent

### Abstract



### Introduction
How do we understand the emergence of a connected nervous system, particularly in terms of how it leads to neural function and behavior? One way is to infer the co-occurrence of neural cell differentiation in a model organism [1,2]. This requires a small connectome in which cell differentiation can be tracked. Even for organisms such as the nematode Caenorhabditis elegans [3], direct experimentation is difficult. An embodied in silico system with a generalized nervous system would provide a means to both modify the developmental process and directly observe all possible developmental outcomes. Utilizing an abstraction to study hard-to-observe questions is in fact consistent with how simulations have been used throughout the history of neuroscience [4]. We propose that Braitenberg Vehicles (BV) [5] can be used as a means to construct such simulations. Originally a thought experiment first proposed by Valentino Braitenberg, BVs are embodied model of a simple nervous system. The basic architecture not only allows us to embody a connectome, but also to model its behavioral outputs. It is of note that Braitenberg’s aim was to imagine how simple behaviors can emerge from hard-wired nervous systems and phenotypes. Our approach differs in that we allow nervous systems to develop using a variety of techniques. We will introduce a general computational model, followed by specific instantiations involving different aspects of cognition. 

#### Motivation
This work is motivated by a desire to understand neurodevelopment balanced with a need to establish a model system that allows us to simulate processes such as learning, plasticity, and the regulation of behavior. Of particular interest is a model which allows us to model global structures such as the connectome [6], which forces us to understand a network of neural cells that provide us with complex behavioral outputs. Much as with biological model organisms, their digital counterparts must allow for these processes to be experimentally tractable. The BV is a good model in this regard, since it allows for a realistic amount of complexity but also provides a means to reverse engineer this complexity. 

#### Neurodevelopment
The study of neural development has a long history [7]. In vertebrates, the spinal cord and brain arise from the neural tube [8], which is a rather simple structure by comparison. This drive towards complexity also results to complex small connectomes with specialized function such as those found in Drosophila mushroom bodies [9]. BV models of development allow us to implement this drive towards complexity in a digital environment where the components of the emerging nervous system can be specified and measured. 

#### Suitability of BVs for Modeling Development
Embodied neural system. BVs provide us with both a simple mapping between sensors and effectors. In addition, BVs allow us to model nervous system connectivity as a consequence of their behavior in the world. 

Simplistic structure-function relationship. BVs provide a simplistic model for understanding the interplay between the complexity of brains and their relationship to the physical world. 

#### Neurodevelopment and Brain Networks
Even in the case of an in silico model, it is often difficult to approximate the complexity of a connectome. Attempts to grow a connectome in silico using the connection rules of an adult mouse brain [10] demonstrate the difficulty of simulating a network at large scales. While scale is a major factor in this complexity, the nature of developmental (as opposed to adult) rules are often unknown. 

In _C. elegans_, the hierarchical nature of its connectome reveals a number of higher-order organization principles such as rich-club connectivity [11] and the hourglass effect [12].

### Models of Regulation
	
### Results

#### Computational model of Developmental Neuroscience
One goal of this project is to create a generalized computational model of neurodevelopment. This will allow us to investigate a large number of potential research questions. In general, we have found that there are three ways to approach an approximation of development and plasticity. Two of these approaches are a forward mapping, and the third is an inverse mapping. The first is to use a correlation (or covariance) matrix approach, where all neural units in the nervous system are compared with every other neural unit. This results in pairwise comparisons that can lead to connectome network maps [13]. The second approach is to add nodes and arcs sequentially to a simple set of I/O connections. In this case, we get a more explicit network topology, and can observe phenomena such as preferential attachment. A third approach is to prune connections from a fully-formed network engaged in hard-wired behaviors. Using this approach, one can come to understand exactly which connections and neurons are essential for the execution of a behavior.

#### Software Instantiations
	Currently, there are three software instantiations of the software: BraGenBrain, modeling neural plasticity using multisensory inputs, and BVs as Deep Learning. We will now discuss their details and limitations.  


__BraGenBrain: a genetic algorithmic approach.__


__Modeling Neural Plasticity using Multisensory Inputs.__
	See references [14-20]


__BVs as Deep Learning.__

### Discussion and Future Plans



### References
[1] Kaiser, M. (2017). Mechanisms of Connectome Development. Trends in Cognitive Sciences, 21(9), P703-P717. doi:10.1016/j.tics.2017.05.010.

[2]  Alicea, B. (2017). The Emergent Connectome in Caenorhabditis elegans Embryogenesis. BioSystems, 173, 247-255. 

[3] Larson, S.D., Gleeson, P., & Brown, A.E.X. (2018). Connectome to behaviour: modelling Caenorhabditis elegans at cellular resolution. Philosophical Transactions of the Royal Society of London B, 373(1758), 20170366. doi:10.1098/rstb.2017.0366.

[4] Fan, X. & Markram, H. (2019). A Brief History of Simulation Neuroscience. Frontiers in Neuroinformatics, doi:10.3389/fninf.2019.00032.

[5] Braitenberg, V. (1984). Vehicles: experiments in synthetic Psychology. MIT Press, Cambridge, MA.

[6] Seung, S. (2009). Connectome: How the Brain's Wiring Makes Us Who We Are. Houghton-Mifflin, Boston.

[7] Stiles, J. & Jernigan, T.L. (2010). The Basics of Brain Development. Neuropsychological Review, 20(4), 327–348. doi:10.1007/s11065-010-9148-4.

[8] Smith, J.L. and Schoenwolf, G.C. (1997). Neurulation: coming to closure. Trends in Neurosciences, 20(11), P510-P517. doi:10.1016/S0166-2236(97)01121-1.

[9] Eichler, K., Li, F., Litwin-Kumar, A., Park, Y., Andrade, I., Schneider-Mizell, C.M., Saumweber, T., Huser, A., Eschbach, C., Gerber, B., Fetter, R.D., Truman, J.W., Priebe, C.E., Abbott, L.F., Thum, A.S., Zlatic, M., & Cardona, A. (2017). The complete connectome of a learning and memory centre in an insect brain. Nature, 548(7666), 175-182. doi:10.1038/nature23455

[10] Craddock, R.C., Tungaraza, R.L., & Milham, M.P. (2015). Connectomics and new approaches for analyzing human brain functional connectivity. Gigascience, 4, 13.

[11] Towlson, E.K., Vertes, P.E., Ahnertm, S.E., Schafer, W.R., & Bullmore, E.T. (2013). The rich club of the C. elegans neuronal connectome. Journal of Neuroscience, 33(15), 6380-6387. doi:10.1523/JNEUROSCI.3784-12.2013.

[12] Sabrin, K.M & Dovrolis, C. (2017). The hourglass effect in hierarchical dependency networks. Network Science, 5(4), 490-528.

[13] Henriksen, S., Pang, R., & Wronkiewicz, M. (2016). A simple generative model of the mouse mesoscale connectome. eLife, 5, e12366. doi:10.7554/eLife.12366.

[14] Li, Z. & Hopfield, J.J. (1989). Modeling the Olfactory Bulb and its Neural Oscillatory Processings. BioI. Cybern. 61, 379-392.

[15] Nagayama, S., Enerva, A., Fletcher, M. L., Masurkar, A.V., Igarashi, K.M., Mori, K., & Chen, W.R. (2010). Differential axonal projection of mitral and tufted cells in the mouse main olfactory system. Frontiers in Neural Circuits 4(120). doi: 10.3389/fncir.2010.00120.

[16] Sanger, T. D. (1989). Optimal Unsupervised Learning in a Single-Layer Linear Feedforward Neural Network. Neural Networks 2, 459-473.

[17] Smith, D.V. & St John, S. J. (1999). Neural coding of gustatory information. Current Opinion in Neurobiology, 9, 427-435.

[18] Shepherd, G.M., Chen, Willhite, W.R. D., Migliore, M., & Greer C.A. (2007). The olfactory granule cell: From classical enigma to central role in olfactory processing. Brain Research Reviews, 55, 373-382.

[19] Soh, Z., Nishikawa, S., Kurita, Y., Takiguchi, N., & Tsuji, T. (2016). A Mathematical Model of the Olfactory Bulb for the Selective Adaptation Mechanism in the Rodent Olfactory System. PLoS One, 11(12), e0165230. doi: 10.1371/journal.

[20] Wu, A., Dvoryanchikov, G., Pereira, E., Chaudhari, N., & Roper, S. D. (2015). Breadth of tuning in taste afferent neurons varies with stimulus strength. Nature Communications, 6, 8171. doi: 10.1038/ncomms9171.
