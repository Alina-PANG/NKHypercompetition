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
digitization=<extent of digitization; [0..1]>
resourcesIncrement=<number of resources to add; integer greater or equal to 1>
search={experiential, exhaustive}
searchScope=[1..2] NOT YET IMPLEMENTED
outfile=<name of output file; if omitted, STDOUT will be used>
(defunct)adaptation={search resources}
```

# Parameter explanation
```
N : total number of resources that can be manipulated 
initResources : scope of resources a firm manipulates; greater scope more room for innovation
numFirms : number of firms in ecosystem
influenceMatrixFile : sets the overall complexity of the innovation ecosystem; interdependencies among resources
digitization : likelihood that during search a firm adapts by adopting a new resource
resourcesIncrement : number of new resources (up to) to adopt 
search : experiential vs. exhaustive search
searchScope : number of permutations of resource configuraitons to consider in search -- e.g., 1: incremental; >1: long jump
```

# Default Values
```
N=20
initResources=4
numFirms=10
influenceMatrixFile=matrix12
iterations=100
digitization=0.0
resourcesIncrement=1
search=experiential
searchScope=1
outfile=output.txt
```