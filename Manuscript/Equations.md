$$\begin{aligned}
&O_{x, y, i} = \sum_k I_{o, i}^{(k)} \exp\left(\frac{d_{d, y}^{(k)}}{c d_{\max}}\right)\\
&G_{x, y, i} = \sum_k I_{g, i}^{(k)} \Theta (d' - d_{x, y}^{(k)})
\end{aligned} \tag{2}$$
where $O_{x, y, i}$ is the $i$th olfactory feature sensible at position $(x, y)$, and $I_{o, i}^{(k)}$ is the $i$th feature of the odor omitted by stimulus source $k$; similarly, $G$ and $I_g$ are for gustatory features. $d_{d, y}^{(k)}$ is the Euclidean distance from $(x, y)$ to source $k$, while $d_{\max}$ is the maximum distance in the space, $d'$ is the gustatory sensible threshold, and $c$ is an arbitrary scalar. $\Theta$ is the standard Heaviside function.
 
**Li-Hopfield Network**  
$$\begin{aligned}
&\frac{\mathrm{d} x}{\mathrm{d} t} = I + Lf_x(x) - Mf_y(y) - a_x x\\
&\frac{\mathrm{d} g}{\mathrm{d} t} =  I_c + Gf_x(x) - a_y y
\end{aligned}  \tag{3}$$
 where $x$ and $y$ are the internal states of mitral cells and granule cells. $M$, $G$, and $L$ are the weight matrices from granule to mitral, mitral to granule, and mitral to mitral, respectively. $f$ are activation functions, while $\Gamma$ is a function setting the lower triangular entries to zeros. $I$ is the input and $I_c$ is the constant ("center") input. $a$ is the time constant.
 
**Bidirectional Associate Memory Using Generalized Hebbian Algorithm with Depression**  
$$\begin{aligned} 
&\frac{\mathrm{d} W}{\mathrm{d} t} = \eta_t  I_o' I_{g^T} - W \Gamma(I_o' I_g^T) - D \\
&\{D_ij} = \frac{\phi}{I_{o,j}'W^2_{ij}+1}\\
&\lim_{t\rightarrow \infty} \eta_t = 0\\
&\lim_{t\rightarrow \infty} \sum_t \eta_t = \infty
\end{aligned}  \tag{4}$$

where $W$ is the association between $I_o'$, the processed olfactory input, and $I_g$, punished by the depression matrix $D$ with a depression rate $\phi$. $D_{ij} \mapsto 0$ if the denominator of $D_{ij}$ is zero.  

**Simple Judgement Unit**  
$$p = \sum_i J_i(I'_{g,i}) \tag{5}$$  

where $p$ is the preference from the summation of different judgement $J$ on recalled taste $I_g'$. If there is no recalled taste and real taste exists, $I'_g = I_g$.
