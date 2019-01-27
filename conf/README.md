# **conf** folder

- Configuration files (experimental setup) should be stored here.  
- Run by invoking ">> java app.Simuation conf/testing.conf"


# File Format
```
N=<value of N>
initResources=<number of resources to endow each firm at init>
numFirms=<number of firms in ecosystem>
influenceMatrixFile=<name of the influence matrix file; in the inf folder>
iterations=<number of simulated time ticks to run>
innovation=<extent of innovation; [0..1]>
resourcesIncrement=<number of resources to add; integer greater or equal to 1>
(defunct)search={experiential, exhaustive}
searchScope=[1..2] NOT YET IMPLEMENTED
resourceDecision={abs, rel}
resourceThreshold=<minimum absolute increment in fitness required to adopt new resources or drop existing resources>
searchThreshold=<minimum absolute increment in fitness required to adopt new configuration through search>
outfile=<name of output file; if omitted, STDOUT will be used>
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
initResources=3
numFirms=1
influenceMatrixFile=matrix16-3
iterations=3
innovation=0.0
resourcesIncrement=1
searchScope=1
resourceDecision=abs
resourceThreshold=0.5
searchThreshold=0.1
outfile=testing.txt
```