# **in** folder

- Configuration files (experimental setup) should be stored here.  
- Run by invoking ">> java java.app.Simuation in/testing.in"


# File Format
```
N=<value of N>
influenceMatrixFile=<name of the influence matrix file; in the inf folder>
iterations=<number of simulated time ticks to run>
outfile=<name of output file; if omitted, STDOUT will be used>
firms=<semi-colon (;) separated list of firm types; each firm type is characterized as a comma-separated list consisting of numFirms,initResources,innovation,resourcesIncrement,searchScope,searchThreshold,resourceDecision,resourceThreshold>
(defunct)initResources=<number of resources to endow each firm at init>
(defunct)numFirms=<number of firms in ecosystem>
(defunct)innovation=<extent of innovation; [0..1]>
(defunct)resourcesIncrement=<number of resources to add; integer greater or equal to 1>
(defunct)(defunct)search={experiential, exhaustive}
(defunct)searchScope=[1..]
(defunct)resourceDecision={abs, rel}
(defunct)resourceThreshold=<minimum absolute increment in fitness required to adopt new resources or drop existing resources>
(defunct)searchThreshold=<minimum absolute increment in fitness required to adopt new configuration through search>
(defunct)adaptation={search resources}
```

# Parameter explanation
```
N : total number of resources that can be manipulated 
initResources : scope of resources a firm manipulates; greater scope more room for innovation
numFirms : number of firms in ecosystem
influenceMatrixFile : sets the overall complexity of the innovation ecosystem; interdependencies among resources.  This acts as a parameter for digitization -- digitization creates greater interdependencies among resources
innovation : likelihood that during search a firm adapts by adopting a new resource
resourcesIncrement : number of new resources (up to) to adopt 
(defunct)search : experiential vs. exhaustive search.  Now we only implement experiential search -- no need to set this parameter
searchScope : max number of permutations of resource configurations to consider in search -- e.g., 1: incremental; >1: possibility of long jump
resourceDecision : whether resource add/drop decisions follow an absolute threshold or a relative threshold.  If absolute, then increase/decrease in fitness must be greater/less than resourceThreshold (parameter) in order for the firm to adopt/drop the resource
resourceThreshold : minimum / maximum increase / decrease in fitness required for firms to adopt / drop a new resource
searchThreshold : minimum increase in fitness required for firms to adopt a new configuration through search
```

# Default Values
```
N=16
influenceMatrixFile=matrix16-3
outfile=testing.txt
iterations=3
firms=1,3,0.0,1,1,0.1,abs,0.5;1,10,0.0,1,5,0.1,abs,0.5

i.e.,: 
firm type 1:
	numFirms=1
	initResources=3
	innovation=0.0
	resourcesIncrement=1
	searchScope=1
	searchThreshold=0.1
	resourceDecision=abs
	resourceThreshold=0.5
firm type 2:
	numFirms=1
	initResources=10
	innovation=0.0
	resourcesIncrement=1
	searchScope=5
	searchThreshold=0.1
	resourceDecision=abs
	resourceThreshold=0.5
```
